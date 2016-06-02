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

package org.inventivetalent.scriptconfig.api;

import javax.script.ScriptEngine;
import java.util.Map;

public interface ScriptConfig {

	/**
	 * Calls a function in the script
	 *
	 * @param name name of the function
	 * @param args array of arguments
	 * @return the returned value of the function
	 */
	Object callFunction(String name, Object... args);

	/**
	 * Calls a function in a script object
	 *
	 * @param thiz object containing the function
	 * @param name name of the function
	 * @param args array of arguments
	 * @return the returned value of the function
	 */
	Object callMethod(Object thiz, String name, Object... args);

	/**
	 * Register a java class of methods and fields to be accessible to the script
	 * <p>
	 * <p>
	 * The class's methods are available under the specified name, for example:
	 * <p>
	 * The method "broadcast" registered with the name "minecraft" will be available at "minecraft.broadcast"
	 *
	 * @param name    name of the class variable
	 * @param wrapper the {@link JavaWrapper}
	 */
	void registerJavaWrapper(String name, JavaWrapper wrapper);

	/**
	 * Provides the script a variable
	 *
	 * @param name  name of the variable
	 * @param value value
	 */
	void setVariable(String name, Object value);

	/**
	 * Provides the script multiple variables
	 *
	 * @param map map of variables
	 */
	void setVariables(Map<String, Object> map);

	/**
	 * Get the value of a variable
	 *
	 * @param name name of the variable
	 * @return the value
	 */
	Object getVariable(String name);

	ScriptEngine getScriptEngine();

	Object getContent();

}
