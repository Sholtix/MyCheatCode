package wtf.sqwezz.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TimerNextgenRenderer implements ElementRenderer {

    public final Dragging dragging;

    private float animWidth;

    public void render(EventDisplay display) {
        MatrixStack ms = display.getMatrixStack();
        float quotient = Vredux.getInstance().getFunctionRegistry().getTimer().maxViolation / (Vredux.getInstance().getFunctionRegistry().getTimer().timerAmount);
        float minimumValue = Math.min(Vredux.getInstance().getFunctionRegistry().getTimer().violation, quotient);
        float width = 60.0f;
        float height = 20f;
        float x = dragging.getX();
        float y = dragging.getY();
        float timerWidth = width;
        float targetWidth = (quotient - minimumValue) / quotient * timerWidth;
        int value = 100;
        animWidth = (value == 100) ? timerWidth : MathUtil.fast(animWidth, targetWidth, 10.0f);
        drawStyledRect(x - 0.5f, y - 0.5f, width + 1.0f, height, 5);
        drawStyledRect(x - 0.5f, y - 0.5f, width + 1.0f, height, 5);
        drawStyledRectR(x + 3, y + 13, animWidth - 6, 3f, 5);
        Fonts.msSemiBold[16].drawCenteredString(ms, "Timer", (double) (x + width / 2.0f), (double) y + 5.0, -1);
        Fonts.msSemiBold[14].drawCenteredString(ms, value + "%", (double) (x + width / 2.0f), (double) y + 3.0 - 1.0 + 13, -1);
        dragging.setWidth(width);
        dragging.setHeight(height);
    }
    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(x, y, width, height + 5, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(x + 0.5f, y + 0.5f, width - 1f, height + 4, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height + 5, radius, ColorUtils.rgba(13, 13, 13, 230));
    }
    private void drawStyledRectR(float x,
                                 float y,
                                 float width,
                                 float height,
                                 float radius) {
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawRoundedRect(x, y, width, height + 5, new Vector4f(4,4,4,4), new Vector4i(vector4i.x, vector4i.y, vector4i.z, vector4i.w));
    }
}
