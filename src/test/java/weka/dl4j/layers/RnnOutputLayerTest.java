/*
 * WekaDeeplearning4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WekaDeeplearning4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WekaDeeplearning4j.  If not, see <https://www.gnu.org/licenses/>.
 *
 * RnnOutputLayerTest.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, New Zealand
 */

package weka.dl4j.layers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import weka.dl4j.lossfunctions.LossBinaryXENT;

/**
 * An output layer test.
 *
 * @author Steven Lang
 */
public class RnnOutputLayerTest extends AbstractFeedForwardLayerTest<RnnOutputLayer> {

  @Override
  public RnnOutputLayer getApiWrapper() {
    return new RnnOutputLayer();
  }

  @Test
  public void testLossFunction() {
    LossBinaryXENT loss = new LossBinaryXENT();
    wrapper.setLossFn(loss);

    assertEquals(loss, wrapper.getLossFn());
  }
}
