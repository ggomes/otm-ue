import checker.ConvergenceChecker;
import checker.L2_assignment_error;
import data.DemandAssignment;
import model.AbstractModelManager;
import model.OTMModelManager;
import org.junit.Test;
import update.ExtraProjectionMethod;
import update.UpdateFormula;

public class Tests {

    @Test
    public void run_dta() {

        try {

            float sim_dt = 2f;
            float duration = 3600f;
            float sampling_dt = 300f;
            String config_name = "config/seven_links.xml";
            String global_model = "ctm";

            // create the model manager: loads the scenario and OD data
            AbstractModelManager model_manager = new OTMModelManager(config_name,sim_dt,duration,sampling_dt,global_model);

            DemandAssignment h0 = model_manager.get_initial_assigment();

            UpdateFormula update_formula = new ExtraProjectionMethod();
            ConvergenceChecker checker = new L2_assignment_error();

            // run the methos
            DemandAssignment hstar = Statics.fixed_point_iteration(model_manager,update_formula,h0,checker);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
