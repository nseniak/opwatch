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

package org.opwatch.testutil;

class StringComparisonTest extends OutputComparisonTest {

	private String displayName;
	private String output;

	public StringComparisonTest(Class<?> loaderClass, String output, String expectedResourceName) {
		super(loaderClass, expectedResourceName);
		this.output = output;
		this.displayName = expectedResourceName;
	}

	@Override
	public String output() {
		return output;
	}

	@Override
	public String displayName() {
		return displayName;
	}

}
