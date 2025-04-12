package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.animations.Animation;
import wtf.sqwezz.utils.animations.Direction;
import wtf.sqwezz.utils.animations.impl.EaseBackIn;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.Stencil;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetHUDNextgenRenderer2 implements ElementRenderer {
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

    public TargetHUDNextgenRenderer2(Dragging drag) {
        this.drag = drag;
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

            float health = fix1000Health(entity.getHealth());
            float maxHealth = entity.getMaxHealth();

            healthAnimation = MathUtil.fast(healthAnimation,
                    MathHelper.clamp(health / maxHealth, 0, 1), 10);
            absorptionAnimation = MathUtil.fast(absorptionAnimation,
                    MathHelper.clamp(entity.getAbsorptionAmount() / maxHealth, 0, 1), 10);

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
            GlStateManager.popMatrix();
        }
    }

    private LivingEntity getTarget(LivingEntity nullTarget) {
        LivingEntity auraTarget = Vredux.getInstance().getFunctionRegistry().getKillAura().getTarget();
        LivingEntity target = nullTarget;
        if (auraTarget != null) {
            stopWatch.reset();
            shouldToBack = true;
            target = auraTarget;
        } else if (mc.currentScreen instanceof ChatScreen) {
            stopWatch.reset();
            shouldToBack = true;
            target = mc.player;
        } else {
            shouldToBack = false;
        }
        return target;
    }

    public void drawTargetHead(LivingEntity entity, float x, float y, float width, float height) {
        if (entity != null) {
            EntityRenderer<? super LivingEntity> rendererManager = mc.getRenderManager().getRenderer(entity);
            drawFace(rendererManager.getEntityTexture(entity), x, y, 8F, 8F, 8F, 8F, width, height, 64F, 64F, entity);
        }
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
        Score score = mc.world.getScoreboard().getOrCreateScore(entity.getScoreboardName(), mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
        float hp = entity.getHealth();
        float maxHp = entity.getMaxHealth();
        String header = mc.ingameGUI.getTabList().header == null ? " " : mc.ingameGUI.getTabList().header.getString().toLowerCase();


        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
            hp = score.getScorePoints();
            maxHp = 20;
        }

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity) {
            hp = score.getScorePoints();
            maxHp = 20;
        }
        double scale = Math.pow(10, 1);
        double result = Math.ceil(hp * scale) / scale;
        String hpText = "HP: " + result;
        float absorb = entity.getAbsorptionAmount();
        double absorbRes = Math.ceil(absorb * scale) / scale;
        String absorbb = String.valueOf((int) absorbRes);
        if (hp > 200) {
            hpText = ("???");
        }
        if (absorbRes > 200) {
            absorbb = "???";
        }
        float hpWidth = Fonts.sfMedium.getWidth(hpText,6);
        Fonts.sfbold.drawText(matrixStack, TextFormatting.getTextWithoutFormattingCodes(entity.getName().getString()), posX + headSize + spacing + spacing - 5,  posY + spacing + 1 - 2, -1, 8);
        Fonts.sfMedium.drawText(matrixStack, hpText, posX + headSize + spacing + spacing - 5,
                posY + spacing + 1 + spacing + spacing - 2, -1, 6.0f,0.05f);
        Scissor.unset();
        Scissor.pop();
    }

    private void renderHealthBar() {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(style.getFirstColor().getRGB(), style.getFirstColor().getRGB(), style.getSecondColor().getRGB(), style.getSecondColor().getRGB());
        Vector4i vector4ii = new Vector4i(new Color(255, 215, 0).getRGB(), new Color(255, 215, 0).getRGB(), new Color(255, 219, 88).getRGB(), new Color(255, 219, 88).getRGB());
        DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37), 7, new Vector4f(4, 4, 4, 4), ColorUtils.rgb(32, 32, 32));
        DisplayUtils.drawShadow(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * healthAnimation, 7, 8, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * healthAnimation, 7, new Vector4f(4, 4, 4, 4), vector4i);
        if (!(ClientUtil.isConnectedToServer("funtime"))) {
            DisplayUtils.drawShadow(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * absorptionAnimation, 7, 8, new Color(102, 255, 0).getRGB());
            DisplayUtils.drawRoundedRect(posX + headSize + spacing + spacing - 5, posY + height - spacing * 2 - 3 + 2, (width - 37) * absorptionAnimation, 7, new Vector4f(4, 4, 4, 4), vector4ii);
        }
    }

    public void setSizeAnimation(double scale) {
        GlStateManager.translated(posX + (width / 2), posY + (height / 2), 0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-(posX + (width / 2)), -(posY + (height / 2)), 0);
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
        mc.getTextureManager().bindTexture(res);
        float hurtPercent = (target.hurtTime - (target.hurtTime != 0 ? mc.timer.renderPartialTicks : 0.0f)) / 10.0f;
        GL11.glColor4f(1, 1 - hurtPercent, 1 - hurtPercent, 1);
        AbstractGui.drawScaledCustomSizeModalRect(d, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private float fix1000Health(float original) {
        Score score = mc.world.getScoreboard().getOrCreateScore(entity.getScoreboardName(),
                mc.world.getScoreboard().getObjectiveInDisplaySlot(2));

        return userConnectedToFunTimeAndEntityIsPlayer() ? score.getScorePoints() : original;
    }


    private boolean userConnectedToFunTimeAndEntityIsPlayer() {
        String header = mc.ingameGUI.getTabList().header == null ? " " : mc.ingameGUI.getTabList().header.getString().toLowerCase();
        return (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("funtime")
                && (header.contains("анархия") || header.contains("гриферский")) && entity instanceof PlayerEntity);
    }

    private void drawBackGround(float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawGradientRound(posX + 0.5f, posY + 0.5f, width - 1f, height - 1, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawShadow(posX, posY, width, height, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawRoundedRect(posX, posY, width, height, radius, ColorUtils.rgba(13,13,13, 230));
    }
}
