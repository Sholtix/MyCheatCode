package wtf.sqwezz.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.StringSetting;
import net.minecraft.client.Minecraft;

@FunctionRegister(name = "NameProtect", type = Category.Misc)
public class NameProtect extends Function {

    public static String fakeName = "";

    public String name = "Protected";

    @Subscribe
    private void onUpdate(EventUpdate e) {
        fakeName = name;
    }

    public static String getReplaced(String input) {
        if (Vredux.getInstance() != null && Vredux.getInstance().getFunctionRegistry().getNameProtect().isState()) {
            input = input.replace(Minecraft.getInstance().session.getUsername(), fakeName);
        }
        return input;
    }
}
