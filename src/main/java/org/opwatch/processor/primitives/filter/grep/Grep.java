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

package org.opwatch.processor.primitives.filter.grep;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.ConditionalFilter;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.internal.objects.NativeRegExp;

public class Grep extends ConditionalFilter<GrepConfig> {

	private NativeRegExp regexp;
	private boolean invert;

	public Grep(ProcessorService processorService, GrepConfig configuration, String name, NativeRegExp regexp, boolean invert) {
		super(processorService, configuration, name);
		this.regexp = regexp;
		this.invert = invert;
	}

	@Override
	public boolean predicateValue(Payload input) {
		String text = payloadValue(input, String.class);
		boolean match = regexp.test(text);
		return (match && !invert) || (!match && invert);
	}

}
