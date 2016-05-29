package de.snet;

import java.text.MessageFormat;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.RichJoinFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to process the DAS1 and DAS2 data set, remove all unnecessary keys and save everything in one file.
 * Creates another file that stores all distinct deviceIDs.
 * @author JonathanH5
 * 
 */
public class DAS1_DAS2_SingleFile {
	
	private static final Logger log = LoggerFactory.getLogger(DAS1_DAS2_SingleFile.class);

	public static void main(String[] args) throws Exception {
		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		// read in parameters
		ParameterTool parameter = ParameterTool.fromArgs(args);
		String sourcePathDAS2 = parameter.getRequired("das2");
		String sourcePathDAS1_DataDas = parameter.getRequired("das1_datadas");
		// String sourcePathDAS1_DataGpsDas = parameter.getRequired("das1_datagpsdas");
		String sourcePathDAS1_DataWsuDas = parameter.getRequired("das1_datawsudas");
		// String sourcePathDAS1_TrynBytes = parameter.getRequired("das1_trynbytes"); FILE NOT FOUND
		String outputPathFile = parameter.getRequired("outputFile");
		String outputPathIDs = parameter.getRequired("outputID");
		log.info(MessageFormat.format("Loading DAS2 from {0}, DAS1_DataDas from {1}, DAS1_DataGpsDas from {2}, "
				+ "DAS1_DataWsuDas from {3} and DAS1_TrynBytes from {4}. Saving output file to {5} and deviceIDs to {6}", 
				sourcePathDAS2, sourcePathDAS1_DataDas, "<skipped>", sourcePathDAS1_DataWsuDas, 
				"<skipped>", outputPathFile, outputPathIDs));
		
		// read in DAS1 files
		DataSet<Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>> das1_DataDas = env
				.readTextFile(sourcePathDAS1_DataDas).flatMap(new DAS1_DataDasFlatMap());
		/* SKIPPED
		DataSet<Tuple4<Integer, Integer, Integer, Long>> das1_DataGpsDas = env
				.readTextFile(sourcePathDAS1_DataGpsDas).flatMap(new DAS1_DataGpsDasFlatMap());
		*/
		DataSet<Tuple5<Integer, Integer, Integer, Double, Double>> das1_DataWsuDas = env
				.readTextFile(sourcePathDAS1_DataWsuDas).flatMap(new DAS1_DataWsuDasFlatMap());
		/* FILE NOT FOUND
		DataSet<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> das1_TrynBytes = env
				.readTextFile(sourcePathDAS1_TrynBytes).flatMap(new DAS1_TrynBytesFlatMap());
		*/
		
		//join DAS1 files
		DataSet<Observation> das1_observation = das1_DataDas
				.leftOuterJoin(das1_DataWsuDas).where(0,1,2).equalTo(0,1,2).with(new DAS1_ObservationJoin());
		
		das1_observation
			.sortPartition("deviceID", Order.ASCENDING)
			.sortPartition("trip", Order.ASCENDING)
			.sortPartition("time", Order.ASCENDING)
			.setParallelism(1)
			.writeAsText("/Users/jonathandev/Desktop/das1_observation.csv", WriteMode.OVERWRITE);
		
		// read in DAS2 file
		DataSet<Observation> das2_observation = env.readTextFile(sourcePathDAS2).flatMap(new ObservationDAS2FlatMap());
		
		das2_observation
			.sortPartition("deviceID", Order.ASCENDING)
			.sortPartition("trip", Order.ASCENDING)
			.sortPartition("time", Order.ASCENDING)
			.setParallelism(1)
			.writeAsText("/Users/jonathandev/Desktop/das2_observation.csv", WriteMode.OVERWRITE);
		
		// union DAS1 observation and DAS2 observation
		
		DataSet<Observation> union = das1_observation.union(das2_observation);
		
		// sort distinct device IDs and save to file
		/*
		union.map(x -> new Tuple1<Integer>(x.deviceID))
			.distinct()
			.sortPartition(0, Order.ASCENDING)
			.setParallelism(1)
			.writeAsCsv(outputPathIDs, WriteMode.OVERWRITE);
		*/
		
		// sort and write to output
		union.sortPartition("deviceID", Order.ASCENDING)
			.sortPartition("trip", Order.ASCENDING)
			.sortPartition("time", Order.ASCENDING)
			.setParallelism(1)
			.writeAsText(outputPathFile, WriteMode.OVERWRITE);		
		
		// execute program
		JobExecutionResult result =  env.execute("DAS1 + DAS2 Single-File Job");
		log.info(result.getAccumulatorResult("hasSpeed") + "/" + result.getAccumulatorResult("entries") + " have speed information.");
	}
	
	// *************************************************************************
	// Custom Flink Operators
	// *************************************************************************
	
