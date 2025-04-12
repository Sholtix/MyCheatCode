package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.command.friends.FriendStorage;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ColorSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting; // Добавляем SliderSetting
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.player.MoveUtils;
import wtf.sqwezz.utils.player.PlayerUtils;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@FunctionRegister(name = "Arrows", type = Category.Render)
public class Pointers extends Function {
    private final ModeSetting colores = new ModeSetting("Тип", "Клиент ( Alpha )", "Клиент ( Alpha )");
    private final ColorSetting color1 = new ColorSetting("Цвет", ColorUtils.rgb(255, 255, 255)).setVisible(() -> colores.is("Свой"));
    private final ColorSetting colorfr1 = new ColorSetting("Цвет друзей", ColorUtils.rgb(73, 252, 3)).setVisible(() -> colores.is("Свой"));
    // Добавляем новые настройки
    private final SliderSetting sizeScale = new SliderSetting("Размер", 1.0f, 0.5f, 2.0f, 0.1f); // От 0.5x до 2x
    private final SliderSetting distanceScale = new SliderSetting("Расстояние", 1.0f, 0.5f, 2.0f, 0.1f); // От 0.5x до 2x

    public float animationStep;
    private float lastYaw;
    private float lastPitch;

    public Pointers() {
        addSettings(colores, color1, colorfr1, sizeScale, distanceScale); // Добавляем новые настройки
    }

    private float animatedYaw;
    private float animatedPitch;
    LivingEntity entity;

    @Subscribe
    public void onDisplay(EventDisplay e) {
        if (mc.player == null || mc.world == null || e.getType() != EventDisplay.Type.PRE) {
            return;
        }

        float size = 40;

        if (mc.currentScreen instanceof InventoryScreen) {
            size += 100;
        }

        if (MoveUtils.isMoving()) {
            size += 0;
        }

        animationStep = MathUtil.fast(animationStep, size, 6);

        if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                if (!PlayerUtils.isNameValid(player.getNameClear()) || mc.player == player)
                    continue;

                double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().info.getProjectedView().getX();
                double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().info.getProjectedView().getZ();

                double cos = MathHelper.cos((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
                double sin = MathHelper.sin((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
                double rotY = -(z * cos - x * sin);
                double rotX = -(x * cos + z * sin);

                float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);

                // Применяем distanceScale для изменения расстояния от центра
                float adjustedDistance = animationStep * distanceScale.get();
                double x2 = adjustedDistance * MathHelper.cos((float) Math.toRadians(angle)) + window.getScaledWidth() / 2f;
                double y2 = adjustedDistance * MathHelper.sin((float) Math.toRadians(angle)) + window.getScaledHeight() / 2f;

                x2 += animatedYaw;
                y2 += animatedPitch;

                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                GlStateManager.translated(x2, y2, 0);
                GlStateManager.rotatef(angle, 0, 0, 1);
                // Применяем sizeScale для изменения размера стрелок
                GlStateManager.scalef(sizeScale.get(), sizeScale.get(), 1.0f);

                if (colores.is("Клиент ( Alpha )")) {
                    int color = FriendStorage.isFriend(player.getGameProfile().getName()) ? FriendStorage.getColor() : ColorUtils.rgba(0, 0, 0, 0);
                    drawTriangle(-4, -1F, 4F, 7F, new Color(0, 0, 0, 32));
                    drawTriangle(-3F, 0F, 3F, 5F, new Color(color));

                    DisplayUtils.drawImageAlpha(new ResourceLocation("Vredux/images/triangle2.png"), -8.0F, -9.0F, 23, 23, new Vector4i(ColorUtils.setAlpha(HUD.getColor(0, 1), (int) 255),
                            ColorUtils.setAlpha(HUD.getColor(0, 1), (int) 255),
                            ColorUtils.setAlpha(HUD.getColor(122, 1), (int) 255),
                            ColorUtils.setAlpha(HUD.getColor(255, 1), (int) 255)
                    ));
                    DisplayUtils.drawImage(new ResourceLocation("Vredux/images/triangle1.png"), -8.0F, -9.0F, 23, 23, color);
                }
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            }
        }
        lastYaw = mc.player.rotationYaw;
        lastPitch = mc.player.rotationPitch;
    }

    public static void drawTriangle(float x, float y, float width, float height, Color color) {
        GL11.glPushMatrix();
        GL11.glPopMatrix();
    }
}