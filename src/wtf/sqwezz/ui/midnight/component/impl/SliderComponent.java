package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

import java.awt.*;

public class SliderComponent extends Component {

    public SliderSetting option;

    public SliderComponent(SliderSetting option) {
        this.option = option;
        this.setting = option;

    }

    boolean drag;

    float anim;

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        height += 2;
        float sliderWidth = ((option.get() - option.min) / (option.max - option.min)) * (width - 12);
        anim = MathUtil.lerp(anim, sliderWidth, 10);
        Fonts.gilroy[13].drawString(matrixStack, option.getName(), x + 6, y + 4, -1);
        Fonts.gilroy[14].drawString(matrixStack, String.valueOf(option.get()), x + width - Fonts.gilroy[14].getWidth(String.valueOf(option.get())) - 6, y + 4, -1);
        DisplayUtils.drawRoundedRect(x + 6 + 1, y + 13, width - 12, 4, new Vector4f(2, 2, 2, 2), new Color(10,10,10).getRGB());
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        DisplayUtils.drawShadow(x + 6 + 1, y + 15, anim, 1, 8, /*new Color(74, 166, 218, 50).getRGB()*/ style.getFirstColor().getRGB());

        DisplayUtils.drawRoundedRect(x + 6, y + 14, anim, 1, new Vector4f(2, 2,
                option.max == option.get() ? 2 : 0, option.max == option.get() ? 2 : 0), /*new Color(74, 166, 218).getRGB()*/ style.getFirstColor().getRGB());

        //DisplayUtils.drawCircle(x + 5 + anim, y + 15.5f, 8, new Color(17, 18, 21).getRGB());
        //DisplayUtils.drawCircle(x + 5 + anim, y + 15.5f, 6, new Color(74, 166, 218).getRGB());
        DisplayUtils.drawCircle(x + 5 + 1 + anim, y + 14.75f, 3.5f, /*new Color(17, 18, 21).getRGB()*/ style.getFirstColor().getRGB());
        if (drag) {
            float draggingValue = (float) MathHelper.clamp(MathUtil.round((mouseX - x - 4) / (width - 12)
                    * (option.max - option.min) + option.min, option.increment), option.min, option.max);
            option.set(draggingValue);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            drag = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        drag = false;
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {

    }
}
