package wtf.sqwezz.functions.impl.render;

import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "AspectRatio", type = Category.Render)
public class AspectRatio extends Function {
    public SliderSetting width = new SliderSetting("Ширина", 1, 0.6f, 2.5f, 0.1f);
    public AspectRatio() {
        addSettings(width);
    }
    @Override
    public boolean onEnable() {
        super.onEnable();
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}