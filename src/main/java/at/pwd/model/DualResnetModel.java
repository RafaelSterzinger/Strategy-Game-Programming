
/*******************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package at.pwd.model;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;

/**
 * Define and load an AlphaGo Zero dual ResNet architecture
 * into DL4J.
 *
 * The dual residual architecture is the strongest
 * of the architectures tested by DeepMind for AlphaGo
 * Zero. It consists of an initial convolution layer block,
 * followed by a number (40 for the strongest, 20 as
 * baseline) of residual blocks. The network is topped
 * off by two "heads", one to predict policies and one
 * for value functions.
 *
 * @author Max Pumperla
 */
class DualResnetModel {

    /**
     * @param blocks amount of residual layers
     * @param numPlanes amount of channels, for Mancala it should be 14
     * @return
     */
    public static ComputationGraph getModel(int blocks, int numPlanes) {

        DL4JAlphaGoZeroBuilder builder = new DL4JAlphaGoZeroBuilder();
        String input = "in";

        builder.addInputs(input);
        String initBlock = "init";
        String convOut = builder.addConvBatchNormBlock(initBlock, input, 2, true);
        String towerOut = builder.addResidualTower(blocks, convOut);
        String policyOut = builder.addPolicyHead(towerOut, true);
        String valueOut = builder.addValueHead(towerOut, true);
        builder.addOutputs(policyOut, valueOut);

        ComputationGraph model = new ComputationGraph(builder.buildAndReturn());
        model.init();
        System.out.println(model.summary());

        //Initialize the user interface backend
        // UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        // StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        // uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains
        // model.setListeners(new StatsListener(statsStorage));

        return model;
    }
}