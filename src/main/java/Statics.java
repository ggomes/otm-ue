import checker.ConvergenceChecker;
import data.DemandAssignment;
import data.PathCost;
import model.ModelManager;
import update.UpdateFormula;

public class Statics {

    public static int MAX_ITERATIONS = 1000;

    public static DemandAssignment fixed_point_iteration(ModelManager model_manager, UpdateFormula update_formula, DemandAssignment h0, ConvergenceChecker checker){

        DemandAssignment h = h0;
        PathCost c, cprev = null;
        DemandAssignment hprev = h0;
        boolean converged = false;
        int i=0;

        while(!converged && i<MAX_ITERATIONS){

            c = model_manager.run(hprev);
            h = update_formula.evaluate(c);

            converged = checker.eval(h,hprev,c,cprev);

            hprev = h;
            cprev = c;
            i++;
        }

        return h;

    }

}
