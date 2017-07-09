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

package org.opwatch.processor.common;

public enum DataRequirement {

	Unknown,
	// As input: Should not receive any input data
	// As output: Does not produces any data
	NoData,
	// As input: Requires input data
	// As output: Produces data that should not be ignored
	Data,
	// As input: Can receive data but does not require it
	// As output: Produces data but that data can be ignored
	Any

}
