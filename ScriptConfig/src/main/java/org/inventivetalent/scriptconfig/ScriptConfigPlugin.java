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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.scriptconfig.api.ScriptConfig;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ScriptConfigPlugin extends JavaPlugin {

	public ScriptConfigProvider scriptConfigProvider;

	@Override
	public void onEnable() {
		scriptConfigProvider = ScriptConfigProvider.create(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("scriptconfig.command.eval")) {
				sender.sendMessage(" ");
				sender.sendMessage("§a/scriptconfig eval <Script>");
				sender.sendMessage("§eExecutes the specified Javascript");
			}
			if (sender.hasPermission("scriptconfig.command.run")) {
				sender.sendMessage(" ");
				sender.sendMessage("§a/scriptconfig run <File> [Function]");
				sender.sendMessage("§eExecutes the specified Javascript file. (Path is relative to the server root directory)");
			}
			return true;
		}
		if ("eval".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("scriptconfig.command.eval")) {
				sender.sendMessage("§cNo permission");
				return false;
			}
			try {
				Object result = scriptConfigProvider.scriptEngineManager.getEngineByName("JavaScript").eval(joinArguments(args, 1, " "));
				sender.sendMessage(result == null ? "§7Script returned null" : result.toString());
				return true;
			} catch (ScriptException e) {
				sender.sendMessage("§cScript Exception: " + e.getMessage());
				sender.sendMessage("§cSee console for details");
				e.printStackTrace();
				return false;
			}
		}
		if ("run".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("scriptconfig.command.run")) {
				sender.sendMessage("§cNo permission");
				return false;
			}
			if (args.length <= 1) {
				sender.sendMessage("§cPlease specify the file");
				return false;
			}
			File file = new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), args[1]);
			if (!file.exists()) {
				sender.sendMessage("§cFile " + file + " not found");
				return false;
			}
			try {
				if (args.length > 2) { // Function specified
					try {
						ScriptConfig scriptConfig = scriptConfigProvider.load(file);
						Object result = scriptConfig.callFunction(args[2]);
						sender.sendMessage(result == null ? "§7Script returned null" : result.toString());
						return true;
					} catch (RuntimeScriptException e) {
						sender.sendMessage("§cScript Exception: " + e.getMessage());
						sender.sendMessage("§c" + e.getException().getMessage());
						sender.sendMessage("§cSee console for details");
						e.printStackTrace();
						return false;
					}
				} else {
					Object result = scriptConfigProvider.scriptEngineManager.getEngineByName("JavaScript").eval(new FileReader(file));
					sender.sendMessage(result == null ? "§7Script returned null" : result.toString());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (ScriptException e) {
				sender.sendMessage("§cScript Exception: " + e.getMessage());
				sender.sendMessage("§cSee console for details");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	String joinArguments(String[] args, int start, String joiner) {
		if (start > args.length) { throw new IllegalArgumentException("start > length"); }

		StringBuilder joined = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) { joined.append(joiner); }
			joined.append(args[i]);
		}
		return joined.toString();
	}
}
