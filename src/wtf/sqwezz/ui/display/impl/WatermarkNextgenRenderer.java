package wtf.sqwezz.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.client.PingUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import wtf.sqwezz.utils.text.GradientUtil;
import wtf.sqwezz.functions.impl.render.HUD;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WatermarkNextgenRenderer implements ElementRenderer {


    @Override
    public void render(EventDisplay eventDisplay) {
        HUD hud = Vredux.getInstance().getFunctionRegistry().getHud();
        MatrixStack ms = eventDisplay.getMatrixStack();
        float posX = -2;
        float posY = 1;
        float padding = 5;
        float fontSize = 6.5f;
        float iconSize = 10;
        int fepe = IMinecraft.mc.getDebugFPS();
        float textW = Fonts.sfui.getWidth("crysense", fontSize);
        float nameW =  hud.nextgenWT.get(0).get() ? Fonts.sfui.getWidth(IMinecraft.mc.player.getName().getString(), fontSize) : -6;
        int pingp = PingUtil.calculatePing();

        float fepese = hud.nextgenWT.get(1).get() ? Fonts.sfui.getWidth(fepe + " fps", fontSize) : -8;
        float pinguu = hud.nextgenWT.get(2).get() ? Fonts.sfui.getWidth(pingp + " ping", fontSize) : -10;
        float allWidth = iconSize + padding * 2 + textW + nameW - 8 + fepese + pinguu + 18;


        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();

        drawStyledRect(posX + 4, posY + 2, allWidth, iconSize + padding * 2 - 7.5f, 4);

        ITextComponent text = GradientUtil.gradient("crysense");

        float textWidth = Fonts.sfui.getWidth(text, fontSize);

        float localPosX = posX + iconSize + padding * 3;
        int fps = IMinecraft.mc.getDebugFPS();
        int ping = PingUtil.calculatePing();
        float tochkWidth = Fonts.sfui.getWidth(".", fontSize + 4);
        float spacing = 1.0f;
        float textInameW = textW + nameW + tochkWidth + (spacing * 2);
        float textNameIFpsW = textW + nameW + fepese + tochkWidth + (spacing * 2) + 6f;

        Fonts.sfui.drawText(ms, text, posX + 7, posY + iconSize / 2 + 1.5f - 1.5f, fontSize, 255);
        if (hud.nextgenWT.get(0).get() || hud.nextgenWT.get(1).get() || hud.nextgenWT.get(2).get()) {
            Fonts.sfui.drawText(ms, ".", posX + textWidth + 9 + spacing, posY + iconSize / 2 + 1.5f - 6.5f, new Color(128, 128, 128).getRGB(), fontSize + 4);
        }
        if (hud.nextgenWT.get(0).get()) {
            Fonts.sfui.drawText(ms, IMinecraft.mc.player.getName().getString(), posX + 10 + textWidth + tochkWidth + spacing * 2, posY + iconSize / 2 + 1.5f - 1.5f, -1, fontSize);
        }
        if (hud.nextgenWT.get(0).get() && (hud.nextgenWT.get(1).get() | hud.nextgenWT.get(2).get())) {
            Fonts.sfui.drawText(ms, ".", posX + 10f + textInameW + spacing, posY + iconSize / 2 + 1.5f - 6.5f, new Color(128, 128, 128).getRGB(), fontSize + 4);
        }
        if (hud.nextgenWT.get(1).get()) {
            Fonts.sfui.drawText(ms, fps + " fps", posX + textInameW + 10 + tochkWidth + (spacing * 2), posY + iconSize / 2 + 1.5f - 1.5f, -1, fontSize);
        }
        if (hud.nextgenWT.get(1).get() && hud.nextgenWT.get(2).get()) {
            Fonts.sfui.drawText(ms, ".", posX + textNameIFpsW + 11f + spacing, posY + iconSize / 2 + 1.5f - 6.5f, new Color(128, 128, 128).getRGB(), fontSize + 4);
        }
        if (hud.nextgenWT.get(2).get()) {
            Fonts.sfui.drawText(ms, ping + " ping", posX + textNameIFpsW + 16f + spacing * 2, posY + iconSize / 2 + 1.5f - 1.5f, -1, fontSize);
        }
    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(x, y, width, height, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(x + 0.5f, y + 0.5f, width - 1f, height - 1, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height, radius, ColorUtils.rgba(13,13,13, 230));
    }
}
