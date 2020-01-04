package at.pwd.model;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.primitives.Pair;

public class Model {

    private static int cnn_filter_num = 128;
    private static int cnn_filter_size = 3;
    private static int res_layer_num = 2;
    private static double l2_reg = 1e-4;
    private static int value_fc_size = 256;
    private static String residualName = "Res";

    ComputationGraphConfiguration model;

    public Model() {
        ComputationGraphConfiguration.GraphBuilder builder = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.01)) // SGD(lr=1e-2, momentum=0.9)
                .graphBuilder()
                .addInputs("Own", "Enemy")
                .addLayer("Conv1", new Convolution1D.Builder()
                        .nOut(cnn_filter_num)
                        .kernelSize(cnn_filter_size)
                        .convolutionMode(ConvolutionMode.Same)
                        .l2(l2_reg)
                        .build(), "Own", "Enemy")
                .addLayer("BatchNorm1", new BatchNormalization.Builder()
                        .build(), "Conv1")
                .addLayer("Activation1", new ActivationLayer(Activation.RELU), "BatchNorm1");

        for (int i = 0; i < res_layer_num; i++) {
            if (i == 0) {
                buildResidualBlock(builder, i, "Activation1");
            } else {
                buildResidualBlock(builder, i, "Activation2_" + residualName + (i - 1));
            }
        }

        builder.addLayer("ResOut", new OutputLayer(), "Activation2_" + residualName + (res_layer_num - 1));

        // Policy Head
        builder.addLayer("ConvPol", new Convolution1D.Builder()
                .nOut(2)
                .kernelSize(1)
                .l2(l2_reg)
                .build(), "ResOut")
                .addLayer("BatchNormPol", new BatchNormalization.Builder()
                        .build(), "ConvPol")
                .addLayer("ActivationPol", new ActivationLayer(Activation.RELU), "BatchNormPol")
                .addLayer("DensePol", new DenseLayer.Builder().nOut(6).l2(l2_reg).activation(Activation.SOFTMAX).build(), new CnnToFeedForwardPreProcessor(), "ActivationPol")
                .addLayer("PolicyOut", new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.MSE).build(), "DensePol");

        // Value Head
        builder.addLayer("ConvVal", new Convolution1D.Builder()
                .nOut(1)
                .kernelSize(1)
                .l2(l2_reg)
                .build(), "ResOut")
                .addLayer("BatchNormVal", new BatchNormalization.Builder()
                        .build(), "ConvVal")
                .addLayer("ActivationVal", new ActivationLayer(Activation.RELU), "BatchNormVal")
                .addLayer("DenseVal1", new DenseLayer.Builder().nOut(value_fc_size).l2(l2_reg).activation(Activation.RELU).build(),"ActivationVal")
                .addLayer("DenseVal2", new DenseLayer.Builder().nOut(1).l2(l2_reg).activation(Activation.TANH).build(), new CnnToFeedForwardPreProcessor(), "DenseVal1")
                .addLayer("ValueOut", new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).build(), "DenseVal2");

        this.model = builder.setOutputs("PolicyOut", "ValueOut").build();
    }

    private void buildResidualBlock(ComputationGraphConfiguration.GraphBuilder x, int num, String input) {
        String name = residualName + num;
        x.addLayer("OutputInX_" + name, new OutputLayer(), input);
        x.addLayer("Conv1_" + name, new Convolution1D.Builder()
                .nOut(cnn_filter_num)
                .kernelSize(cnn_filter_size)
                .convolutionMode(ConvolutionMode.Same)
                .l2(l2_reg)
                .build(), "OutputInX_" + name)
                .addLayer("BatchNorm1_" + name, new BatchNormalization.Builder()
                        .build(), "Conv1_" + name)
                .addLayer("Activation1_" + name, new ActivationLayer(Activation.RELU), "BatchNorm1_" + name)
                .addLayer("Conv2_" + name, new Convolution1D.Builder()
                        .nOut(cnn_filter_num)
                        .kernelSize(cnn_filter_size)
                        .convolutionMode(ConvolutionMode.Same)
                        .l2(l2_reg)
                        .build(), "Activation1_" + name)
                .addLayer("BatchNorm2_" + name, new BatchNormalization.Builder()
                        .build(), "Conv2_" + name)
                .addInputs()
                .addLayer("OutputX_" + name, new OutputLayer(), "BatchNorm2_" + name)
                .addVertex("Add_" + name, new MergeVertex(), "OutputInX_" + name, "OutputX_" + name)
                .addLayer("Activation2_" + name, new ActivationLayer(Activation.RELU), "Add_" + name);
    }

//    private class PolicyLoss implements ILossFunction {
//
//        private INDArray scoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
//            INDArray output = activationFn.getActivation(preOutput.dup(), true);
//
//            INDArray negLabels = labels.dup().mul(-1);
//            INDArray log = Transforms.log(output.add(1e-07));
//            negLabels.muli(log);
//            if (mask != null) {
//                negLabels.muliColumnVector(mask);
//            }
//            return negLabels;
//        }
//
//        @Override
//        public double computeScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
//            INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
//
//            double score = scoreArr.sumNumber().doubleValue();
//
//            if (average) {
//                score /= scoreArr.size(0);
//            }
//
//            return score;
//        }
//
//        @Override
//        public INDArray computeScoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
//            INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
//            return scoreArr.sum(1);
//        }
//
//        @Override
//        public INDArray computeGradient(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
//            INDArray output = activationFn.getActivation(preOutput.dup(), true);
//        /*
//        //NOTE: There are many ways to do this same set of operations in nd4j
//        //The following is the most readable for the sake of this example, not necessarily the fastest
//        //Refer to the Implementation of LossL1 and LossL2 for more efficient ways
//        */
//            INDArray yMinusyHat = labels.sub(output);
//            INDArray dldyhat = yMinusyHat.mul(-2).sub(Transforms.sign(yMinusyHat)); //d(L)/d(yhat) -> this is the line that will change with your loss function
//
//            //Everything below remains the same
//            INDArray dLdPreOut = activationFn.backprop(preOutput.dup(), dldyhat).getFirst();
//            //multiply with masks, always
//            if (mask != null) {
//                dLdPreOut.muliColumnVector(mask);
//            }
//
//            return dLdPreOut;
//        }
//
//        @Override
//        public Pair<Double, INDArray> computeGradientAndScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
//            return new Pair<>(
//                    computeScore(labels, preOutput, activationFn, mask, average),
//                    computeGradient(labels, preOutput, activationFn, mask));
//        }
//
//        @Override
//        public String name() {
//            return "PolicyLoss";
//        }
//    }
}
