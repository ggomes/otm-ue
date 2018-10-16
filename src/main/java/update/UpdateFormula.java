package update;

import data.DemandAssignment;
import data.PathCost;

public interface UpdateFormula {
    DemandAssignment evaluate(PathCost c);
}
