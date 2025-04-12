package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.*;
import wtf.sqwezz.ui.midnight.ClickGui;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.client.KeyStorage;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import net.minecraft.util.math.vector.Vector4f;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends Component {

    public Function function;

    public List<Component> components = new ArrayList<>();

    public ModuleComponent(Function function) {
        this.function = function;
        for (Setting<?> setting : function.getSettings()) {
            if (setting instanceof BooleanSetting bool) {
                components.add(new BooleanComponent(bool));
            }
            if (setting instanceof SliderSetting slider) {
                components.add(new SliderComponent(slider));
            }
            if (setting instanceof BindSetting bind) {
                components.add(new BindComponent(bind));
            }
            if (setting instanceof ModeSetting mode) {
                components.add(new ModeComponent(mode));
            }
            if (setting instanceof ModeListSetting mode) {
                components.add(new ListComponent(mode));
            }
            if (setting instanceof StringSetting string) {
                components.add(new TextComponent(string));
            }
            if (setting instanceof ColorSetting colorSetting) {
                components.add(new ColorComponent(colorSetting));
            }
        }
    }

    public float animationToggle;
    public static ModuleComponent binding;

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
            float totalHeight = 2;
            for (Component component : components) {
                if (component.setting != null && component.setting.visible.get()) {
                    totalHeight += component.height;
                }
            }

            float off = 2f;

            components.forEach(c -> {
                c.function = function;
                c.parent = parent;
            });

            animationToggle = MathUtil.lerp(animationToggle, function.isState() ? 1 : 0, 10);
            //DisplayUtils.drawShadow(x, y, width, height + totalHeight, 10, new Color(17, 18, 21).getRGB());
            DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1, height + 1 + totalHeight, new Vector4f(6, 6, 6, 6), new Color(80, 85, 95).getRGB());
            DisplayUtils.drawRoundedRect(x, y, width, height + totalHeight, new Vector4f(6, 6, 6, 6), new Color(10, 10, 10).getRGB());

            Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
            Fonts.msBold[16].drawString(matrixStack, function.getName(), x + 7.5f, y + 9f, function.isState() ? style.getFirstColor().getRGB() : -1);

            String key = KeyStorage.getKey(function.getBind());
            String bind = key.isEmpty() ? "none" : key;
            String fullBind = "Бинд: " + bind;
            float bindWidth = Fonts.gilroy[14].getWidth("Бинд: " + bind);
        DisplayUtils.drawRoundedRect(x + 5 + 2.5f - 0.5f, y + 20 + off - 0.5f, 10 + bindWidth + 1, 13 + 1 + 1, 2, new Color(80, 85, 95).getRGB());
        DisplayUtils.drawRoundedRect(x + 5 + 2.5f, y + 20 + off, 10 + bindWidth, 13 + 1, 2, new Color(10, 10, 10).getRGB());
            Fonts.gilroy[14].drawCenteredString(matrixStack, fullBind, x + 5 + 2.5f + (10 + Fonts.gilroy[14].getWidth(fullBind)) / 2, y + 27 + off - 1, -1);
            int color = ColorUtils.interpolateColor(new Color(10, 10, 10).getRGB(), ColorUtils.IntColor.rgba(style.getFirstColor().getRed(), style.getFirstColor().getGreen(), style.getFirstColor().getBlue(), 255), animationToggle);
            DisplayUtils.drawShadow(x + 20 + bindWidth, y + 20 + off, Fonts.gilroy[14].getWidth("Активно") + 10, 13 + 1, 8, ColorUtils.reAlphaInt(color, 50));
        DisplayUtils.drawRoundedRect(x + 20 + bindWidth - 0.5f, y + 20 + off - 0.5f, Fonts.gilroy[14].getWidth("Активно") + 10 + 1, 13 + 1 + 1, 2, new Color(80, 85, 95).getRGB());
        DisplayUtils.drawRoundedRect(x + 20 + bindWidth, y + 20 + off, Fonts.gilroy[14].getWidth("Активно") + 10, 13 + 1, 2f, color);
            Scissor.push();

            Scissor.setFromComponentCoordinates(x + 5, y + 23 + off, 10 * animationToggle, 10);
            Scissor.unset();
            Scissor.pop();
            Fonts.gilroy[14].drawString(matrixStack, "Активно", x + 25f + bindWidth, y + 27f - 1 + off, function.isState() ? new Color(0, 0, 0).getRGB() : -1);

            float offsetY = 0;
            for (Component component : components) {
                if (component.setting != null && component.setting.visible.get()) {
                    component.setPosition(x, y + height + offsetY, width, 20);
                    component.drawComponent(matrixStack, mouseX, mouseY);
                    offsetY += component.height;
                }
            }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        String key = KeyStorage.getKey(function.getBind());
        String bind = key.isEmpty() ? "none" : key;
        float bindWidth = Fonts.gilroy  [14].getWidth("Бинд: " + bind);
        if (MathUtil.isInRegion(mouseX, mouseY, x + 20 + bindWidth, y + 22, Fonts.gilroy[14].getWidth("Активно") + 10, 16) && mouseButton <= 1) {
            function.toggle();
        }

        if (binding == this && mouseButton > 2) {
            function.setBind(-100 + mouseButton);
            binding = null;
        }

        if (MathUtil.isInRegion(mouseX, mouseY, x + 5 + 2.5f, y + 22, 10 + bindWidth, 13 + 3)) {
            if (mouseButton == 0) {
                ClickGui.typing = false;
                binding = this;
            }
        }
        components.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        components.forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        components.forEach(component -> component.keyTyped(keyCode, scanCode, modifiers));
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        components.forEach(component -> component.charTyped(codePoint, modifiers));
    }
}
