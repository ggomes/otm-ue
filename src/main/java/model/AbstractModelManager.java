package model;

import commodity.Path;
import data.DemandAssignment;
import data.ODPair;
import data.PathCost;

import java.util.List;
import java.util.Map;

public abstract class AbstractModelManager {
    public Map<ODPair, List<Path>> path_map;
    public abstract PathCost run(DemandAssignment demand_assignment);
    public abstract DemandAssignment get_initial_assigment();
}
