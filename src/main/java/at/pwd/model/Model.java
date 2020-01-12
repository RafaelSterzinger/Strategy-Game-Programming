package at.pwd.model;

import at.pwd.game.State;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;

import javax.security.auth.login.Configuration;
import java.io.IOException;

public class Model {

    private ComputationGraph value;
    private ComputationGraph policy;
    private ComputationGraph model;

    public Model() {
        this.model = DualResnetModel.getModel(2, 1);
        // String value = null;
        // try {
        //     value = new ClassPathResource("value.h5").getFile().getPath();
        //     this.value = KerasModelImport.importKerasModelAndWeights(value);
        //     this.value.init();
        //     String policy = new ClassPathResource("policy.h5").getFile().getPath();
        //     this.policy = KerasModelImport.importKerasModelAndWeights(policy);
        //     this.policy.init();
        // } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
        //     e.printStackTrace();
        // }
    }

    public INDArray[] predict(State state) {
        // return value.output(state.getStateForModel());
        return model.output(state.getStateForModel());
    }

//    public void fit(INDArray[] input, INDArray policy, INDArray value) {
//        model.fit(input, new INDArray[]{policy, value});
//    }
}
