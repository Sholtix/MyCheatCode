package wtf.sqwezz.scripts.interpreter.lib;

import wtf.sqwezz.scripts.interpreter.LuaValue;

class TableLibFunction extends LibFunction {
	public LuaValue call() {
		return argerror(1, "table expected, got no value");
	}
}
