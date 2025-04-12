package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.projections.ProjectionUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@FunctionRegister(name = "Predictions", type = Category.Render)
public class Predictions extends Function {

    record PearlPoint(Vector3d position, int ticks) {
    }

    final List<PearlPoint> pearlPoints = new ArrayList<>();

    @Subscribe
    public void aa(EventDisplay e) {
        for (PearlPoint pearlPoint : pearlPoints) {
            Vector3d pos = pearlPoint.position;
            Vector2f projection = ProjectionUtil.project(pos.x, pos.y - 0.3F, pos.z);


            if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
                continue;
            }

            float width = Fonts.montserrat.getWidth("1488111", 7);

            float textWidth = width + 11 + 11;

            float posX = projection.x - textWidth / 2;
            float posY = projection.y;
            Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
            Vector4i vec4f = new Vector4i(ColorUtils.rgb(33,33,33), ColorUtils.rgb(33,33,33), ColorUtils.rgb(33,33,33), ColorUtils.rgb(33,33,33));
            DisplayUtils.drawGradientRound((int) posX+20, (int) posY-5 , 15, 45, 30, vector4i.x, vector4i.y, vector4i.z, vector4i.w);
            DisplayUtils.drawGradientRound((int) posX+17.5f, (int) posY+0.5f , 20, 20, 10, vector4i.x, vector4i.y, vector4i.z, vector4i.w);
            DisplayUtils.drawGradientRound((int) posX+19, (int) posY+2 , 17, 17, 8, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(Items.ENDER_PEARL.getDefaultInstance(), (int) posX+19, (int) posY + 2);
        }
    }

    @Subscribe
    public void onRender(WorldEvent event) {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        Vector3d renderOffset = mc.getRenderManager().info.getProjectedView();

        glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);

        glLineWidth(3);

        buffer.begin(1, DefaultVertexFormats.POSITION);

        pearlPoints.clear();
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderPearlEntity throwable) {
                Vector3d motion = throwable.getMotion();
                Vector3d pos = throwable.getPositionVec();
                Vector3d prevPos;
                int ticks = 0;

                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);
                    motion = getNextMotion(throwable, motion);
                    int themeColor = HUD.getColor(i);
                    ColorUtils.setColor(themeColor);

                    buffer.pos(prevPos.x, prevPos.y, prevPos.z).endVertex();

                    RayTraceContext rayTraceContext = new RayTraceContext(
                            prevPos,
                            pos,
                            RayTraceContext.BlockMode.COLLIDER,
                            RayTraceContext.FluidMode.NONE,
                            throwable
                    );

                    BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

                    boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;

                    if (isLast) {
                        pos = blockHitResult.getHitVec();
                    }

                    buffer.pos(pos.x, pos.y, pos.z).endVertex();

                    if (blockHitResult.getType() == BlockRayTraceResult.Type.BLOCK || pos.y < -128) {
                        pearlPoints.add(new PearlPoint(pos, ticks));
                        break;
                    }
                    ticks++;
                }
            }
        }

        tessellator.draw();

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glPopMatrix();
    }

    private Vector3d getNextMotion(ThrowableEntity throwable, Vector3d motion) {
        if (throwable.isInWater()) {
            motion = motion.scale(0.8);
        } else {
            motion = motion.scale(0.99);
        }

        if (!throwable.hasNoGravity()) {
            motion.y -= throwable.getGravityVelocity();
        }

        return motion;
    }
}