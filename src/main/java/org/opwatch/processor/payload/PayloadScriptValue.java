package org.opwatch.processor.payload;

import jdk.nashorn.api.scripting.AbstractJSObject;
import org.opwatch.service.ScriptService;

public abstract class PayloadScriptValue extends AbstractJSObject {

	public abstract Object toJavascript(ScriptService scriptService);

	public static String javascriptClassName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	@Override
	public String getClassName() {
		return javascriptClassName(this.getClass());
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		if (hint == String.class) {
			return "[object " + getClassName() + "]";
		} else {
			throw new UnsupportedOperationException("cannot.get.default.number");
		}
	}

}
