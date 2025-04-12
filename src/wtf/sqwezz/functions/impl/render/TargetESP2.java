package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.impl.combat.KillAura;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.utils.EntityUtils;
import wtf.sqwezz.utils.animations.Animation;
import wtf.sqwezz.utils.animations.Direction;
import wtf.sqwezz.utils.animations.impl.DecelerateAnimation;
import wtf.sqwezz.utils.animations.impl.EaseBackIn;
import wtf.sqwezz.utils.animations.impl.EaseInOutCubic;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.projections.ProjectionUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@FunctionRegister(name = "TargetESP 2", type = Category.Render)
public class TargetESP2 extends Function {
    private final Animation alpha = new DecelerateAnimation(1000, 225);

    private LivingEntity currentTarget;

    private double speed;
    private long lastTime = System.currentTimeMillis();
    private LivingEntity target;

    public TargetESP2() {
    }

    @Subscribe
    private void onUpdate(EventUpdate eventUpdate) {
        KillAura killAura = Vredux.getInstance().getFunctionRegistry().getKillAura();

        if (killAura.getTarget() != null) {
            currentTarget = killAura.getTarget();
        }

        alpha.setDirection(!killAura.isState() || killAura.getTarget() == null ? Direction.BACKWARDS : Direction.FORWARDS);
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {

        if (e.getType() != EventDisplay.Type.PRE) {
            return;
        }
        if (this.currentTarget != null && this.currentTarget != mc.player) {
            double sin = Math.sin(System.currentTimeMillis() / 800.0);
            float size = 100.0F;

            Vector3d interpolated = currentTarget.getPositon(e.getPartialTicks());
            Vector2f pos = ProjectionUtil.project(interpolated.x, interpolated.y + currentTarget.getHeight() / 2f, interpolated.z);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(pos.x, pos.y, 0);
            GlStateManager.rotatef((float) sin * 120, 0, 0, 1);
            GlStateManager.translatef(-pos.x, -pos.y, 0);
            
            if (pos != null) {
                DisplayUtils.drawImageAlpha(new ResourceLocation("Vredux/images/target.png"), pos.x - size / 2f, pos.y - size / 2f, size/1, size/1, new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) this.alpha.getOutput()), ColorUtils.setAlpha(HUD.getColor(0, 1), (int) this.alpha.getOutput()), ColorUtils.setAlpha(HUD.getColor(122, 1), (int) this.alpha.getOutput()), ColorUtils.setAlpha(HUD.getColor(255, 1), (int) this.alpha.getOutput())));
                GlStateManager.popMatrix();
            }
        }
    }
}