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

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.OptionalProperty;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepConfig extends ActiveProcessorConfig {

	private NativeRegExp regexp;
	private Boolean invert = false;

	@ImplicitProperty
	public NativeRegExp getRegexp() {
		return regexp;
	}

	public void setRegexp(NativeRegExp regexp) {
		this.regexp = regexp;
	}

	@OptionalProperty
	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
