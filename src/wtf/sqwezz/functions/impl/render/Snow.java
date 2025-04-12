package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.renderer.WorldRenderer.frustum;

@FunctionRegister(name = "Snow", type = Category.Render)
public class Snow extends Function {
    private final ModeSetting fallModeSetting = new ModeSetting("Режим", "Простой", "Простой", "Отскоки");
    public static final ModeSetting setting = new ModeSetting("Вид", "Бубенцы", "Бубенцы", "Звездочки", "Сердечки","Доллары","Молнии");
    public final SliderSetting size = new SliderSetting("Количество", 350f, 100f, 5000f, 50f);
    MatrixStack matrixStack = new MatrixStack();
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<ParticleBase> particles2 = new CopyOnWriteArrayList<>();
    private float dynamicSpeed = (fallModeSetting.is("Отскоки")) ? 0.1f : 0.4f;
    public Snow() {
        addSettings(fallModeSetting, setting, size);
    }

    private boolean isInView(Vector3d pos) {
        frustum.setCameraPosition(mc.getRenderManager().info.getProjectedView().x,
                mc.getRenderManager().info.getProjectedView().y,
                mc.getRenderManager().info.getProjectedView().z);
        return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }
    @Subscribe
    private void onUpdate(EventUpdate e) {
        particles.clear();
        particles2.removeIf(ParticleBase::tick);
        for (int n = particles2.size(); (float) n < size.get(); ++n) {
            if (mc.currentScreen instanceof IngameMenuScreen) return;
            particles2.add(new ParticleBase((float) (mc.player.getPosX() + (double) MathUtil.random(-48.0F, 48.0F)), (float) (mc.player.getPosY() + (double) MathUtil.random(-20.0F, 48.0F)), (float) (mc.player.getPosZ() + (double) MathUtil.random(-48.0F, 48.0F)),
                    MathUtil.random(-dynamicSpeed, dynamicSpeed), MathUtil.random(-0.1F, 0.1F), MathUtil.random(-dynamicSpeed, dynamicSpeed)));
        }
        particles2.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > 5000);
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        render(matrixStack);
    }

    public static void render(MatrixStack matrixStack) {
        if (mc.currentScreen instanceof IngameMenuScreen) return;
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
        particles2.forEach((particleBase) -> {
            particleBase.render(bufferBuilder);
        });
        bufferBuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferBuilder);
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }

    private class Particle {
        private Vector3d pos;
        private final Vector3d end;
        private final long time;
        private float alpha;
        public Particle() {
            pos = mc.player.getPositionVec().add(-ThreadLocalRandom.current().nextFloat(-20, 20), ThreadLocalRandom.current().nextFloat(-5, 20), -ThreadLocalRandom.current().nextFloat(-20, 20));
            end = pos.add(-ThreadLocalRandom.current().nextFloat(-3, 3), -ThreadLocalRandom.current().nextFloat(-3, 3), -ThreadLocalRandom.current().nextFloat(-3, 3));
            time = System.currentTimeMillis();
        }
        public void update() {
            alpha = MathUtil.fast(alpha, 1, 10);
            pos = MathUtil.fast(pos, end, 0.5f);
        }
    }

    @Override
    public void onDisable() {
        particles.clear();
        super.onDisable();
    }

    public class ParticleBase {
        public long time;
        protected float prevposX, prevposY, prevposZ;
        protected float posX, posY, posZ;
        protected float motionX, motionY, motionZ;
        protected int age, maxAge;
        private float alpha;
        private long collisionTime = -1L;

        public ParticleBase(float x, float y, float z, float motionX, float motionY, float motionZ) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.prevposX = x;
            this.prevposY = y;
            this.prevposZ = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.time = System.currentTimeMillis();
            this.maxAge = this.age = (int) MathUtil.random(120, 200);
        }

        public void update() {
            alpha = MathUtil.fast(alpha, 1, 10);
            if (fallModeSetting.is("Отскоки")) updateWithBounce();
        }

        public boolean tick() {
            this.age = mc.player.getDistanceSq((double)this.posX, (double)this.posY, (double)this.posZ) > 4096.0 ? (this.age -= 8) : --this.age;
            if (this.age < 0) {
                return true;
            } else {
                this.prevposX = this.posX;
                this.prevposY = this.posY;
                this.prevposZ = this.posZ;
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
                if (!fallModeSetting.is("Отскоки")) {
                    this.motionX *= 0.9F;
                    this.motionY *= 0.9F;
                    this.motionZ *= 0.9F;
                    this.motionY -= 0.001F;
                } else {
                    this.motionX = 0;
                    this.motionZ = 0;
                }
                return false;
            }
        }

        private void updateWithBounce() {
            if (this.collisionTime != -1L) {
                long timeSinceCollision = System.currentTimeMillis() - this.collisionTime;
                this.alpha = Math.max(0.0f, 1.0f - (float) timeSinceCollision / 3000.0f);
            }

            this.motionY -= 8.0E-4;
            float newPosX = this.posX + this.motionX;
            float newPosY = this.posY + this.motionY;
            float newPosZ = this.posZ + this.motionZ;

            BlockPos particlePos = new BlockPos(newPosX, newPosY, newPosZ);
            BlockState blockState = mc.world.getBlockState(particlePos);

            if (!blockState.isAir()) {
                if (this.collisionTime == -1L) {
                    this.collisionTime = System.currentTimeMillis();
                }

                if (!mc.world.getBlockState(new BlockPos(this.posX + this.motionX, this.posY, this.posZ)).isAir()) {
                    this.motionX = 0.0f;
                }
                if (!mc.world.getBlockState(new BlockPos(this.posX, this.posY + this.motionY, this.posZ)).isAir()) {
                    this.motionY = -this.motionY * 0.8f;
                }
                if (!mc.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ + this.motionZ)).isAir()) {
                    this.motionZ = 0.0f;
                }

                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            } else {
                this.posX = newPosX;
                this.posY = newPosY;
                this.posZ = newPosZ;
            }
        }

        public void render(BufferBuilder bufferBuilder) {
            if (setting.is("Бубенцы")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("Vredux/images/glow.png"));
            } else if (setting.is("Звездочки")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("Vredux/images/star.png"));
            } else if (setting.is("Сердечки")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("Vredux/images/heart.png"));
            } else if (setting.is("Доллары")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("Vredux/images/dollar.png"));
            } else if (setting.is("Молнии")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("Vredux/images/lightning.png"));
            }


            float size = 1 - ((System.currentTimeMillis() - time) / 5000f);
            update();
            ActiveRenderInfo camera = mc.gameRenderer.getActiveRenderInfo();
            int color = ColorUtils.setAlpha(HUD.getColor(age * 2), (int) ((int) (alpha * 255) * size));
            Vector3d pos = MathUtil.interpolatePos(prevposX, prevposY, prevposZ, posX, posY, posZ);

            MatrixStack matrices = new MatrixStack();
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
            matrices.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));

            Matrix4f matrix1 = matrices.getLast().getMatrix();

            bufferBuilder.pos(matrix1, 0, -0.9f * size, 0).color(color).tex(0, 1).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.9f * size, -0.9f * size, 0).color(color).tex(1, 1).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.9f * size, 0, 0).color(color).tex(1, 0).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, 0, 0, 0).color(color).tex(0, 0).lightmap(0, 240).endVertex();
        }
    }
}