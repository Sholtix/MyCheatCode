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
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
@FunctionRegister(name = "TargetESP3", type = Category.Render)
public class TargetESP3 extends Function {
    private final Animation alpha = new DecelerateAnimation(1000, 255);

    private LivingEntity currentTarget;
    private double speed;
    private long lastTime = System.currentTimeMillis();
    private LivingEntity target;

    public TargetESP3() {
    }

    @Subscribe
    private void onUpdate(EventUpdate eventUpdate) {



    }
    @Subscribe

    private void onWorldEvent(WorldEvent e) {
        KillAura killAura = Vredux.getInstance().getFunctionRegistry().getKillAura();
        if (killAura.isState() && killAura.getTarget() != null) {
            MatrixStack stack = new MatrixStack();
            killAura = Vredux.getInstance().getFunctionRegistry().getKillAura();
            EntityRendererManager rm = mc.getRenderManager();
            float c = (float) ((((System.currentTimeMillis() - lastTime) / 450F)) + (Math.sin((((System.currentTimeMillis() - lastTime) / 200F))) / 100f));

            double x = MathHelper.interporate(mc.getRenderPartialTicks(), killAura.getTarget().lastTickPosX, killAura.getTarget().getPosX());
            double y = MathHelper.interporate(mc.getRenderPartialTicks(), killAura.getTarget().lastTickPosY, killAura.getTarget().getPosY()) + killAura.getTarget().getHeight() / 1.6f;
            double z = MathHelper.interporate(mc.getRenderPartialTicks(), killAura.getTarget().lastTickPosZ, killAura.getTarget().getPosZ());


            x -= rm.info.getProjectedView().getX();
            y -= rm.info.getProjectedView().getY();
            z -= rm.info.getProjectedView().getZ();
            float alpha = Shaders.shaderPackLoaded ? 1f : 0.5f;
            alpha *= -2F;

            float pl = -0.1f;

            boolean fa = true;
            for (int b = 0; b < 1; b++) {
                for (float i = c * 360; i < c * 360 + 255; i += 2) {
                    float cur = i;
                    float min = c * 360;
                    float max = c * 360 + 510;
                    float dc = MathHelper.normalize(cur, c * 360 - 255, max);
                    float degrees = i;

                    int color = HUD.getColor(0);
                    int color2 = HUD.getColor(250);

                    float rf = 0.50f;
                    double radians = Math.toRadians(degrees);
                    double plY = pl + Math.sin(radians * 0.2) * 0.6;

                    stack.push();
                    stack.translate(x, y, z);
                    stack.rotate(mc.getRenderManager().info.getRotation());
                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.15f : 0.15f) * (Math.max(fa ? 0.15f : 0.15f, fa ? dc : (1f - -(0.4f - dc)) / 2f) + 0.45f);
                    float w = q * (1.7f + ((0.5f - alpha) * 2));

                    DisplayUtils.drawImage1(stack,
                            new ResourceLocation("Vredux/images/glow.png"),
                            Math.cos(radians) * rf - w /2f,
                            plY - 0.3,
                            Math.sin(radians) * rf - w / 4f, w, w,
                            color,
                            color2,
                            color2,
                            color);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    stack.pop();
                }
                c *= 1f;
                fa = !fa;
                pl += 1f;
                
            }
        }
    }
}