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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTracker implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected List<Long> timeStamps;
	protected List<Long> values;
	protected List<String> labels;

	protected long dumpInterval = 0;
	protected long lastDump = 0;
	protected String fname;

	protected long startTime;

	protected int interval;
	protected int intervalCounter;
	protected String name;

	protected long buffer;
	
	protected boolean firstWrite = true;

	public PerformanceTracker(String name, String fname) {
		timeStamps = new ArrayList<Long>();
		values = new ArrayList<Long>();
		labels = new ArrayList<String>();
		this.interval = 1;
		this.name = name;
		this.fname = fname;
		buffer = 0;
		this.startTime = System.currentTimeMillis();
	}

	public PerformanceTracker(String name, int capacity, int interval, String fname) {
		this(name, capacity, interval, 0, fname);
	}

	public PerformanceTracker(String name, int capacity, int interval, long dumpInterval,
			String fname) {
		timeStamps = new ArrayList<Long>(capacity);
		values = new ArrayList<Long>(capacity);
		labels = new ArrayList<String>(capacity);
		this.interval = interval;
		this.name = name;
		buffer = 0;
		this.dumpInterval = dumpInterval;
		this.fname = fname;
		this.startTime = System.currentTimeMillis();
	}

	public void track(Long value, String label) {
		buffer = buffer + value;
		intervalCounter++;

		if (intervalCounter % interval == 0) {

			add(buffer, label);
			buffer = 0;
			intervalCounter = 0;
		}
	}

	public void add(Long value, String label) {
		long ctime = System.currentTimeMillis() - startTime;
		values.add(value);
		labels.add(label);
		timeStamps.add(ctime);
		
		if (dumpInterval > 0) {
			if (ctime - lastDump > dumpInterval) {
				System.out.println("csv-be iras!!");
				writeCSV();
				lastDump = ctime;
				values.clear();
				labels.clear();
				timeStamps.clear();
			}
		}

	}

	public void track(Long value) {
		track(value, "tracker");
	}

	public void track(int value, String label) {
		track(Long.valueOf(value), label);
	}

	public void track(int value) {
		track(Long.valueOf(value), "tracker");
	}

	public void track() {
		track(1);
	}

	@Override
	public String toString() {
		StringBuilder csv = new StringBuilder();

		if (firstWrite) {
			firstWrite = false;
			csv.append("Time," + name + ",Label\n");
		}
			
		for (int i = 0; i < timeStamps.size(); i++) {
			csv.append(timeStamps.get(i) + "," + values.get(i) + "," + labels.get(i) + "\n");
		}

		return csv.toString();
	}

	public void writeCSV() {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fname, true)));
			out.print(toString());
			out.close();

		} catch (IOException e) {
			System.out.println("CSV output file not found");
		}

	}

	public void writeCSV(String fname) {

		try {
			PrintWriter out = new PrintWriter(fname);
			out.print(toString());
			out.close();

		} catch (FileNotFoundException e) {
			System.out.println("CSV output file not found");
		}

	}

	public void setFname(String fname) {
		this.fname = fname;
	}

}
