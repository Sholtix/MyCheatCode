//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.ColorSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.render.ColorUtils;

@FunctionRegister(
        name = "WorldColor",
        type = Category.Render
)
public class WorldColor extends Function {
    public static boolean WorldColor = false;
    public static final ColorSetting worldcolor = new ColorSetting("Цвет блоков", ColorUtils.rgb(255, 255, 255));
    public static SliderSetting transparentworld = new SliderSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.1F);

    public WorldColor() {
        this.addSettings(new Setting[]{worldcolor});
    }

    @Subscribe
    private void onRender(WorldEvent e) {
    }

    public boolean onEnable() {
        super.onEnable();
        WorldColor = true;
        return false;
    }

    public void onDisable() {
        super.onDisable();
        WorldColor = false;
    }
}
