//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventDisplay.Type;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ColorSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.CustomFramebuffer;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.KawaseBlur;
import wtf.sqwezz.utils.shader.impl.Outline;
import net.minecraft.client.settings.PointOfView;
import org.lwjgl.opengl.GL11;

@FunctionRegister(
        name = "Glass Hand",
        type = Category.Render
)
public class GlassHand extends Function {
    public CustomFramebuffer hands = (new CustomFramebuffer(false)).setLinear();
    public CustomFramebuffer mask = (new CustomFramebuffer(false)).setLinear();
    private final BooleanSetting OutlineRender = new BooleanSetting("Обводка", false);
    private final BooleanSetting GlassRender = new BooleanSetting("Стекло", false);
    public static final ModeSetting colorrender = new ModeSetting("Режим цвета", "Темы", new String[]{"Темы", "Свой"});
    public static final ColorSetting colorone = (new ColorSetting("Цвет", ColorUtils.rgb(255, 255, 255))).setVisible(() -> {
        return colorrender.is("Свой");
    });
    public final SliderSetting handblur = (new SliderSetting("Сила блюра", 3.0F, 1.0F, 5.0F, 0.1F)).setVisible(() -> {
        return (Boolean)this.GlassRender.get();
    });
    public final SliderSetting glowradius = (new SliderSetting("Радиус Glow", 2.0F, 1.0F, 7.0F, 1.0F)).setVisible(() -> {
        return (Boolean)this.OutlineRender.get();
    });

    public GlassHand() {
        this.addSettings(new Setting[]{this.OutlineRender, this.GlassRender, colorrender, colorone, this.handblur, this.glowradius});
    }

    @Subscribe
    public void onRender(EventDisplay e) {
        if (e.getType() == Type.HIGH) {
            if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                float blur = (Float)this.handblur.get();
                KawaseBlur.blur.updateBlur(blur, (int)blur + 1);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.enableAlphaTest();
                if (colorrender.is("Свой")) {
                    ColorUtils.setColor((Integer)colorone.get());
                } else {
                    ColorUtils.setColor(ColorUtils.getColor(0));
                }

                if ((Boolean)this.GlassRender.get()) {
                    KawaseBlur.blur.render(() -> {
                        this.hands.draw();
                    });
                }

                Outline.registerRenderCall(() -> {
                    this.hands.draw();
                });
                if ((Boolean)this.OutlineRender.get()) {
                    float radiuz = (Float)this.glowradius.get();
                    if (colorrender.is("Свой")) {
                        Outline.draw((int)radiuz, (Integer)colorone.get());
                    } else {
                        Outline.draw((int)radiuz, HUD.getColor(1));
                    }
                }

                GlStateManager.disableAlphaTest();
                GlStateManager.popMatrix();
            }

        }
    }

    public static void setSaturation(float saturation) {
        float[] saturationMatrix = new float[]{0.3086F * (1.0F - saturation) + saturation, 0.6094F * (1.0F - saturation), 0.082F * (1.0F - saturation), 0.0F, 0.0F, 0.3086F * (1.0F - saturation), 0.6094F * (1.0F - saturation) + saturation, 0.082F * (1.0F - saturation), 0.0F, 0.0F, 0.3086F * (1.0F - saturation), 0.6094F * (1.0F - saturation), 0.082F * (1.0F - saturation) + saturation, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F};
        GL11.glLoadMatrixf(saturationMatrix);
    }
}
