package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeListSetting;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;

import java.awt.*;

public class ListComponent extends Component {

    public ModeListSetting option;

    public boolean opened;

    public ListComponent(ModeListSetting option) {
        this.option = option;
        this.setting = option;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        float off = 4;
        float offset = 17 - 8;
        for (BooleanSetting s : option.get()) {
            offset += 9;
        }
        if (!opened) offset = 0;
        Fonts.gilroy[14].drawString(matrixStack, option.getName(), x + 5, y + 3, -1);
        off += Fonts.gilroy[14].getFontHeight() / 2f + 2;
        height += offset + 7;
        DisplayUtils.drawShadow(x + 5, y + off, width - 10, 20 - 6, 10, new Color(26, 29, 33, 50).getRGB());
        DisplayUtils.drawRoundedRect(x + 5 - 0.5f, y + off - 0.5f, width - 10 + 1, 20 - 6 + 1, 4, new Color(53, 55, 60).getRGB());
        DisplayUtils.drawRoundedRect(x + 5, y + off, width - 10, 20 - 6, 4, new Color(26, 29, 33).getRGB());
        DisplayUtils.drawShadow(x + 5, y + off + 17, width - 10, offset, 12, new Color(0, 0, 0, 100).getRGB());
        DisplayUtils.drawRoundedRect(x + 5 - 0.5f, y + off + 17 - 0.5f, width - 10 + 1, offset + 1, 4, new Color(53, 55, 60).getRGB());
        DisplayUtils.drawRoundedRect(x + 5, y + off + 17, width - 10, offset, 4, new Color(17, 18, 21).getRGB());
        Scissor.push();
        Scissor.setFromComponentCoordinates(x + 5, y + off, width - 10, 20 - 6 - 4);
        Fonts.gilroy[14].drawString(matrixStack, option.getNames(), x + 10, y + 20 - 4, -1);
        Scissor.unset();
        Scissor.pop();
        if (opened) {
            int i = 1;
            for (BooleanSetting s : option.get()) {
                boolean hovered = MathUtil.isInRegion(mouseX, mouseY, x, y + off + 20 + i, width, 8);
                s.anim = MathUtil.lerp(s.anim, (hovered ? 2 : 0), 10);
                Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
                Fonts.gilroy[14].drawString(matrixStack, s.getName(), x + 9 + s.anim, y + off + 23.5F + i, option.getValueByName(s.getName()).get() ? style.getFirstColor().getRGB() : new Color(163, 176, 188).getRGB());
                i += 9;
            }
            height += 3;
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float off = 4 + Fonts.gilroy[14].getFontHeight() / 2f + 2;

        if (MathUtil.isInRegion(mouseX, mouseY, x + 5, y + off, width - 10, 20 - 5)) {
            opened = !opened;
        }


        if (!opened) return;
        int i = 1;
        for (BooleanSetting s : option.get()) {
            if (MathUtil.isInRegion(mouseX, mouseY, x, y + off + 20 + i, width, 8)) {
                s.set(!s.get());
            }
            i += 9;
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
