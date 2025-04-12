package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import wtf.sqwezz.events.JumpEvent;
import wtf.sqwezz.events.WorldEvent;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.render.ColorUtils;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

import java.util.concurrent.CopyOnWriteArrayList;

@FunctionRegister(name = "JumpCircles", type = Category.Render)
public class JumpCircle extends Function {

    private final SliderSetting radik = new SliderSetting("Размер", 2.5f, 0.5f, 10f, 0.5f);
    private final SliderSetting life = new SliderSetting("Время Жизни", 20, 0.5f, 40, 0.5f);
    private final ModeSetting textureMode = new ModeSetting("Текстура", "Default", "Default", "Fat", "Thin", "Text");

    private final CopyOnWriteArrayList<Circle> circles = new CopyOnWriteArrayList<>();

    public JumpCircle() {
        addSettings(radik, life, textureMode);
    }

    @Subscribe
    private void onJump(JumpEvent e) {
        // Интерполируем позицию игрока
        Vector3d currentPos = new Vector3d(mc.player.getPosition().getX(), mc.player.getPosition().getY(), mc.player.getPosition().getZ());
        Vector3d prevPos = new Vector3d(mc.player.prevPosX, mc.player.prevPosY, mc.player.prevPosZ);
        float partialTicks = mc.getRenderPartialTicks();
        Vector3d interpolatedPos = prevPos;
        circles.add(new Circle(interpolatedPos.add(0, 0.07, 0)));
    }

    @Subscribe
    private void onRender(WorldEvent e) {
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableCull();

        GlStateManager.translated(
                -mc.getRenderManager().info.getProjectedView().x,
                -mc.getRenderManager().info.getProjectedView().y,
                -mc.getRenderManager().info.getProjectedView().z
        );

        for (Circle c : circles) {
            ResourceLocation texture = getTextureByMode(textureMode.get());
            mc.getTextureManager().bindTexture(texture);

            long elapsed = System.currentTimeMillis() - c.time;
            if (elapsed > life.get() * 1000) {
                circles.remove(c);
                continue;
            }

            // Запускаем анимацию исчезновения
            if (elapsed > 3500 && !c.isBack) {
                c.animation.animate(0.000001f, 2f, Easings.QUAD_IN); // Плавное исчезновение
                c.isBack = true;
            }

            c.animation.update();
            float rad = (float) (radik.get() * (float) c.animation.getValue());

            Vector3d vector3d = c.vector3d;
            float angle = (float) (elapsed / 3000.0 * 360.0);

            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
            GlStateManager.rotatef(angle, 0, 0.0001f, 0);
            GlStateManager.translatef((float) -vector3d.x, (float) -vector3d.y, (float) -vector3d.z);
            vector3d = vector3d.add(-rad / 2f, 0, -rad / 2f);

            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR_TEX);


            int baseColor = ColorUtils.getColor(5);
            // Плавное затухание прозрачности с квадратичной интерполяцией
            float alphaProgress = elapsed <= 1500 ? 0 : (elapsed - 1500) / 2000f;
            alphaProgress = MathHelper.clamp(alphaProgress, 0, 1);
            float alphaFactor = 1 - alphaProgress * alphaProgress; // Квадратичное затухание
            int alpha = (int) (255 * alphaFactor);

            float a = alpha / 255.0f;
            float r = (baseColor >> 16 & 255) / 255.0f;
            float g = (baseColor >> 8 & 255) / 255.0f;
            float b = (baseColor & 255) / 255.0f;

            buffer.pos(vector3d.x, vector3d.y, vector3d.z).color(r, g, b, a).tex(0, 0).endVertex();
            buffer.pos(vector3d.x + rad, vector3d.y, vector3d.z).color(r, g, b, a).tex(1, 0).endVertex();
            buffer.pos(vector3d.x + rad, vector3d.y, vector3d.z + rad).color(r, g, b, a).tex(1, 1).endVertex();
            buffer.pos(vector3d.x, vector3d.y, vector3d.z + rad).color(r, g, b, a).tex(0, 1).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private ResourceLocation getTextureByMode(String mode) {
        switch (mode.toLowerCase()) {
            case "fat":
                return new ResourceLocation("Vredux/images/circle_fat.png");
            case "thin":
                return new ResourceLocation("Vredux/images/circle_thin.png");
            case "text":
                return new ResourceLocation("Vredux/images/circle_text.png");
            default:
                return new ResourceLocation("Vredux/images/circle.png");
        }
    }

    private static class Circle {
        public final Vector3d vector3d;
        public final long time;
        public final Animation animation;
        public boolean isBack;

        public Circle(Vector3d vector3d) {
            this.vector3d = vector3d;
            this.time = System.currentTimeMillis();
            this.animation = new Animation();
            this.isBack = false;
            this.animation.animate(1, 2f, Easings.QUAD_OUT); // Плавное появление
        }
    }
}
