package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.font.Fonts;
import wtf.sqwezz.utils.text.GradientUtil;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.math.Vector4i;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.Comparator;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PotionNextgenRenderer implements ElementRenderer {


    final Dragging dragging;


    float width;
    float height;

    @Subscribe
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 6.0f;
        float padding = 3;

        ITextComponent name = GradientUtil.gradient("Potions");

        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();

        drawStyledRect(posX, posY, width, height, 4);
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        wtf.sqwezz.utils.font.Fonts.msSemiBold[16].drawCenteredString(ms, name, posX + width / 2, posY - 1f + padding + 4f, -1);

        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;

        posY += 5f;

        Collection<EffectInstance> list = IMinecraft.mc.player.getActivePotionEffects();

        if (list.size() > 1) {
            list = list.stream().sorted(Comparator.comparingInt(pot -> (int) -Fonts.sfMedium.getWidth(pot.getEffectName(), 6.5f))).toList();
        }

        for (EffectInstance ef : list) {
            int amp = ef.getAmplifier();

            String ampStr = "";

            if (amp >= 1 && amp <= 9) {
                ampStr = " " + (amp + 1);
            }

            String nameText = ef.getPotion().getDisplayName().getString();
            float nameWidth = Fonts.sfMedium.getWidth(nameText, fontSize);

            String bindText = EffectUtils.getPotionDurationString(ef, 1);
            float bindWidth = Fonts.sfMedium.getWidth(bindText, fontSize);

            float localWidth = nameWidth + Fonts.sfMedium.getWidth(ampStr, fontSize) + bindWidth + padding * 4;

            Fonts.sfMedium.drawText(ms, nameText, posX + padding, posY + 0.5f, ColorUtils.rgba(210, 210, 210, 255), fontSize, 0.05f);

            float ampStrX = posX + padding + nameWidth;
            Fonts.sfMedium.drawText(ms, ampStr, ampStrX, posY + 0.5f, ColorUtils.rgba(230, 100, 100, 255), fontSize, 0.05f);

            Fonts.sfMedium.drawText(ms, bindText, posX + width - padding - bindWidth, posY + 0.5f, ColorUtils.rgba(210, 210, 210, 255), fontSize, 0.05f);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (fontSize + padding);
            localHeight += (fontSize + padding);
        }
        Scissor.unset();
        Scissor.pop();
        width = Math.max(maxWidth, 80) - 3;
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {
        Style style =  Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(x, y, width, height + 5, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(x + 0.5f, y + 0.5f, width - 1f, height + 4, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(x, y , width, height + 5, radius, ColorUtils.rgba(13,13,13, 230));
    }
}
