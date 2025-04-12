package wtf.sqwezz.scripts.interpreter.globals;

import wtf.sqwezz.scripts.interpreter.compiler.LuaC;
import wtf.sqwezz.scripts.interpreter.Globals;
import wtf.sqwezz.scripts.interpreter.LoadState;
import wtf.sqwezz.scripts.interpreter.lib.*;
import wtf.sqwezz.scripts.lua.libraries.ModuleLibrary;
import wtf.sqwezz.scripts.lua.libraries.PlayerLibrary;

public class Standarts {
    public static Globals standardGlobals() {
        Globals globals = new Globals();
        globals.load(new BaseLib());
        globals.load(new Bit32Lib());
        globals.load(new MathLib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new PlayerLibrary());
        globals.load(new ModuleLibrary());
        LoadState.install(globals);
        LuaC.install(globals);
        return globals;
    }
}
