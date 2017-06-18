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

package org.opwatch.processor.primitives.filter.trail;

import org.opwatch.processor.common.TrailCollecter;
import org.opwatch.processor.payload.ObjectSeries;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.SeriesObject;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.service.ProcessorService;

public class Trail extends TrailCollecter<TrailConfig> {

	public Trail(ProcessorService processorService, TrailConfig configuration, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, configuration, name, scheduledExecutor, duration);
	}

	@Override
	protected Object collectedObject(Payload payload) {
		return payload.getValue();
	}

	@Override
	protected Object producedObject() {
		SeriesObject[] objects = new SeriesObject[queue.size()];
		queue.toArray(objects);
		return ObjectSeries.toJavascript(processorService.getScriptService(), objects);
	}

}
