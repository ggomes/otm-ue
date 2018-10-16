package data;

import api.info.ODPair;

import java.util.List;
import java.util.Map;

public class ODMatrix {

    public Map<ODPair, List<Double>> data;
    public float sample_dt;
    public float num_time_steps;

    public ODMatrix(Map<ODPair, List<Double>> data,float sample_dt,float num_time_steps){
        this.data = data;
        this.sample_dt = sample_dt;
        this.num_time_steps = num_time_steps;
    }

}
