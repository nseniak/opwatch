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

package org.opwatch.documentation;

import java.util.List;

public class ProcessorDoc {

	private String type;
	private List<ProcessorDescFieldDoc> fields;
	private String documentation;

	public ProcessorDoc(String type, List<ProcessorDescFieldDoc> fields, String documentation) {
		this.type = type;
		this.fields = fields;
		this.documentation = documentation;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ProcessorDescFieldDoc> getFields() {
		return fields;
	}

	public void setFields(List<ProcessorDescFieldDoc> fields) {
		this.fields = fields;
	}

}
