package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.AttackEvent;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.projections.ProjectionUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.renderer.WorldRenderer.frustum;

@FunctionRegister(name = "Particles", type = Category.Render)
public class Particles extends Function {

    private final ModeSetting setting = new ModeSetting("Вид", "Бубенцы", "Орбизы", "Звездочки","Молнии","Доллары");
    private final SliderSetting value = new SliderSetting("Кол-во за удар", 15.0f, 1.0f, 25.0f, 0.5f);
    private final BooleanSetting enableParticles = new BooleanSetting("Enable Particles", true);
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

    public Particles() {
        addSettings(setting, value, enableParticles);
    }

    private boolean isInView(Vector3d pos) {
        frustum.setCameraPosition(mc.getRenderManager().info.getProjectedView().x,
                mc.getRenderManager().info.getProjectedView().y,
                mc.getRenderManager().info.getProjectedView().z);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    private boolean isVisible(Vector3d pos) {
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        RayTraceContext context = new RayTraceContext(cameraPos, pos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, mc.player);
        BlockRayTraceResult result = mc.world.rayTraceBlocks(context);
        return result.getType() == RayTraceResult.Type.MISS;
    }

    @Subscribe
    private void onUpdate(AttackEvent e) {
        if (!enableParticles.get()) {
            return;
        }
        if (e.entity == mc.player) return;
        if (e.entity instanceof LivingEntity livingEntity) {
            Vector3d center = livingEntity.getPositionVec().add(0, livingEntity.getHeight() / 1.4f, 0); // Спавним партиклы чуть выше
            for (int i = 0; i < value.get(); i++) {
                particles.add(new Particle(center));
            }
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }

        for (Particle p : particles) {
            if (System.currentTimeMillis() - p.time > 7000 || p.alpha <= 0) {
                particles.remove(p);
            } else if (mc.player.getPositionVec().distanceTo(p.pos) > 10) {
                particles.remove(p);
            } else if (isInView(p.pos) && isVisible(p.pos)) {
                p.update();
                Vector2f pos = ProjectionUtil.project(p.pos.x, p.pos.y, p.pos.z);

                float size = 1 - ((System.currentTimeMillis() - p.time) / 6000f);
                int alpha = 255;

                final ResourceLocation logo = new ResourceLocation("Vredux/images/star.png");
                final ResourceLocation logo2 = new ResourceLocation("Vredux/images/glow.png");
                final ResourceLocation logo3 = new ResourceLocation("Vredux/images/lightning.png");
                final ResourceLocation logo4 = new ResourceLocation("Vredux/images/dollar.png");

                switch (setting.get()) {

                    case "Молнии" -> {
                        DisplayUtils.drawImageAlpha(logo3, pos.x, pos.y - 15, 45 * size, 50 * size,new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) ((175 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(90, 1),(int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(180, 1), (int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(270, 1), (int) ((255 * p.alpha) * size))));
                    }
                    case "Доллары" -> {
                        DisplayUtils.drawImageAlpha(logo4, pos.x, pos.y - 15, 45 * size, 50 * size,new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) ((175 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(90, 1),(int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(180, 1), (int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(270, 1), (int) ((255 * p.alpha) * size))));
                    }
                    case "Звездочки" -> {
                        DisplayUtils.drawImageAlpha(logo, pos.x, pos.y - 15, 45 * size, 50 * size,new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) ((175 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(90, 1),(int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(180, 1), (int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(270, 1), (int) ((255 * p.alpha) * size))));
                    }

                    case "Орбизы" -> {
                        DisplayUtils.drawImageAlpha(logo2, pos.x, pos.y - 15, 45 * size, 50 * size,new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) ((175 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(90, 1),(int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(180, 1), (int) ((255 * p.alpha) * size)),
                                ColorUtils.setAlpha(HUD.getColor(270, 1), (int) ((255 * p.alpha) * size))
                        ));
                    }
                }
            } else {
                particles.remove(p);
            }
        }
    }

    private class Particle {
        private Vector3d pos;
        private Vector3d end;
        private long time;
        private long collisionTime = -1;
        private Vector3d velocity;
        private float alpha;

        public Particle(Vector3d pos) {
            this.pos = pos;
            this.end = pos.add(-ThreadLocalRandom.current().nextFloat((float) -3.5, 3.5F), -ThreadLocalRandom.current().nextFloat((float) -3.5, 3.5F), -ThreadLocalRandom.current().nextFloat((float) -3.5, 3.5F)); // Разлетаются шире
            this.time = System.currentTimeMillis();
            this.velocity = new Vector3d(
                    ThreadLocalRandom.current().nextDouble(-0.02, 0.02), // Увеличиваем диапазон скорости
                    ThreadLocalRandom.current().nextDouble(0.02, 0.03), // Увеличиваем диапазон скорости
                    ThreadLocalRandom.current().nextDouble(-0.02, 0.02) // Увеличиваем диапазон скорости
            );
            this.alpha = 2f;
        }

        public void update() {

            if (collisionTime != -1) {
                long timeSinceCollision = System.currentTimeMillis() - collisionTime;
                alpha = Math.max(0, 1 - (timeSinceCollision / 3000f));
            }

            velocity = velocity.add(-0.000003, -0.0002, -0.000003);

            Vector3d newPos = pos.add(velocity);

            BlockPos particlePos = new BlockPos(newPos);
            BlockState blockState = mc.world.getBlockState(particlePos);
            if (!blockState.isAir()) {
                if (collisionTime == -2) {
                    collisionTime = System.currentTimeMillis();
                }

                if (!mc.world.getBlockState(new BlockPos(pos.x + velocity.x, pos.y, pos.z)).isAir()) {
                    velocity = new Vector3d(0, velocity.y, velocity.z);
                }
                if (!mc.world.getBlockState(new BlockPos(pos.x, pos.y + velocity.y, pos.z)).isAir()) {
                    velocity = new Vector3d(velocity.x, -velocity.y * 0.5f, velocity.z); // Отскок с уменьшением скорости
                }
                if (!mc.world.getBlockState(new BlockPos(pos.x, pos.y, pos.z + velocity.z)).isAir()) {
                    velocity = new Vector3d(velocity.x, velocity.y, 0);
                }

                pos = pos.add(velocity);
            } else {
                pos = newPos;
            }
        }
    }

}
