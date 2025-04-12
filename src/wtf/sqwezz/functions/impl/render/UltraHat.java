package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.impl.combat.KillAura;
import wtf.sqwezz.utils.animations.Animation;
import wtf.sqwezz.utils.animations.impl.DecelerateAnimation;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
@FunctionRegister(name = "UltraHat", type = Category.Render)
public class UltraHat extends Function {
    private final Animation alpha = new DecelerateAnimation(1000, 255);

    private LivingEntity currentTarget;
    private double speed;
    private long lastTime = System.currentTimeMillis();
    private LivingEntity target;

    public UltraHat() {
    }

    @Subscribe
    private void onUpdate(EventUpdate eventUpdate) {



    }
    @Subscribe

    private void onWorldEvent(WorldEvent e) {

        {
            MatrixStack stack = new MatrixStack();
            EntityRendererManager rm = mc.getRenderManager();
            float c = (float) ((((System.currentTimeMillis() - lastTime) / 2000F)) + (Math.sin((((System.currentTimeMillis() - lastTime) / 1500F))) / 10f));

            double x = MathHelper.interporate(mc.getRenderPartialTicks(), mc.player.lastTickPosX, mc.player.getPosX());
            double y = MathHelper.interporate(mc.getRenderPartialTicks(), mc.player.lastTickPosY, mc.player.getPosY()) + mc.player.getHeight() / 1.35f;
            double z = MathHelper.interporate(mc.getRenderPartialTicks(), mc.player.lastTickPosZ, mc.player.getPosZ());


            x -= rm.info.getProjectedView().getX();
            y -= rm.info.getProjectedView().getY();
            z -= rm.info.getProjectedView().getZ();
            float alpha = Shaders.shaderPackLoaded ? 1f : 0.5f;
            alpha *= 0.3;

            float pl = 0;

            boolean fa = true;
            for (int b = 0; b < 2; b++) {
                for (float i = c * 360; i < c * 360 + 70; i += 4) {
                    float cur = i;
                    float min = c * 360;
                    float max = c * 360 + 140;
                    float dc = MathHelper.normalize(cur, c * 360 - 70, max);
                    float degrees = i;
                    int color = HUD.getColor(0);
                    int color2 = HUD.getColor(90);
                    if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                        color -= ColorUtils.rgb(0,0,0);
                        color2 -= ColorUtils.rgb(0,0,0);
                    }

                    float rf = 0.40f;
                    double radians = Math.toRadians(degrees);
                    double plY = pl + Math.sin(radians * 1f) * 0.07f;

                    stack.push();
                    stack.translate(x, y, z);
                    stack.rotate(mc.getRenderManager().info.getRotation());
                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.15f : 0.15f) * (Math.max(fa ? 0.15f : 0.15f, fa ? dc : (1f - -(0.4f - dc)) / 2f) + 0.45f);
                    float w = q * (1.7f + ((0.5f - alpha) * 2));
                    DisplayUtils.drawImage1(stack,
                            new ResourceLocation("Vredux/images/glow.png"),
                            Math.cos(radians) * rf - w / 2f,
                            plY + 0.30,
                            Math.sin(radians) * rf - w / 2f, w, w,
                            color,
                            color2,
                            color2,
                            color);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    stack.pop();
                }
                c *= -1f;
                fa = !fa;
                pl += 0f;
            }
        }
    }
}