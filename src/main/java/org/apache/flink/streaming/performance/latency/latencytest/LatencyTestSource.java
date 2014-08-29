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

package org.apache.flink.streaming.performance.latency.latencytest;

import java.io.IOException;

import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.function.source.RichSourceFunction;
import org.apache.flink.util.Collector;

public class LatencyTestSource extends RichSourceFunction<Tuple1<Long>>{
	private static final long serialVersionUID = 1L;

	private Tuple1<Long> outValue = new Tuple1<Long>();
	
	@Override
	public void invoke(Collector<Tuple1<Long>> collector) throws IOException {
		System.out.println(System.currentTimeMillis());
		while (true) {
			outValue.f0 = System.currentTimeMillis();
			collector.collect(outValue);
		}
	}
}