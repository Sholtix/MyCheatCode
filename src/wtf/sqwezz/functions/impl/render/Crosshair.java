package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.settings.PointOfView;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;

import java.awt.*;

@FunctionRegister(name = "Crosshair", type = Category.Render)
public class Crosshair extends Function {

    private final SliderSetting radius = new SliderSetting("Радиус", 3f, 3f, 6f, 0.1f);
    private final SliderSetting thickness = new SliderSetting("Толщина", 1.0f, 0.5f, 5.0f, 0.1f); // <-- новая настройка

    private final BooleanSetting staticCrosshair = new BooleanSetting("Статический", false);

    private float lastYaw;
    private float lastPitch;
    private float animatedYaw;
    private float animatedPitch;
    private float animation;
    private float animationSize;

    private final int outlineColor;
    private final int entityColor;

    public Crosshair() {
        this.outlineColor = Color.BLACK.getRGB();
        this.entityColor = Color.RED.getRGB();
        this.addSettings(new Setting[]{ this.staticCrosshair, this.radius, this.thickness }); // <-- добавлено сюда
    }

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.POST) return;

        float x = mc.getMainWindow().getScaledWidth() / 2f;
        float y = mc.getMainWindow().getScaledHeight() / 2f;

        if (!(Boolean) this.staticCrosshair.get()) {
            x += this.animatedYaw;
            y += this.animatedPitch;
        }

        this.animationSize = MathUtil.fast(this.animationSize, (1.0F - mc.player.getCooledAttackStrength(1.0F)) * 260.0F, 10.0F);

        if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
            float thicknessValue = thickness.get(); // получаем текущую толщину
            DisplayUtils.drawCircle2(x, y, 0.0F, 360.0F, radius.get(), thicknessValue, false, ColorUtils.getColor(90));
            DisplayUtils.drawCircle3(x, y, 0.0F, this.animationSize, radius.get(), thicknessValue + 1.0f, false, ColorUtils.rgb(23, 21, 21));
        }
    }
}
