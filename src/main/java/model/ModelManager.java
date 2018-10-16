package model;

import data.DemandAssignment;
import data.PathCost;

public interface ModelManager {
    PathCost run(DemandAssignment demand_assignment);
    DemandAssignment get_initial_assigment();
}
