/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.processor.primitives.filter.collect;

import com.google.common.collect.EvictingQueue;
import org.opwatch.processor.payload.ObjectSeries;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.SeriesObject;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

public class Collect extends Filter<CollectConfig> {

	private int count;
	private EvictingQueue<SeriesObject> queue;

	public Collect(ProcessorService processorService, CollectConfig configuration, String name, int count) {
		super(processorService, configuration, name);
		this.count = count;
		this.queue = EvictingQueue.create(count);
	}

	@Override
	public synchronized void consume(Payload payload) {
		Object value = payload.getValue();
		if (value != null) {
			long timestamp = System.currentTimeMillis();
			queue.add(new SeriesObject(value, timestamp));
			if (queue.size() == count) {
				SeriesObject[] objects = new SeriesObject[queue.size()];
				queue.toArray(objects);
				outputTransformed(ObjectSeries.toJavascript(processorService.getScriptService(), objects), payload);
			}
		}
	}

}
