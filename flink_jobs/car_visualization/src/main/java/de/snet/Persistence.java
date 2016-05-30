package de.snet;

import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.MongoInputFormat;
import com.mongodb.hadoop.mapred.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapred.HadoopInputFormat;
import org.apache.flink.api.java.hadoop.mapred.HadoopOutputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Job to process the DAS1 and DAS2 data set, remove all unnecessary keys and save everything in one file.
 * Creates another file that stores all distinct deviceIDs.
 * @author MCMB
 * 
 */
public class Persistence {

	private static final Logger log = LoggerFactory.getLogger(Persistence.class);

	public static void main(String[] args) throws Exception {
		// set up the execution environment
		System.setProperty("hadoop.home.dir", "c:\\winutil\\");
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        HadoopInputFormat<BSONWritable, BSONWritable> hadoopIn;
        JobConf conf = new JobConf();
        hadoopIn = new HadoopInputFormat<BSONWritable, BSONWritable>(new MongoInputFormat(), BSONWritable.class, BSONWritable.class, conf);

        MongoOutputFormat<Text,BSONWritable> mongoOut = new MongoOutputFormat<Text,BSONWritable>();
//      mongoOut.getJobConf().set("mongo.input.uri", "mongodb://localhost:27017/dbname.collectioname");
//        hadoopIn.getJobConf().set("mongo.input.uri", "mongodb://flink:flink1234@ds019033.mlab.com:19033/ioscar.das");
//        MongoConfigUtil.setOutputURI(hadoopIn.getJobConf(), "mongodb://flink:flink1234@ds019033.mlab.com:19033/ioscar.das");
//        hadoopIn.getJobConf().set("mongo.input.uri", "mongodb://yellowtest:yellowmellow@ds019033.mlab.com:19033/ioscar.das");
//        MongoConfigUtil.setOutputURI(hadoopIn.getJobConf(), "mongodb://yellowtest:yellowmellow@ds019033.mlab.com:19033/ioscar.das");
//        hadoopIn.getJobConf().set("mongo.input.uri", "mongodb://yellowtest:yellow1234@ds019033.mlab.com:19033/ioscar.das");
//        hadoopIn.getJobConf().set("mongo.auth.uri", "mongodb://yellowtest:yellow1234@ds019033.mlab.com:19033/ioscar.das");
//        MongoConfigUtil.setOutputURI(hadoopIn.getJobConf(), "mongodb://yellowtest:yellow1234@ds019033.mlab.com:19033/ioscar.das");
//		hadoopIn.getJobConf().set("mongo.input.uri", "mongodb://flink:flink@127.0.0.1/das.das");
//        hadoopIn.getJobConf().set("mongo.auth.uri", "mongodb://yellowtest:yellow1234@ds019033.mlab.com:19033/ioscar.das");
//        MongoConfigUtil.setOutputURI(hadoopIn.getJobConf(), "mongodb://flink:flink@127.0.0.1/das.das");
		hadoopIn.getJobConf().set("mongo.input.uri", "mongodb://127.0.0.1/das.das");
//        hadoopIn.getJobConf().set("mongo.auth.uri", "mongodb://yellowtest:yellow1234@ds019033.mlab.com:19033/ioscar.das");
		MongoConfigUtil.setOutputURI(hadoopIn.getJobConf(), "mongodb://127.0.0.1/das.das");
        DataSet<Observation> union = env.fromCollection(generateObservations());
        DataSet<Tuple2<Text,BSONWritable>> data2Mongo = union.map(new ToMongoMap());
        /*
        store dataset in mongodb
         */
        data2Mongo.output(new HadoopOutputFormat<Text,BSONWritable>(mongoOut, hadoopIn.getJobConf()));

		JobExecutionResult result =  env.execute("Mongo Store");
		log.info(result.getAccumulatorResult("hasSpeed") + "/" + result.getAccumulatorResult("entries") + " have speed information.");
	}

	private static List<Observation> generateObservations(){
		ArrayList<Observation> observations = new ArrayList<Observation>();
		for (int i = 0; i<10; i++){
			Observation obs = new Observation();
			obs.deviceID = i;
			obs.trip = i;
			obs.time = i;
			obs.headingGPS = Double.valueOf(i);
//			obs.latitudeGPS = Double.valueOf(i);
//			obs.longitudeGPS = Double.valueOf(i);
//            obs.point2d = new Double[2];
//			obs.point2d = new de.snet.Point(Double.valueOf(i),Double.valueOf(i));
//					new Point(new Position(Double.valueOf(i),Double.valueOf(i)));
//            obs.point2d[0] = Double.valueOf(i);
//            obs.point2d[1] = Double.valueOf(i);
			obs.utcTimeGPS = Long.valueOf(i);
			obs.speed = Double.valueOf(i);
			obs.acceleration = Double.valueOf(i);
			obs.brakeStatus = i;
			obs.absStatus = i;
			obs.tractionControlStatus = i;
			obs.stabilityControlStatus = i;
			observations.add(obs);
		}

		return observations;
	}
}
