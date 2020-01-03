package at.pwd.model;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.*;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.learning.regularization.Regularization;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.LinkedList;
import java.util.List;

public class Model {
    public Model() {
        ComputationGraphConfiguration.GraphBuilder builder = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.01)) // SGD(lr=1e-2, momentum=0.9)
                .graphBuilder()
                .addLayer("Conv1", new Convolution1D.Builder(3)
                        .convolutionMode(ConvolutionMode.Same)
                        .nOut(16)
                        .build())
                .addLayer("BatchNormalization1", new BatchNormalization())
                .addLayer("Activation1", new ActivationLayer(Activation.RELU));
        buildResidualBlock(builder);
        // .addLayer("L1", new DenseLayer.Builder().nIn(3).nOut(4).build(), "input")
        // .addLayer("out1", new OutputLayer.Builder()
        //         .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
        //         .nIn(4).nOut(3).build(), "L1")
        // .addLayer("out2", new OutputLayer.Builder()
        //         .lossFunction(LossFunctions.LossFunction.MSE)
        //         .nIn(4).nOut(2).build(), "L1")
        // .setOutputs("out1", "out2");
    }

    private void buildResidualBlock(ComputationGraphConfiguration.GraphBuilder x, int i) {
        String name = "Residual" + i;
        x.addLayer(name + "Conv1", new Convolution1D.Builder(3)
                .convolutionMode(ConvolutionMode.Same)
                .nOut(16)
                .build())
                .addLayer(name+"BatchNormalization")
                .addLayer(name + "Add", new );
    }
}
