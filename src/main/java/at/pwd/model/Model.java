package at.pwd.model;

import at.pwd.game.State;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;

public class Model {

    private static final int CNN_FILTER_NUM = 128;
    private static final int CNN_FILTER_SIZE = 3;
    private static final int RES_LAYER_NUM = 2;
    private static final double L2_REG = 1e-4;
    private static final int VALUE_FC_SIZE = 256;
    private static final String RESIDUAL_NAME = "Res";

    private ComputationGraph model;

    public INDArray[] predict(State state){
        return model.output(state.getStateForModel());
    }

    public Model() {
        model = new ComputationGraph(createConfig());
        model.init();
    }

    private ComputationGraphConfiguration createConfig() {
        ComputationGraphConfiguration.GraphBuilder builder = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.01)) // SGD(lr=1e-2, momentum=0.9)
                .graphBuilder()
                .addInputs("Own", "Enemy")
                // .setInputTypes(InputType.inf)
                .addLayer("Conv1", new Convolution1D.Builder()
                        .nIn(14)
                        .nOut(CNN_FILTER_NUM)
                        .kernelSize(CNN_FILTER_SIZE)
                        .convolutionMode(ConvolutionMode.Same)
                        .l2(L2_REG)
                        .build(), "Own", "Enemy")
                .addLayer("BatchNorm1", new BatchNormalization.Builder()
                        .nIn(CNN_FILTER_NUM)
                        .nOut(CNN_FILTER_NUM)
                        .build(), "Conv1")
                .addLayer("Activation1", new ActivationLayer(Activation.RELU), "BatchNorm1");

        for (int i = 0; i < RES_LAYER_NUM; i++) {
            if (i == 0) {
                buildResidualBlock(builder, i, "Activation1");
            } else {
                buildResidualBlock(builder, i, "Activation2_" + RESIDUAL_NAME + (i - 1));
            }
        }

        builder.addLayer("ResOut", new OutputLayer.Builder().nIn(CNN_FILTER_NUM*2).nOut(CNN_FILTER_NUM*2).build(), "Activation2_" + RESIDUAL_NAME + (RES_LAYER_NUM - 1));

        // Policy Head
        builder.addLayer("ConvPol", new Convolution1D.Builder()
                .nIn(CNN_FILTER_NUM*2)
                .nOut(2)
                .kernelSize(1)
                .l2(L2_REG)
                .build(), "ResOut")
                .addLayer("BatchNormPol", new BatchNormalization.Builder().nIn(2).nOut(2)
                        .build(), "ConvPol")
                .addLayer("ActivationPol", new ActivationLayer(Activation.RELU), "BatchNormPol")
                .addLayer("DensePol", new DenseLayer.Builder().nIn(2).nOut(6).l2(L2_REG).activation(Activation.SOFTMAX).build(), new CnnToFeedForwardPreProcessor(), "ActivationPol")
                .addLayer("PolicyOut", new OutputLayer.Builder().nIn(6).nOut(6).lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).build(), "DensePol");

        // Value Head
        builder.addLayer("ConvVal", new Convolution1D.Builder()
                .nIn(CNN_FILTER_NUM*2)
                .nOut(1)
                .kernelSize(1)
                .l2(L2_REG)
                .build(), "ResOut")
                .addLayer("BatchNormVal", new BatchNormalization.Builder().nIn(1).nOut(1)
                        .build(), "ConvVal")
                .addLayer("ActivationVal", new ActivationLayer(Activation.RELU), "BatchNormVal")
                .addLayer("DenseVal1", new DenseLayer.Builder().nIn(1).nOut(VALUE_FC_SIZE).l2(L2_REG).activation(Activation.RELU).build(),"ActivationVal")
                .addLayer("DenseVal2", new DenseLayer.Builder().nIn(VALUE_FC_SIZE).nOut(1).l2(L2_REG).activation(Activation.TANH).build(), new CnnToFeedForwardPreProcessor(), "DenseVal1")
                .addLayer("ValueOut", new OutputLayer.Builder().nIn(1).nOut(1).lossFunction(LossFunctions.LossFunction.MSE).build(), "DenseVal2");

        return builder.setOutputs("PolicyOut", "ValueOut").build();
    }

    private void buildResidualBlock(ComputationGraphConfiguration.GraphBuilder x, int num, String input) {
        String name = RESIDUAL_NAME + num;
        x.addLayer("OutputInX_" + name, new OutputLayer.Builder().nIn(CNN_FILTER_NUM).nOut(CNN_FILTER_NUM).build(), input);
        x.addLayer("Conv1_" + name, new Convolution1D.Builder()
                .nIn(CNN_FILTER_NUM)
                .nOut(CNN_FILTER_NUM)
                .kernelSize(CNN_FILTER_SIZE)
                .convolutionMode(ConvolutionMode.Same)
                .l2(L2_REG)
                .build(), "OutputInX_" + name)
                .addLayer("BatchNorm1_" + name, new BatchNormalization.Builder().nIn(CNN_FILTER_NUM).nOut(CNN_FILTER_NUM)
                        .build(), "Conv1_" + name)
                .addLayer("Activation1_" + name, new ActivationLayer(Activation.RELU), "BatchNorm1_" + name)
                .addLayer("Conv2_" + name, new Convolution1D.Builder()
                        .nIn(CNN_FILTER_NUM)
                        .nOut(CNN_FILTER_NUM)
                        .kernelSize(CNN_FILTER_SIZE)
                        .convolutionMode(ConvolutionMode.Same)
                        .l2(L2_REG)
                        .build(), "Activation1_" + name)
                .addLayer("BatchNorm2_" + name, new BatchNormalization.Builder().nIn(CNN_FILTER_NUM).nOut(CNN_FILTER_NUM)
                        .build(), "Conv2_" + name)
                .addInputs()
                .addLayer("OutputX_" + name, new OutputLayer.Builder().nIn(CNN_FILTER_NUM).nOut(CNN_FILTER_NUM).build(), "BatchNorm2_" + name)
                .addVertex("Add_" + name, new MergeVertex(), "OutputInX_" + name, "OutputX_" + name)
                .addLayer("Activation2_" + name, new ActivationLayer(Activation.RELU), "Add_" + name);
    }


}
