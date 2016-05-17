package de.snet;

import java.text.MessageFormat;

import org.apache.flink.api.common.functions.FlatMapFunction;
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
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to process the DAS2 data set, remove all unnecessary keys and save everything in one file.
 * Creates another file that stores all distinct deviceIDs.
 * @author JonathanH5
 * 
 */
public class DAS2_SingleFile {
	
	private static final Logger log = LoggerFactory.getLogger(DAS2_SingleFile.class);

	public static void main(String[] args) throws Exception {
		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		// read in parameters
		ParameterTool parameter = ParameterTool.fromArgs(args);
		String sourcePath = parameter.getRequired("sourcePath");
		String outputPathFile = parameter.getRequired("outputPathFile");
		String outputPathIDs = parameter.getRequired("outputPathIDs");
		log.info(MessageFormat.format("Loading file from {0}, saving output file to {1} and deviceIDs to {2}", 
						sourcePath, outputPathFile, outputPathIDs));
		
		DataSet<String> source = env.readTextFile(sourcePath);
		//cleaned tuples
		DataSet<DAS2_Observation> cleaned = source.flatMap(new ObservationFlatMap());
		
		// sort distinct device IDs and save to file
		cleaned.map(x -> new Tuple1<Integer>(x.DeviceID))
			.distinct()
			.sortPartition(0, Order.ASCENDING)
			.setParallelism(1)
			.writeAsCsv(outputPathIDs, WriteMode.OVERWRITE);
		
		// sort source and write to output
		cleaned.sortPartition("DeviceID", Order.ASCENDING)
			.sortPartition("Trip", Order.ASCENDING)
			.sortPartition("Time", Order.ASCENDING)
			.setParallelism(1)
			.writeAsText(outputPathFile, WriteMode.OVERWRITE);		
		
		// execute program
		env.execute("DAS2 Single-File Job");
	}
	
	// *************************************************************************
	// Custom Flink Operators
	// *************************************************************************
	
	/**
	 * Extracts an input line into a DAS2_Observation. If this line is invalid,
	 * it is ignored, null values are handled properly.
	 * @author JonathanH5
	 *
	 */
	public static final class ObservationFlatMap implements FlatMapFunction<String, DAS2_Observation> {

		private static final long serialVersionUID = -1578033182182747973L;

		@Override
		public void flatMap(String arg0, Collector<DAS2_Observation> out) throws Exception {
			DAS2_Observation obs = DAS2_Observation.createDAS2_Observation(arg0);
			if (obs != null) {
				out.collect(obs);
			}
		}



	}
	
	// *************************************************************************
	// Custom Data Types (POJOS)
	// *************************************************************************
	
	/**
	 * Stores all relevant information of one DAS2_Observation.
	 * @author JonathanH5
	 *
	 */
	public static class DAS2_Observation {

		public Integer DeviceID;
		public Integer Trip;
		public Integer Time;
		public Double GPS_Heading;
		public Double GPS_Latitude;
		public Double GPS_Longitude;
		public Double GPS_Speed;
		public Long GPS_UTC_Time;
		public Integer InVehicle_ABS_State;
		public Integer InVehicle_Brake_Status; 
		public Double InVehicle_Longitudinal_Accel;
		public Integer InVehicle_Stability_Control_Status;
		public Integer InVehicle_Traction_Control_Status;
		
		public DAS2_Observation() {}
		
		/**
		 * Splits a DAS2 line String by "," and returns a new DAS2_Observation.
		 * Relevant fields: 0,1,2,6,7,8,11,12,16,17,19,22,25
		 * @param line
		 * @return DAS2_Observation with above relevant fields
		 */
		public static DAS2_Observation createDAS2_Observation(String line) {
			String[] f = line.split(",");
			//remove whitspaces
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].replaceAll(" ", "");
			}
			DAS2_Observation obs = new DAS2_Observation();
			try {
				obs.DeviceID = Integer.parseInt(f[0]);
				obs.Trip = Integer.parseInt(f[1]);
				obs.Time = Integer.parseInt(f[2]);
				obs.GPS_Heading = parseDouble(f[6]);
				obs.GPS_Latitude = parseDouble(f[7]);
				obs.GPS_Longitude = parseDouble(f[8]);
				obs.GPS_Speed = parseDouble(f[11]);
				obs.GPS_UTC_Time = parseLong(f[12]);
				obs.InVehicle_ABS_State = parseInt(f[16]);
				obs.InVehicle_Brake_Status = parseInt(f[17]);
				obs.InVehicle_Longitudinal_Accel = parseDouble(f[22]);
				obs.InVehicle_Stability_Control_Status = parseInt(f[22]);
				obs.InVehicle_Traction_Control_Status = parseInt(f[25]);
			} catch (Exception e) {
				log.warn("Invalid line: " + line);
				log.warn(e.getMessage());
				return null;
			}
			return obs;
		}
		
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

		@Override
		public String toString() {
			return DeviceID + "," + Trip + "," + Time + "," + GPS_Heading + "," + GPS_Latitude + "," + GPS_Longitude + ","
					+ GPS_Speed + "," + GPS_UTC_Time + "," + InVehicle_ABS_State + "," + InVehicle_Brake_Status + ","
					+ InVehicle_Longitudinal_Accel + "," + InVehicle_Stability_Control_Status + "," + InVehicle_Traction_Control_Status;
		}
		
		public String toString2() {
			return "DAS2_Observation [DeviceID=" + DeviceID + ", Trip=" + Trip + ", Time=" + Time + ", GPS_Heading="
					+ GPS_Heading + ", GPS_Latitude=" + GPS_Latitude + ", GPS_Longitude=" + GPS_Longitude
					+ ", GPS_Speed=" + GPS_Speed + ", GPS_UTC_Time=" + GPS_UTC_Time + ", InVehicle_ABS_State="
					+ InVehicle_ABS_State + ", InVehicle_Brake_Status=" + InVehicle_Brake_Status
					+ ", InVehicle_Longitudinal_Accel=" + InVehicle_Longitudinal_Accel
					+ ", InVehicle_Stability_Control_Status=" + InVehicle_Stability_Control_Status
					+ ", InVehicle_Traction_Control_Status=" + InVehicle_Traction_Control_Status + "]";
		}
	}
}
