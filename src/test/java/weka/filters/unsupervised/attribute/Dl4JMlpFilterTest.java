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
 * Dl4jMlpFilterTest.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import static org.junit.Assert.assertEquals;
import static weka.util.TestUtil.saveClf;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.dl4j.iterators.instance.ImageInstanceIterator;
import weka.dl4j.layers.ConvolutionLayer;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.Layer;
import weka.dl4j.layers.OutputLayer;
import weka.dl4j.layers.SubsamplingLayer;
import weka.filters.Filter;
import weka.util.DatasetLoader;
import weka.util.TestUtil;

/**
 * JUnit tests for Dl4jMlpFilter.
 *
 * @author Steven Lang
 */
public class Dl4JMlpFilterTest {

  @Test
  public void testProcessIris() throws Exception {
    Dl4jMlpClassifier clf = new Dl4jMlpClassifier();
    clf.setNumEpochs(1);
    Instances iris = DatasetLoader.loadIris();

    DenseLayer dl1 = new DenseLayer();
    dl1.setLayerName("l1");
    dl1.setNOut(10);

    DenseLayer dl2 = new DenseLayer();
    dl2.setLayerName("l2");
    dl2.setNOut(5);

    DenseLayer dl3 = new DenseLayer();
    dl3.setLayerName("l3");
    dl3.setNOut(10);

    OutputLayer ol = new OutputLayer();
    clf.setLayers(dl1, dl2, dl3, ol);
    clf.buildClassifier(iris);

    String tmpDir = System.getProperty("java.io.tmpdir");
    String clfPath = Paths.get(tmpDir, "dl4j-mlp-clf.ser").toString();
    saveClf(clfPath, clf);

    checkLayer(clf, iris, dl1.getLayerName(), clfPath, false);
    checkLayer(clf, iris, dl2.getLayerName(), clfPath, false);
    checkLayer(clf, iris, dl3.getLayerName(), clfPath, false);

    Files.delete(Paths.get(clfPath));
  }

  @Test
  public void testProcessMnist() throws Exception {
    // Init data
    Instances dataMnist = DatasetLoader.loadMiniMnistMeta();
    ImageInstanceIterator idiMnist = DatasetLoader.loadMiniMnistImageIterator();
    idiMnist.setTrainBatchSize(TestUtil.DEFAULT_BATCHSIZE);

    Dl4jMlpClassifier clf = new Dl4jMlpClassifier();
    clf.setInstanceIterator(idiMnist);
    clf.setNumEpochs(1);

    ConvolutionLayer cl1 = new ConvolutionLayer();
    cl1.setNOut(4);

    SubsamplingLayer ssl = new SubsamplingLayer();

    ConvolutionLayer cl2 = new ConvolutionLayer();
    cl2.setNOut(2);

    OutputLayer ol = new OutputLayer();
    Layer[] layers = {cl1, ssl, cl2, ol};
    clf.setLayers(layers);
    clf.buildClassifier(dataMnist);

    String tmpDir = System.getProperty("java.io.tmpdir");
    String clfPath = Paths.get(tmpDir, "dl4j-mlp-clf.ser").toString();
    saveClf(clfPath, clf);

    for (Layer layer : layers) {
      checkLayer(clf, dataMnist, layer.getLayerName(), clfPath, false);
    }
  }

  protected void checkLayer(Dl4jMlpClassifier clf, Instances iris, String transformationLayerName,
      String clfPath, boolean useZooModel) throws Exception {
    Instances activationsExpected = clf.getActivationsAtLayer(transformationLayerName, iris);
    Dl4jMlpFilter filter = new Dl4jMlpFilter();
    filter.setSerializedModelFile(new File(clfPath));
    filter.setTransformationLayerName(transformationLayerName);
    filter.setInputFormat(iris);
    filter.setUseZooModel(useZooModel);

    Instances activationsActual = Filter.useFilter(iris, filter);

    for (int i = 0; i < activationsActual.size(); i++) {
      Instance expected = activationsExpected.get(i);
      Instance actual = activationsActual.get(i);
      for (int j = 0; j < expected.numAttributes(); j++) {
        assertEquals(expected.value(j), actual.value(j), 1e-6);
      }
    }
  }
}