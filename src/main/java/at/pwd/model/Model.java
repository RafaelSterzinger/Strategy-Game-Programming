package at.pwd.model;


import at.pwd.game.State;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Model {

    private SavedModelBundle model;
    float[][] probs = new float[1][6];
    float[][] value = new float[1][1];

    public Model() {
        URL url = this.getClass().getClassLoader().getResource("tf_safe");
        if(url != null) {
            String path = url.getPath();
            this.model = SavedModelBundle.load(path, "serve");
        } else {
            System.out.println("Could not find model!");
        }
    }

    public void fit(State state) {
        List<Tensor<?>> results = model.session().runner()
                .feed("serving_default_input_1:0", new State().getStateForModel())
                .fetch("StatefulPartitionedCall", 0)
                .fetch("StatefulPartitionedCall", 1)
                .run();
        results.get(0).copyTo(probs);
        results.get(1).copyTo(value);
    }

    public float[] getQuality() {
        return probs[0];
    }

    public float getValue() {
        return value[0][0];
    }

}
