package data;

import commodity.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandAssignment {

    Map<Long, List<Double>> values;

    public void add_demand(Path path, List<Double> path_demand){
        if(values==null)
            values = new HashMap<>();
        values.put(path.getId(),path_demand);
    }

}
