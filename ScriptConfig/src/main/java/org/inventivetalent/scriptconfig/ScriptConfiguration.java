/*
 * Copyright 2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.scriptconfig;

import org.inventivetalent.scriptconfig.api.JavaWrapper;
import org.inventivetalent.scriptconfig.api.ScriptConfig;
import org.inventivetalent.scriptconfig.wrappers.LoggerWrapper;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.Reader;
import java.util.Map;

class ScriptConfiguration implements ScriptConfig {

	private   ScriptEngine scriptEngine;
	private   Object       content;
	protected File         file;

	private ScriptConfiguration(ScriptConfigProvider provider, ClassLoader classLoader, String engine) {
		//"Change" the classloader before creating the engine (https://stackoverflow.com/questions/33945507/java-nashorn-classnotfoundexception-java-type | https://stackoverflow.com/questions/30225398/java-8-scriptengine-across-classloaders/30251930#30251930)
		Thread.currentThread().setContextClassLoader(classLoader);

		scriptEngine = provider.scriptEngineManager.getEngineByName(engine);

		// Register internal Java wrappers
		registerJavaWrapper("log", new LoggerWrapper(provider.getPlugin()) {
			@Override
			public String format(String message) {
				if (file != null) {
					return String.format("[Script|%s] %s", file.getName(), message);
				}
				return super.format(message);
			}
		});
	}

	protected ScriptConfiguration(ScriptConfigProvider provider, ClassLoader classLoader, String engine, String scriptContent) {
		this(provider, classLoader, engine);
		try {
			this.content = this.scriptEngine.eval(scriptContent);
		} catch (ScriptException e) {
			throw new RuntimeException("Invalid script", e);
		}
	}

	protected ScriptConfiguration(ScriptConfigProvider provider, ClassLoader classLoader, String engine, Reader reader) {
		this(provider, classLoader, engine);
		try {
			this.content = this.scriptEngine.eval(reader);
		} catch (ScriptException e) {
			throw new RuntimeException("Invalid script", e);
		}
	}

	@Override
	public Object callFunction(String name, Object... args) {
		Invocable invocable = (Invocable) this.scriptEngine;
		try {
			return invocable.invokeFunction(name, args);
		} catch (ScriptException e) {
			throw throwScriptException(name, e);
		} catch (NoSuchMethodException e) {
			if (e.getMessage().startsWith("No such function ")) { // Function actually not found
				throw throwNoSuchFunction(name, "not found", e);
			} else {// Exception caused by a method invocation in the script
				throw throwScriptException(name, e);
			}
		}
	}

	@Override
	public Object callMethod(Object thiz, String name, Object... args) {
		Invocable invocable = (Invocable) this.scriptEngine;
		try {
			return invocable.invokeMethod(thiz, name, args);
		} catch (ScriptException e) {
			throw throwScriptException(name, e);
		} catch (NoSuchMethodException e) {
			if (e.getMessage().startsWith("No such function ")) { // Function actually not found
				throw throwNoSuchFunction(name, "not found", e);
			} else {// Exception caused by a method invocation in the script
				throw throwScriptException(name, e);
			}
		}
	}

	RuntimeScriptException throwScriptException(String name, Exception e) {
		return new RuntimeScriptException("Exception while calling function '" + name + "'" + (file != null ? " in file '" + file.getName() + "'" : ""), e);
	}

	NoSuchFunctionException throwNoSuchFunction(String name, String message, Exception e) {
		String msg = "Function '" + name + "' " + message + (file != null ? " in file '" + file.getName() + "'" : "");
		if (e != null) { return new NoSuchFunctionException(msg, e); }
		return new NoSuchFunctionException(msg);
	}

	@Override
	public void registerJavaWrapper(String name, JavaWrapper wrapper) {
		setVariable(name, wrapper);
	}

	@Override
	public void setVariable(String name, Object value) {
		this.scriptEngine.put(name, value);
	}

	@Override
	public void setVariables(Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			this.scriptEngine.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object getVariable(String name) {
		//		try {
		//			return this.scriptEngine.eval(name);
		//		} catch (ScriptException e) {
		return this.scriptEngine.get(name);
		//		}
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	@Override
	public Object getContent() {
		return content;
	}
}
