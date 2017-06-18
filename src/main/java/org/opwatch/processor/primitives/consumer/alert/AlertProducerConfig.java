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

package org.opwatch.processor.primitives.consumer.alert;

import org.opwatch.processor.config.*;

public class AlertProducerConfig extends ActiveProcessorConfig {

	private String level = "medium";
	private String title;
	private ValueOrFilter<Object> details;
	private JavascriptPredicate trigger;
	private Boolean toggle = false;
	private String channel;

	@OptionalProperty
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@ImplicitProperty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@OptionalProperty
	public ValueOrFilter<Object> getDetails() {
		return details;
	}

	public void setDetails(ValueOrFilter<Object> details) {
		this.details = details;
	}

	@OptionalProperty
	public JavascriptPredicate getTrigger() {
		return trigger;
	}

	public void setTrigger(JavascriptPredicate trigger) {
		this.trigger = trigger;
	}

	@OptionalProperty
	public Boolean getToggle() {
		return toggle;
	}

	public void setToggle(Boolean toggle) {
		this.toggle = toggle;
	}

	@OptionalProperty
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
