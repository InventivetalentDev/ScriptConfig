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

import org.bukkit.plugin.Plugin;
import org.inventivetalent.scriptconfig.api.ScriptConfig;

import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * Provider class for {@link org.inventivetalent.scriptconfig.api.ScriptConfig}s
 * <p>
 * Use {@link #create(Plugin)} to create a new instance for your plugin
 */
public class ScriptConfigProvider {

	private         Plugin              plugin;
	protected final ScriptEngineManager scriptEngineManager;

	private ScriptConfigProvider(Plugin plugin) {
		this.plugin = plugin;
		scriptEngineManager = new ScriptEngineManager(plugin.getClass().getClassLoader());
	}

	/**
	 * Loads the raw script content
	 *
	 * @param script script content
	 * @param engine engine to use
	 * @return a new {@link ScriptConfiguration}
	 */
	public ScriptConfig load(String script, String engine) {
		return new ScriptConfiguration(this, plugin.getClass().getClassLoader(), engine, script);
	}

	/**
	 * Loads the raw script content for JavaScript
	 *
	 * @param script script content
	 * @return a new {@link ScriptConfiguration}
	 */
	public ScriptConfig load(String script) {
		return load(script, "JavaScript");
	}

	/**
	 * Reads the script content from a reader
	 *
	 * @param reader reader
	 * @param engine engine to use
	 * @return a new {@link ScriptConfiguration}
	 */
	public ScriptConfig load(Reader reader, String engine) {
		return new ScriptConfiguration(this, plugin.getClass().getClassLoader(), engine, reader);
	}

	/**
	 * Reads the script content from a reader for JavaScript
	 *
	 * @param reader reader
	 * @return a new {@link ScriptConfiguration}
	 */
	public ScriptConfig load(Reader reader) {
		return load(reader, "JavaScript");
	}

	/**
	 * Loads a script from a file
	 *
	 * @param file   {@link File} to load
	 * @param engine engine to use
	 * @return a new {@link ScriptConfiguration}
	 * @throws FileNotFoundException
	 */
	public ScriptConfig load(File file, String engine) throws FileNotFoundException {
		try {
			ScriptConfig config = load(new FileReader(file), engine);
			((ScriptConfiguration) config).file = file;
			return config;
		} catch (InvalidScriptException e) {
			e.setScriptSource(file.getAbsolutePath());
			throw e;
		}
	}

	/**
	 * Loads a script from a file for JavaScript
	 *
	 * @param file {@link File} to load
	 * @return a new {@link ScriptConfiguration}
	 * @throws FileNotFoundException
	 */
	public ScriptConfig load(File file) throws FileNotFoundException {
		return load(file, "JavaScript");
	}

	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Creates a new {@link ScriptConfigProvider}
	 *
	 * @param plugin {@link Plugin}
	 * @return a new {@link ScriptConfigProvider}
	 */
	public static ScriptConfigProvider create(Plugin plugin) {
		if (plugin == null) { throw new IllegalArgumentException("plugin cannot be null"); }
		if (!plugin.isEnabled()) { throw new IllegalStateException(plugin.getName() + " is not enabled"); }
		//		if (!(plugin instanceof JavaPlugin)) { throw new IllegalArgumentException("plugin is not a JavaPlugin"); }
		return new ScriptConfigProvider(plugin);
	}

}
