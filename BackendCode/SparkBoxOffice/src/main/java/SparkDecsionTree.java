
import com.mongodb.DBObject;
import com.mongodb.spark.api.java.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.avro.generic.GenericData;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

//import com.mongodb.spark.MongoSpark;


public class SparkDecsionTree {

     public static void main(String[] args) {
       
            SparkSession spark = SparkSession
                    .builder()
                    .appName("JavaDecisionTreeClassificationExample").config("spark.executor.memory", "512m")
                    .getOrCreate();

            // $example on$
            // Load the data stored in LIBSVM format as a DataFrame.
            Dataset<Row> data = spark
                    .read()
                    .format("libsvm")
                    .load("/opt/tomcat/webapps/boxofficewebapi-0.0.1-SNAPSHOT/WEB-INF/classes/libsvmEncoded_25k.txt");

         Dataset<Row> dataTest = spark
                 .read()
                 .format("libsvm")
                 .load("/opt/tomcat/webapps/boxofficewebapi-0.0.1-SNAPSHOT/WEB-INF/classes/testSvm.txt").toDF();


         // Index labels, adding metadata to the label column.
            // Fit on whole dataset to include all labels in index.
            StringIndexerModel labelIndexer = new StringIndexer()
                    .setInputCol("label")
                    .setOutputCol("indexedLabel")
                    .fit(data);

            // Automatically identify categorical features, and index them.
            VectorIndexerModel featureIndexer = new VectorIndexer()
                    .setInputCol("features")
                    .setOutputCol("indexedFeatures")
                    .setMaxCategories(20) // features with > 4 distinct values are treated as continuous.
                    .fit(data);

            
            Dataset<Row>[] splits = data.randomSplit(new double[]{0.7, 0.3});
            Dataset<Row> trainingData = splits[0];
            Dataset<Row> testData = splits[1];

   

        RandomForestClassifier dt = new RandomForestClassifier()
                .setLabelCol("indexedLabel")
                    .setFeaturesCol("indexedFeatures");


           
            IndexToString labelConverter = new IndexToString()
                    .setInputCol("prediction")
                    .setOutputCol("predictedLabel")
                    .setLabels(labelIndexer.labels());

            // Chain indexers and tree in a Pipeline.
            Pipeline pipeline = new Pipeline()
                    .setStages(new PipelineStage[]{labelIndexer, featureIndexer, dt, labelConverter});

            // Train model. This also runs the indexers.
            PipelineModel model = pipeline.fit(trainingData);

            // Make predictions.
            Dataset<Row> predictions = model.transform(testData);

            Dataset<Row> predictions_par = model.transform(dataTest);

    
         int cnt =0;
         System.out.print(predictions.select("predictedLabel").showString(1,0));
         predictions.select("predictedLabel").show(1);

         try {
       
         } catch (Exception e) {
             e.printStackTrace();
         }

         System.out.println(predictions.select("predictedLabel").showString(1,0));

            MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                    .setLabelCol("indexedLabel")
                    .setPredictionCol("prediction")
                    .setMetricName("accuracy");
            double accuracy = evaluator.evaluate(predictions);
       
        RandomForestClassificationModel rfModel = (RandomForestClassificationModel)(model.stages()[2]);

            spark.stop();
        }

}
