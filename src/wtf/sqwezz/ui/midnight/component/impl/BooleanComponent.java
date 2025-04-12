package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;

public class BooleanComponent extends Component {

    public BooleanSetting option;

    public BooleanComponent(BooleanSetting option) {
        this.option = option;
        this.setting = option;
    }

    public float animationToggle;

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        height = 15;
        float off = 0.5f;
        //animationToggle = MathUtil.lerp(animationToggle, option.get() ? 1 : 0, 10);

        /*int color = ColorUtils.interpolateColor(ColorUtils.IntColor.rgba(26, 29, 33, 255),
                ColorUtils.IntColor.rgba(74, 166, 218, 255), animationToggle);*/
        int color = ColorUtils.IntColor.rgba(26, 29, 33, 255);

        DisplayUtils.drawShadow(x + 5 + 120, y + 1 + off, 10, 10, 8, ColorUtils.reAlphaInt(color, 50));
        DisplayUtils.drawRoundedRect(x + 5 + 120, y + 1 + off, 10, 10, 2f, color);
        Scissor.push();

        float optionWidth = Fonts.gilroy[14].getWidth(option.getName());
        Scissor.setFromComponentCoordinates(x + 5 + 120, y + 1 + off, 10 * 1, 10);
        if (option.get()) {
            Fonts.icons1[12].drawString(matrixStack, "T", x + 7 + 120, y + 6 + off, -1);
        } else {
            Fonts.icons1[12].drawString(matrixStack, "U", x + 7 + 120, y + 6 + off, -1);
        }
        Scissor.unset();
        Scissor.pop();

        Fonts.gilroy[13].drawString(matrixStack, option.getName(), x + 5 + 2.5f/* + 110 - optionWidth*/, y + 4.5f + off, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MathUtil.isInRegion(mouseX, mouseY, x, y, width, 15)) {

            option.set(!option.get());
        }
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
