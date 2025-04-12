package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.functions.settings.impl.ColorSetting;
import wtf.sqwezz.ui.midnight.component.ColorWindow;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.DisplayUtils;

public class ColorComponent extends Component {

    public static ColorWindow opened;
    public ColorSetting option;
    public ColorWindow setted;

    public ColorComponent(ColorSetting option) {
        this.option = option;
        setted = new ColorWindow(this);
        this.setting = option;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        Fonts.gilroy[14].drawString(matrixStack, option.getName(), x + 5, y + height / 2f - 1, -1);
        float size = 8;
       DisplayUtils.drawRoundedRect(x + width - 10 - size /2f, y + height / 2f - size /2f, size,size, 3, option.get());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float size = 12;
        if (MathUtil.isInRegion(mouseX,mouseY, x + width - 10 - size /2f, y + height / 2f - size /2f, size,size)) {
            if (setted == opened) {
                opened = null;
                return;
            }
            opened = setted;
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

    @Override
    public void onConfigUpdate() {
        super.onConfigUpdate();
        setted.onConfigUpdate();
    }
}
