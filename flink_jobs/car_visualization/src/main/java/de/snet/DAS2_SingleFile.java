//package de.snet;
//
//import java.text.MessageFormat;
//
//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.api.common.operators.Order;
//import org.apache.flink.api.java.DataSet;
//
///**
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.api.java.tuple.Tuple1;
//import org.apache.flink.api.java.utils.ParameterTool;
//import org.apache.flink.core.fs.FileSystem.WriteMode;
//import org.apache.flink.util.Collector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Job to process the DAS2 data set, remove all unnecessary keys and save everything in one file.
// * Creates another file that stores all distinct deviceIDs.
// * @author JonathanH5
// *
// */
//public class DAS2_SingleFile {
//
//	private static final Logger log = LoggerFactory.getLogger(DAS2_SingleFile.class);
//
//	public static void main(String[] args) throws Exception {
//		// set up the execution environment
//		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//
//		// read in parameters
//		ParameterTool parameter = ParameterTool.fromArgs(args);
//		String sourcePath = parameter.getRequired("sourcePath");
//		String outputPathFile = parameter.getRequired("outputPathFile");
//		String outputPathIDs = parameter.getRequired("outputPathIDs");
//		log.info(MessageFormat.format("Loading file from {0}, saving output file to {1} and deviceIDs to {2}",
//						sourcePath, outputPathFile, outputPathIDs));
//
//		DataSet<String> source = env.readTextFile(sourcePath);
//		//cleaned tuples
//		DataSet<Observation> cleaned = source.flatMap(new ObservationFlatMap());
//
//		// sort distinct device IDs and save to file
//		cleaned.map(x -> new Tuple1<Integer>(x.deviceID))
//			.distinct()
//			.sortPartition(0, Order.ASCENDING)
//			.setParallelism(1)
//			.writeAsCsv(outputPathIDs, WriteMode.OVERWRITE);
//
//		// sort source and write to output
//		cleaned.sortPartition("deviceID", Order.ASCENDING)
//			.sortPartition("trip", Order.ASCENDING)
//			.sortPartition("time", Order.ASCENDING)
//			.setParallelism(1)
//			.writeAsText(outputPathFile, WriteMode.OVERWRITE);
//
//		// execute program
//		env.execute("DAS2 Single-File Job");
//	}
//
//	// *************************************************************************
//	// Custom Flink Operators
//	// *************************************************************************
//
//	/**
//	 * Extracts an input line into a DAS2_Observation. If this line is invalid,
//	 * it is ignored, null values are handled properly.
//	 * @author JonathanH5
//	 *
//	 */
//	public static final class ObservationFlatMap implements FlatMapFunction<String, Observation> {
//
//		private static final long serialVersionUID = -1578033182182747973L;
//
//		@Override
//		public void flatMap(String arg0, Collector<Observation> out) throws Exception {
//			Observation obs = Observation.createDAS2_Observation(arg0);
//			if (obs != null) {
//				out.collect(obs);
//			}
//		}
//
//
//
//	}
//
//}
