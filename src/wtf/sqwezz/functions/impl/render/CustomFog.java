package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ColorSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.render.ColorUtils;

@FunctionRegister(name = "Custom Fog", type = Category.Render)
public class CustomFog extends Function {
    public SliderSetting power = new SliderSetting("Сила", 20F, 1F,40F, 1F);
    public final ModeSetting mode = new ModeSetting("Мод","Клиент","Клиент","Свой");
    public ColorSetting color = new ColorSetting("Цвет", ColorUtils.rgb(255,255,255)).setVisible(()-> mode.is("Свой"));

    public CustomFog() {
        addSettings(power,mode,color);
    }

    public boolean state;
    @Override
    public boolean onEnable() {
        super.onEnable();
        //Shaders.setShaderPack(Shaders.SHADER_PACK_NAME_DEFAULT);
        return false;
    }

    @Subscribe
    public void onEvent(EventUpdate event) {
    }

    public int getDepth() {
        return 6;
    }
}