	/**
	 * Splits line String by "," and returns a Tuple7, if valid.
	 * If this line is invalid, it is ignored. Null values are handled properly.
	 * Relevant fields: 0,1,2,14,15,16,11
	 * -> devideID, trip, time, headingGPS, latitudeGPS, longitudeGPS, brakeStatus
	 * @author JonathanH5
	 * 
	 */
	public static final class DAS1_DataDasFlatMap implements 
		FlatMapFunction<String, Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>> {

		private static final long serialVersionUID = -3334502331335353425L;

		@Override
		public void flatMap(String line,
				Collector<Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>> out) throws Exception {
			String[] f = line.split(",");
			//remove whitspaces
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].replaceAll(" ", "");
			}
			try {
				Integer deviceID = Integer.parseInt(f[0]);
				Integer trip = Integer.parseInt(f[1]);
				Integer time = Integer.parseInt(f[2]);
				Double headingGPS = parseDouble(f[14]);
				Double latitudeGPS = parseDouble(f[15]);
				Double longitudeGPS = parseDouble(f[16]);
				Integer brakeStatus = parseInt(f[11]);
				out.collect(new Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>(
						deviceID, trip, time, headingGPS, latitudeGPS, longitudeGPS, brakeStatus));
			} catch (Exception e) {
				log.warn("Invalid line: " + line);
				log.warn(e.getMessage());
			}
		}
		
	}

	/**
	 * Splits line String by "," and returns a Tuple4, if valid.
	 * If this line is invalid, it is ignored. Null values are handled properly.
	 * Relevant fields: 0,1,2,3 (GpsTime),4 (GpsWeek)
	 * -> devideID, trip, time, utcTimeGPS (created from GpsTime and GpsWeek)
	 * @author JonathanH5
	 * 
	 */
	public static final class DAS1_DataGpsDasFlatMap implements 
		FlatMapFunction<String, Tuple4<Integer, Integer, Integer, Long>> {

		private static final long serialVersionUID = -3334502331335353425L;

		@Override
		public void flatMap(String line,
				Collector<Tuple4<Integer, Integer, Integer, Long>> out) throws Exception {
			String[] f = line.split(",");
			//remove whitspaces
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].replaceAll(" ", "");
			}
			try {
				Integer deviceID = Integer.parseInt(f[0]);
				Integer trip = Integer.parseInt(f[1]);
				Integer time = Integer.parseInt(f[2]);
				Integer gpsWeek = parseInt(f[3]);
				Long gpsTime = parseLong(f[4]);
				if (gpsWeek == null || gpsTime == null) {
					out.collect(new Tuple4<Integer, Integer, Integer, Long>(
							deviceID, trip, time, null));
				}
				//round gpsTime
				time = time - time % 10;
				Long utcTimeGPS = 604800000 * gpsWeek + gpsTime - 1464370575000L;
				out.collect(new Tuple4<Integer, Integer, Integer, Long>(
						deviceID, trip, time, utcTimeGPS));
			} catch (Exception e) {
				log.warn("Invalid line: " + line);
				log.warn(e.getMessage());
			}
		}
		
	}

	/**
	 * Splits line String by "," and returns a Tuple5, if valid.
	 * If this line is invalid, it is ignored. Null values are handled properly.
	 * Relevant fields: 0,1,2,16,20
	 * -> devideID, trip, time, speed, acceleration
	 * @author JonathanH5
	 * 
	 */
	public static final class DAS1_DataWsuDasFlatMap implements 
		FlatMapFunction<String, Tuple5<Integer, Integer, Integer, Double, Double>> {

		private static final long serialVersionUID = -3334502331335353425L;

		@Override
		public void flatMap(String line,
				Collector<Tuple5<Integer, Integer, Integer, Double, Double>> out) throws Exception {
			String[] f = line.split(",");
			//remove whitspaces
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].replaceAll(" ", "");
			}
			try {
				Integer deviceID = Integer.parseInt(f[0]);
				Integer trip = Integer.parseInt(f[1]);
				Integer time = Integer.parseInt(f[2]);
				Double speed = parseDouble(f[16]);
				Double acceleration = parseDouble(f[20]);
				// recalculate some fields based on specifications for later joins
				if (speed != null) {
					speed = speed / 3.6;
				}
				out.collect(new Tuple5<Integer, Integer, Integer, Double, Double>(
						deviceID, trip, time, speed, acceleration));
			} catch (Exception e) {
				log.warn("Invalid line: " + line);
				log.warn(e.getMessage());
			}
		}
		
	}
	
	/**
	 * Splits line String by "," and returns a Tuple6, if valid.
	 * If this line is invalid, it is ignored. Null values are handled properly.
	 * Relevant fields: 0,1,2,???
	 * -> devideID, trip, time, ???
	 * @author JonathanH5
	 * 
	 */
	public static final class DAS1_TrynBytesFlatMap implements 
		FlatMapFunction<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> {

		private static final long serialVersionUID = -3334502331335353425L;

		@Override
		public void flatMap(String line,
				Collector<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> out) throws Exception {
			/* CANNOT FINISH UNTIL FILE IS FOUND
			String[] f = line.split(",");
			//remove whitspaces
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].replaceAll(" ", "");
			}
			try {
				Integer deviceID = Integer.parseInt(f[0]);
				Integer trip = Integer.parseInt(f[1]);
				Integer time = Integer.parseInt(f[2]);
				Integer absStatus 
				Integer tractionControlStatus
				Integer stabilityControlStatus
				out.collect(new Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>(
						deviceID, trip, time, absStatus, tractionControlStatus, stabilityControlStatus));
			} catch (Exception e) {
				log.warn("Invalid line: " + line);
				log.warn(e.getMessage());
			}
			*/
		}
		
	}
	
	/**
	 * Joins the DAS1_DataDas and the DAS1_DataGpsDas data sets and returns Tuple8.
	 * MIGHT BE USED LATER BUT IS SKIPPED FOR NOW
	 * @author JonathanH5
	 *
	 */
	public static final class DAS1_DataGpsJoin implements JoinFunction<
		Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>, 
		Tuple4<Integer, Integer, Integer, Long>, 
		Tuple8<Integer, Integer, Integer, Double, Double, Double, Integer, Long>> {

		private static final long serialVersionUID = 590660124936438265L;

		@Override
		public Tuple8<Integer, Integer, Integer, Double, Double, Double, Integer, Long> join(
				Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer> arg0,
				Tuple4<Integer, Integer, Integer, Long> arg1) throws Exception {
			
			return new Tuple8<Integer, Integer, Integer, Double, Double, Double, Integer, Long>(
					arg0.f0, arg0.f1, arg0.f2, arg0.f3, arg0.f4, arg0.f5, arg0.f6, arg1.f3);
		}
		
	}
	
	/**
	 * LeftOuterJoins the DAS1_DataDas and the DAS1_DataWsuDas data sets and return an observation.
	 * DAS1_DataWsuDas might not have all times stored in DAS1_DataDas for each deviceID, trip combination.
	 * Not all fields of the observation can be set, some will remain null.
	 * 
	 * Creates to accumulators to identify how many entries we have in total and how many of them have speed
	 * information ("entries" and "hasSpeed").
	 * 
	 * @author JonathanH5
	 *
	 */
	public static final class DAS1_ObservationJoin extends RichJoinFunction<
		Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer>, 
		Tuple5<Integer, Integer, Integer, Double, Double>, 
		Observation> {

	private static final long serialVersionUID = 4836898571667494014L;
	
	private IntCounter hasSpeed = new IntCounter();
	private IntCounter entries = new IntCounter();

	@Override
		public void open(Configuration parameters) throws Exception {
			getRuntimeContext().addAccumulator("hasSpeed", this.hasSpeed);
			getRuntimeContext().addAccumulator("entries", this.entries);
			super.open(parameters);
		}
	
	@Override
	public Observation join(Tuple7<Integer, Integer, Integer, Double, Double, Double, Integer> arg0,
			Tuple5<Integer, Integer, Integer, Double, Double> arg1) throws Exception {
		this.entries.add(1);
		Observation obs = new Observation();
		obs.deviceID = arg0.f0;
		obs.trip = arg0.f1;
		obs.time = arg0.f2;
		obs.headingGPS = arg0.f3;
		obs.latitudeGPS = arg0.f4;
		obs.longitudeGPS = arg0.f5;
		obs.brakeStatus = arg0.f6;
		if (arg1 != null) {
			this.hasSpeed.add(1);
			obs.speed = arg1.f3;
			obs.acceleration = arg1.f4;
		}
		return obs;
	}


	
}
	
	/**
	 * Extracts an input line into a DAS2_Observation. If this line is invalid,
	 * it is ignored, null values are handled properly.
	 * @author JonathanH5
	 *
	 */
	public static final class ObservationDAS2FlatMap implements FlatMapFunction<String, Observation> {

		private static final long serialVersionUID = -1578033182182747973L;

		@Override
		public void flatMap(String arg0, Collector<Observation> out) throws Exception {
			Observation obs = Observation.createDAS2_Observation(arg0);
			if (obs != null) {
				out.collect(obs);
			}
		}

	}
	
	
	
	// *************************************************************************
	// Helper
	// *************************************************************************
	
	/**
	 * Parses a String to a Double. Returns null if not possible.
	 * @param s
	 * @return the corresponding Double or null
	 */
	public static Double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Parses a String to a Long. Returns null if not possible.
	 * @param s
	 * @return the corresponding Long or null
	 */
	public static Long parseLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Parses a String to an Integer. Returns null if not possible.
	 * @param s
	 * @return the corresponding Integer or null
	 */
	public static Integer parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}


}
