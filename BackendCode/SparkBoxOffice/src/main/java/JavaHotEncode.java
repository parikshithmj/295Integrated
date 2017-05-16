import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.regression.DecisionTreeRegressor;
import org.apache.spark.sql.SparkSession;

// $example on$
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.execution.columnar.ObjectColumnStats;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.ml.feature.VectorIndexerModel;



public class JavaHotEncode {


        public static void main(String[] args) {

            JavaSparkContext sc = new JavaSparkContext();
            SparkSession spark = SparkSession
                    .builder()
                    .appName("Attempt")
                    .getOrCreate();

          //  Dataset<Row> dataset = spark.read().csv("par.csv");
            Dataset<Row> dataset = spark.read().csv("movie_metadata_old.csv");


            dataset.printSchema();
            dataset.show();


            StringIndexerModel indexer = new StringIndexer()
                    .setInputCol("_c7")
                    .setOutputCol("DayPar")
                    .fit(dataset);
            Dataset<Row> indexed = indexer.transform(dataset);

            indexed.show();


            Dataset<Row>[] splits = dataset.randomSplit(new double[]{0.7, 0.3});
            Dataset<Row> trainingData = splits[0];

            Dataset<Row> testData = splits[1];

            // Train a DecisionTree model.
            DecisionTreeRegressor dt = new DecisionTreeRegressor()
                    .setFeaturesCol("DayPar");

            // Chain indexer and tree in a Pipeline.
            Pipeline pipeline = new Pipeline()
                    .setStages(new PipelineStage[]{indexer, dt});

            // Train model. This also runs the indexer.
            PipelineModel model = pipeline.fit(trainingData);

            // Make predictions.
            Dataset<Row> predictions = model.transform(testData);

            // Select example rows to display.
            predictions.select("label", "features").show(5);


        }


}
