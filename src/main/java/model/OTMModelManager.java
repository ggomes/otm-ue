package model;

import api.APIopen;
import api.info.CommodityInfo;
import api.info.SubnetworkInfo;
import commodity.Path;
import commodity.Subnetwork;
import data.DemandAssignment;
import data.ODMatrix;
import data.ODPair;
import data.PathCost;
import error.OTMException;
import keys.KeyCommodityDemandTypeId;
import profiles.AbstractDemandProfile;
import runner.OTM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTMModelManager extends AbstractModelManager {

    public APIopen api;
    public ODMatrix od;
    public final float sample_dt = 300f;
    public final float duration = 3600f;
    public final int num_time_steps = (int) (duration/sample_dt);

    public OTMModelManager(String config_name,float sim_dt,float duration,float sampling_dt,String global_model) throws Exception {

        // load api .........................................................
        try {
            api = new APIopen(OTM.load(config_name,sim_dt,true,global_model));
        } catch (OTMException e) {
            e.printStackTrace();
        }

        // check single commodity
        if(api.api.get_commodities().size()!=1)
            throw new Exception("This code assumes a single commodity.");

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

            ODPair odpair = new ODPair(origin,destination);

            if(data.containsKey(odpair)){
                List<Double> existing = data.get(odpair);
                for(int i=0;i<num_time_steps;i++)
                    existing.set(i,existing.get(i) + profile.profile.get_value_for_time(i*sampling_dt));
            } else {
                List<Double> values = new ArrayList<>();
                for(int i=0;i<num_time_steps;i++)
                    values.add(profile.profile.get_value_for_time(i*sampling_dt));
                data.put(odpair,values);
            }

        }

        // save to an ODMatrix object .............................................
        od = new ODMatrix(data,sample_dt,num_time_steps);

        // path map ...............................................................
        path_map = new HashMap<>();
        for(ODPair odpair : data.keySet()){
            long origin = odpair.origin;
            long destination = odpair.destination;
            for(Subnetwork subnetwork : api.scenario().subnetworks.values()){
                if(subnetwork.is_path){
                    Path path = (Path) subnetwork;
                    if(path.get_origin().getId()==origin && path.get_destination().getId()==destination){
                        List<Path> curr_paths= path_map.containsKey(odpair) ? path_map.get(odpair): new ArrayList<>();
                        curr_paths.add(path);
                        path_map.put(odpair,curr_paths);
                    }
                }
            }
        }

    }

    @Override
    public PathCost run(DemandAssignment demand_assignment) {

        api.api.run(0,duration);

        // TODO!!!

        return null;
    }

    @Override
    public DemandAssignment get_initial_assigment() {

        DemandAssignment h = new DemandAssignment();

        for(Map.Entry<ODPair, List<Double>> e : od.data.entrySet()){
            ODPair odpair = e.getKey();
            List<Double> od_demand = e.getValue();

            int num_paths = path_map.get(odpair).size();

            List<Double> path_demand = new ArrayList<>();
            for(Double d : od_demand)
                path_demand.add(d/num_paths);

            for(Path path : path_map.get(odpair)){
                h.add_demand(path,path_demand);
            }
        }
        return h;
    }


}
