package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.animations.Animation;
import wtf.sqwezz.utils.animations.Direction;
import wtf.sqwezz.utils.animations.impl.EaseBackIn;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.Stencil;
import wtf.sqwezz.utils.render.font.Fonts;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.drag.Dragging;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHUDNextgenRenderer implements ElementRenderer {
    final StopWatch stopWatch = new StopWatch();
    final Dragging drag;
    LivingEntity entity = null;
    boolean shouldToBack;
    final Animation animation = new EaseBackIn(400, 1, 1);
    float healthAnimation = 0.0f, absorptionAnimation = 0.0f;
    float posX, posY;

    static final float width = 95.6f;
    static final float height = 34.3f;
    static final float spacing = 5;
    static final float rounding = 6;
    static final float headSize = 28;

    public TargetHUDNextgenRenderer(Dragging drag2) {
        this.drag = drag2;
    }

    @Subscribe
    public void render(EventDisplay eventDisplay) {
        posX = drag.getX();
        posY = drag.getY();
        entity = getTarget(entity);
        boolean backAnimation = !shouldToBack || stopWatch.isReached(1000);
        animation.setDuration(backAnimation ? 400 : 300);
        animation.setDirection(backAnimation ? Direction.BACKWARDS : Direction.FORWARDS);

        if (animation.getOutput() == 0.0f) {
            entity = null;
        }

        if (entity != null) {
            drag.setWidth(width);
            drag.setHeight(height);

            float hp = entity.getHealth();
            float maxHp = entity.getMaxHealth();
            Score score = IMinecraft.mc.world.getScoreboard().getOrCreateScore(entity.getScoreboardName(), IMinecraft.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
            String header = IMinecraft.mc.ingameGUI.getTabList().header == null ? " " : IMinecraft.mc.ingameGUI.getTabList().header.getString().toLowerCase();

            if (IMinecraft.mc.getCurrentServerData() != null && IMinecraft.mc.getCurrentServerData().serverIP.contains("funtime")
                    && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
                hp = score.getScorePoints();
                maxHp = 20;
            }
            healthAnimation = MathUtil.fast(healthAnimation, MathHelper.clamp(hp / maxHp, 0, 1), 10);
            absorptionAnimation = MathUtil.fast(absorptionAnimation, MathHelper.clamp(entity.getAbsorptionAmount() / maxHp, 0, 1), 10);
            if (IMinecraft.mc.getCurrentServerData() != null && IMinecraft.mc.getCurrentServerData().serverIP.contains("funtime")
                    && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
                hp = score.getScorePoints();
                maxHp = 20;
            }

            GlStateManager.pushMatrix();
            setSizeAnimation(animation.getOutput());
            drawBackGround(rounding);
            renderTextThatDisplaysTargetInfo(eventDisplay.getMatrixStack());
            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(posX + spacing - 2f, posY + 2.5f, headSize + 0.5f, headSize + 0.5f,6, new Color(13,13,13).getRGB());
            Stencil.readStencilBuffer(1);
            drawTargetHead(entity, posX + spacing - 2, posY + spacing + 1 - 3, headSize, headSize);
            Stencil.uninitStencilBuffer();
            renderHealthBar();
            drawItemStack(posX + headSize + 36 + 8 - 17, posY + 16 - 2.5f, -8);
            GlStateManager.popMatrix();
        }
    }
    private void drawItemStack(float x, float y, float offset) {
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(entity.getHeldItemMainhand(), entity.getHeldItemOffhand()));
        entity.getArmorInventoryList().forEach(stacks::add);
        stacks.removeIf(w -> w.getItem() instanceof AirItem);
        Collections.reverse(stacks);
        final AtomicReference<Float> posX = new AtomicReference<>(x);

        stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .forEach(stack -> drawItemStack(stack,
                        posX.getAndAccumulate(offset, Float::sum),
                        y,
                        true,
                        true, 0.50f));
    }

    public static void drawItemStack(ItemStack stack, float x, float y, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);
        if (scale) GL11.glScaled(scaleValue, scaleValue, scaleValue);
        IMinecraft.mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 35, 0);
        if (withoutOverlay) IMinecraft.mc.getItemRenderer().renderItemOverlays(IMinecraft.mc.fontRenderer, stack, 35, 0);
        RenderSystem.popMatrix();
    }

    private LivingEntity getTarget(LivingEntity nullTarget) {
        LivingEntity auraTarget = Vredux.getInstance().getFunctionRegistry().getKillAura().getTarget();
        LivingEntity target = nullTarget;
        if (auraTarget != null) {
            stopWatch.reset();
            shouldToBack = true;
            target = auraTarget;
        } else if (IMinecraft.mc.currentScreen instanceof ChatScreen) {
            stopWatch.reset();
            shouldToBack = true;
            target = IMinecraft.mc.player;
        } else {
            shouldToBack = false;
        }
        return target;
    }

    public void drawTargetHead(LivingEntity entity, float x, float y, float width, float height) {
        if (entity != null) {
            EntityRenderer<? super LivingEntity> rendererManager = IMinecraft.mc.getRenderManager().getRenderer(entity);
            drawFace(rendererManager.getEntityTexture(entity), x, y, 8F, 8F, 8F, 8F, width, height, 64F, 64F, entity);
        }
    }

    public static void sizeAnimation(double width, double height, double scale) {
        GlStateManager.translated(width, height, 0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-width, -height, 0);
    }

    public void drawFace(ResourceLocation res, float d,
                         float y,
                         float u,
                         float v,
                         float uWidth,
                         float vHeight,
                         float width,
                         float height,
                         float tileWidth,
                         float tileHeight,
                         LivingEntity target) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        IMinecraft.mc.getTextureManager().bindTexture(res);
        float hurtPercent = (target.hurtTime - (target.hurtTime != 0 ? IMinecraft.mc.timer.renderPartialTicks : 0.0f)) / 10.0f;
        GL11.glColor4f(1, 1 - hurtPercent, 1 - hurtPercent, 1);
        AbstractGui.drawScaledCustomSizeModalRect(d, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private void renderTextThatDisplaysTargetInfo(MatrixStack matrixStack) {
        float animationValue = (float) animation.getOutput();
        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float testX = posX + (width * halfAnimationValueRest);
        float testY = posY + (height * halfAnimationValueRest);
        float testW = width * animationValue;
        float testH = height * animationValue;
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW - 6, testH);
        Score score = IMinecraft.mc.world.getScoreboard().getOrCreateScore(entity.getScoreboardName(), IMinecraft.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
        float hp = entity.getHealth();
        float maxHp = entity.getMaxHealth();
        String header = IMinecraft.mc.ingameGUI.getTabList().header == null ? " " : IMinecraft.mc.ingameGUI.getTabList().header.getString().toLowerCase();


        if (IMinecraft.mc.getCurrentServerData() != null && IMinecraft.mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
            hp = score.getScorePoints();
            maxHp = 20;
        }

        if (IMinecraft.mc.getCurrentServerData() != null && IMinecraft.mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
            hp = score.getScorePoints();
            maxHp = 20;
        }
        double scale = Math.pow(10, 1);
        double result = Math.ceil(hp * scale) / scale;
        String hpText = String.valueOf(result);
        float absorb = entity.getAbsorptionAmount();
        double absorbRes = Math.ceil(absorb * scale) / scale;
        if (hp > 200 && ClientUtil.isConnectedToServer("funtime")) {
            hpText = ("???");
        }
        float textWidth = Fonts.sfMedium.getWidth(hpText, 5);
        Fonts.sfbold.drawText(matrixStack, TextFormatting.getTextWithoutFormattingCodes(entity.getName().getString()),
                posX + headSize + spacing + spacing - 5, posY + spacing + 1 - 2, -1, 8, 0.05f);
        if (hp > maxHp - 2.5) {
            Fonts.sfMedium.drawText(matrixStack, hpText, posX + (width - 42) + 23.5f,
                    posY + spacing + 1 + spacing + spacing + 15 - 3, -1, 5, 0.05f);
        } else if (hp < 2) {
            Fonts.sfMedium.drawText(matrixStack, hpText, posX + (width - 63),
                    posY + spacing + 1 + spacing + spacing + 15 - 3, -1, 5, 0.05f);
        } else if (hp < maxHp - 2 || hp > 1) {
            Fonts.sfMedium.drawText(matrixStack, hpText, posX + (width - 42) * healthAnimation + 30,
                    posY + spacing + 1 + spacing + spacing + 15 - 3, -1, 5, 0.05f);
        }

        Scissor.unset();
        Scissor.pop();
    }

    private void renderHealthBar() {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4ii = new Vector4i(new Color(255, 215, 0).getRGB(), new Color(255, 215, 0).getRGB(), new Color(255, 219, 88).getRGB(), new Color(255, 219, 88).getRGB());
        Vector4i vector4i = new Vector4i(style.getFirstColor().getRGB(), style.getFirstColor().getRGB(), style.getSecondColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37), 3, new Vector4f(2.5f,2.5f,2.5f,2.5f), ColorUtils.rgb(32, 32, 32));
        DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * healthAnimation, 3, new Vector4f(2.5f,2.5f,2.5f,2.5f), vector4i);
        if (!(entity.getAbsorptionAmount() > 100)) {
            DisplayUtils.drawShadow(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * absorptionAnimation, 3, 8, new Color(102, 255, 0).getRGB());
            DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * absorptionAnimation, 3, new Vector4f(2.5f,2.5f,2.5f,2.5f), vector4ii);
        }
    }

    public void setSizeAnimation(double scale) {
        GlStateManager.translated(posX + (width / 2), posY + (height / 2), 0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-(posX + (width / 2)), -(posY + (height / 2)), 0);
    }


    private boolean userConnectedToFunTimeAndEntityIsPlayer() {
        String header = IMinecraft.mc.ingameGUI.getTabList().header == null ? " " : IMinecraft.mc.ingameGUI.getTabList().header.getString().toLowerCase();
        return (IMinecraft.mc.getCurrentServerData() != null && IMinecraft.mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity);
    }

    private void drawBackGround(float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(posX, posY, width, height, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(posX + 0.5f, posY + 0.5f, width - 1f, height - 1, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(posX, posY, width, height, radius, ColorUtils.rgba(13, 13, 13, 230));
    }
}
