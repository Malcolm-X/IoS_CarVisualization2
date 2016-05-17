package de.snet;

import java.text.MessageFormat;

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
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to process the DAS2 data set, remove all unnecessary keys and save everything in one file.
 * Creates another file that stores all distinct deviceIDs.
 * 
 * outputFile: 
 * DeviceID - Trip - Time - GPS_Heading - GPS_Heading - GPS_Longitude - GPS_Speed - GPS_UTC_Time - 
 * InVehicle_ABS_State - InVehicle_Brake_Status - InVehicle_Longitudinal_Accel - InVehicle_Stability_Control_Status
 * InVehicle_Traction_Control_Status
 * 
 * outputIDs: DeviceID
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
		
		// read in source files
		DataSet<Tuple13<Integer, Integer, Integer, Double, Double, Double, 
			Double, Long, Integer, Integer, Double, Integer, Integer>> source = env.readCsvFile(sourcePath)
				//.ignoreInvalidLines() //might be a bad idea
				.includeFields("11100011100110001101001001")
				.ignoreFirstLine()
				.types(Integer.class, Integer.class, Integer.class, Double.class, Double.class, Double.class, 
						Double.class, Long.class, Integer.class, Integer.class, Double.class, Integer.class, Integer.class);
		
		// sort distinct device IDs and save to file
		source.map(x -> new Tuple1<Integer>(x.f0))
			.distinct()
			.sortPartition(0, Order.ASCENDING)
			.setParallelism(1)
			.writeAsCsv(outputPathIDs, WriteMode.OVERWRITE);
		
		// sort source and write to output
		source.sortPartition(0, Order.ASCENDING)
			.sortPartition(1, Order.ASCENDING)
			.sortPartition(2, Order.ASCENDING)
			.setParallelism(1)
			.writeAsCsv(outputPathFile, "\n", ",", WriteMode.OVERWRITE);

		// execute program
		env.execute("DAS2 Single-File Job");
	}
	
	// *************************************************************************
	// Custom Flink Operators
	// *************************************************************************
	
}
