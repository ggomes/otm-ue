package checker;

import data.DemandAssignment;
import data.PathCost;

public interface ConvergenceChecker {
    boolean eval(DemandAssignment h, DemandAssignment hprev, PathCost c,PathCost cprev);
}
