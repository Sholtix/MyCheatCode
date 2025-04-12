package wtf.sqwezz.scripts.lua.libraries;

import wtf.sqwezz.scripts.interpreter.LuaValue;
import wtf.sqwezz.scripts.interpreter.compiler.jse.CoerceJavaToLua;
import wtf.sqwezz.scripts.interpreter.lib.OneArgFunction;
import wtf.sqwezz.scripts.interpreter.lib.TwoArgFunction;
import wtf.sqwezz.scripts.lua.classes.ModuleClass;

public class ModuleLibrary extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("register", new register());

        env.set("module", library);
        return library;
    }

    public class register extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            return CoerceJavaToLua.coerce(new ModuleClass(arg.toString()));
        }

    }

}
