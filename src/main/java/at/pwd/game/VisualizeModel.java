package at.pwd.game;

import at.pwd.model.Model;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.*;

import javax.xml.stream.Location;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class VisualizeModel {
    public static void main(String[] args) {
        SavedModelBundle savedModelBundle =
                SavedModelBundle.load("/home/kathi/Documents/Strategy-Game-Programming/src/main/resources/tf_safe", "serve");
        Graph graph = savedModelBundle.graph();
        Iterator<Operation> it = graph.operations();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        List<Tensor<?>> results = savedModelBundle.session().runner()
                .feed("serving_default_input_1:0", new State().getStateForModel())
                .fetch("StatefulPartitionedCall",0)
                .fetch("StatefulPartitionedCall",1)
                .run();

        float[][] probs = new float[1][6];
        results.get(0).copyTo(probs);

        float[][] value = new float[1][1];
        results.get(1).copyTo(value);

        System.out.println("Probabilities: " + Arrays.toString(probs[0]));
        System.out.println("Value: "+Arrays.toString(value[0]));
    }
}
