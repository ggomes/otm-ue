package model;

import api.APIopen;
import api.info.CommodityInfo;
import api.info.ODPair;
import api.info.SubnetworkInfo;
import data.DemandAssignment;
import data.ODMatrix;
import data.PathCost;
import error.OTMException;
import keys.KeyCommodityDemandTypeId;
import profiles.AbstractDemandProfile;
import runner.OTM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTMModelManager implements ModelManager {

    public APIopen api;
    public ODMatrix od;
    public float sample_dt;
    public int num_time_steps;


    public OTMModelManager(String config_name,float sim_dt,float duration,float sampling_dt,String global_model) throws Exception {

        // load api .........................................................
        try {
            api = new APIopen(OTM.load(config_name,sim_dt,true,global_model));
        } catch (OTMException e) {
            e.printStackTrace();
        }

        // sample_dt .........................................................
        if(api.scenario().data_demands.values().stream().mapToDouble(x->x.profile.dt).distinct().count()!=1)
            throw new Exception("not all demands have the same dt");

        this.sample_dt = (float) api.scenario().data_demands.values().stream()
                .mapToDouble(x->x.profile.dt)
                .distinct()
                .findFirst()
                .getAsDouble();

        // num_time_steps .....................................................
        if(api.scenario().data_demands.values().stream().mapToInt(x->x.profile.get_length()).distinct().count()!=1)
            throw new Exception("not all demands have the same length");

        this.num_time_steps = api.scenario().data_demands.values().stream()
                .mapToInt(x->x.profile.get_length())
                .distinct()
                .findFirst()
                .getAsInt();

        // load the OD matrix ..................................................
        Map<ODPair, List<Double>> data = new HashMap<>();

        for(Map.Entry<KeyCommodityDemandTypeId, AbstractDemandProfile> e : api.scenario().data_demands.entrySet()){
            KeyCommodityDemandTypeId state = e.getKey();
            AbstractDemandProfile profile = e.getValue();

            CommodityInfo commodity = api.api.get_commodity_with_id(state.commodity_id);

            if(!commodity.pathfull)
                continue;

            SubnetworkInfo path = api.api.get_subnetwork_with_id(state.link_or_subnetwork_id);
            List<Long> link_ids = path.get_link_ids();

            long origin = link_ids.get(0);
            long destination = link_ids.get(link_ids.size()-1);

            ODPair odpair = new ODPair(origin,destination,commodity.id);

            if(data.containsKey(odpair)){
                List<Double> existing = data.get(odpair);
                for(int i=0;i<existing.size();i++)
                    existing.set(i,existing.get(i) + profile.profile.get_ith_value(i));
            } else {
                data.put(odpair,new ArrayList<>(profile.profile.values));
            }

        }

        // save to an ODMatrix object .............................................
        od = new ODMatrix(data,sample_dt,num_time_steps);
    }

    @Override
    public PathCost run(DemandAssignment demand_assignment) {
        return null;
    }

    @Override
    public DemandAssignment get_initial_assigment() {
        return null;
    }


}
