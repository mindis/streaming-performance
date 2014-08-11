/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.flink.streaming.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LatencyTester {
	
	private static enum State {STARTING, HISTOGRAM, PRINT};
	
	private String fileName;
	
	private HistogramMap histogram;
	
	private State state;
	
	private long lastStateChange;
	private long startingTime = 60 * 1000;
	private long histogramTime = 30 * 1000;
	
	public LatencyTester(int intervalLength, String fileName) {
		this.histogram = new HistogramMap(intervalLength);
		this.fileName = fileName;
		
		lastStateChange = System.currentTimeMillis();
		state = State.STARTING;
	}
	
	public void add(long sendTime, long arrivalTime) {
		long diff = arrivalTime - sendTime;
		diff = diff / 1000000L;
		
		switch(state) {
		case STARTING:
			state = handleStarting();
			break;
		case HISTOGRAM:
			state = histogramAdd(diff);
			break;
		case PRINT:
			state = print();
			break;
		}
	}
	
	private State handleStarting() {
		long nowMilli = System.currentTimeMillis();
		State retval;
		
		if(nowMilli < lastStateChange + startingTime) {
			retval = State.STARTING;
		} else {
			lastStateChange = nowMilli;
			retval = State.HISTOGRAM;
		}
		return retval;
	}
	
	private State histogramAdd(long value) {
		long nowMilli = System.currentTimeMillis();
		State retval;
		
		if(nowMilli < lastStateChange + histogramTime) {
			histogram.add(value);
			retval = State.HISTOGRAM;
		} else {
			lastStateChange = nowMilli;
			retval = State.PRINT;
		}
		return retval;
	}
	
	private State print() {
		writeLog();
		lastStateChange = System.currentTimeMillis();
		return State.HISTOGRAM;
	}
	
	public void writeLog() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			out.print(histogram.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("CSV output file not found");
		}
	}
}
