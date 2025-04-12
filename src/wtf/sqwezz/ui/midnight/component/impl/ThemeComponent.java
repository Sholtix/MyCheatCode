package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.Stencil;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

import static wtf.sqwezz.utils.client.IMinecraft.mc;

public class ThemeComponent extends Component {

    public Style style;
    public boolean opened;

    public ThemeComponent(Style style) {
        this.style = style;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        int color1 = style.getColor(0);
        int color2 = style.getColor(90);
        int color3 = style.getColor(180);
        int color4 = style.getColor(270);

        String input = style.getStyleName();
        String extracted = "";

        if (input.contains(" ") || input.contains("-")) {
            int spaceIndex = input.indexOf(" ");
            int dashIndex = input.indexOf("-");

            int splitIndex = (spaceIndex >= 0 && dashIndex >= 0)
                    ? Math.min(spaceIndex, dashIndex)
                    : (spaceIndex >= 0 ? spaceIndex : dashIndex);

            extracted = input.substring(splitIndex + 1).trim();
            input = input.substring(0, splitIndex).trim();
        }

        Vector4i vector4i = new Vector4i(color1, color2, color3, color4);

        int windowHeight = mc.getMainWindow().getScaledHeight();
        int maxY = (int) (windowHeight * 1.14);
        int minY = (int) (windowHeight * 0.36);

        Scissor.push();
        Scissor.setFromComponentCoordinates(x, minY, width, maxY - y - 6);

        DisplayUtils.drawRoundedRect(x + 2, y - 1, width - 5 + 2, height + 2, 6,
                Vredux.getInstance().getStyleManager().getCurrentStyle() == style
                        ? new Color(32, 36, 42).brighter().getRGB()
                        : new Color(80, 85, 95).getRGB());

        DisplayUtils.drawRoundedRect(x + 3, y, width - 5, height, 6, new Color(10, 10, 10).getRGB());

        Fonts.msMedium[14].drawString(matrixStack,
                Fonts.msMedium[14].getWidth(style.getStyleName()) > 48 ? input : style.getStyleName(),
                x + 6, y + 5f, -1);
        if (Fonts.msMedium[14].getWidth(style.getStyleName()) > 48) {
            Fonts.msMedium[14].drawString(matrixStack, extracted, x + 6, y + 12f, -1);
        }

        Stencil.initStencilToWrite();
        drawSmoothWave(x + 3, y, width - 5, height);
        Stencil.readStencilBuffer(1);

        DisplayUtils.drawRoundedRect(x + 3, y, width - 5, height, new Vector4f(7, 7, 7, 7), vector4i);

        Stencil.uninitStencilBuffer();
        Scissor.pop();
    }



    private void drawSmoothWave(float x, float y, float width, float height) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        float waveHeight = height * 0.1f;
        float frequency = 1.5f;

        for (int i = 0; i <= width; i++) {
            float progress = (float) i / width;
            float wave = (float) (Math.sin(progress * Math.PI * frequency) * waveHeight + height / 1.6f);

            GL11.glVertex2f(x + i, y + wave - 2);
            GL11.glVertex2f(x + i, y + height);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        float scale = 2f;
//        float width = 900 / scale;
//        float height = 650 / scale;
//        float leftPanel = 199 / scale;
//        float x = MathUtil.calculateXPosition(mc.getMainWindow().scaledWidth() / 2f, width);
//        float y = MathUtil.calculateXPosition(mc.getMainWindow().scaledHeight() / 2f, height);
//        if (mouseButton == 0) {
//            if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + leftPanel + 6, y + 64 / 2f + 50, width - leftPanel - 12, height - 64 / 2f - 70)) {
//                if (isHovered(mouseX, mouseY, 40)) {
//                    sqwezz.getInstance().getStyleManager().setCurrentStyle(style);
//                }
//            }
//        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }
}
