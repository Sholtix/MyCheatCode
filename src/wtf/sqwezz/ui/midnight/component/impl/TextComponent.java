package wtf.sqwezz.ui.midnight.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.functions.settings.impl.StringSetting;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class TextComponent extends Component {

    public StringSetting option;

    public boolean isTyping;
    public boolean opened;

    //public static boolean typing;

    public TextComponent(StringSetting option) {
        this.option = option;
        this.setting = option;
    }
    String text = "";

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        height += 3;
        text = option.get();

        if (!isTyping && option.get().isEmpty()) {
            text = option.getDescription();
        }

        float widthT = Fonts.gilroy[14].getWidth(text) + 4;
        Scissor.push();
        Scissor.setFromComponentCoordinates(x,y,width,height);

        DisplayUtils.drawRoundedRect(x + 5 + 2.5f - 0.5f, y + 2 + 10 - 0.5f, widthT + 1, 10 + 1, 2, new Color(53, 55, 60).getRGB());
        DisplayUtils.drawRoundedRect(x + 5 + 2.5f, y + 2 + 10, widthT, 10, 2, isTyping ? new Color(17, 18, 21).brighter().brighter().getRGB() : new Color(17, 18, 21).brighter().getRGB());

        Fonts.gilroy[14].drawCenteredString(matrixStack, text, x + 5 + (widthT / 2) + 2.5f, y + 6 + 10, -1);
        Fonts.gilroy[14].drawString(matrixStack, option.getName(), x + 5 + 2.5f, y + 6, -1);

        Scissor.pop();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            isTyping = !isTyping;
        } else {
            isTyping = false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (isTyping) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!option.get().isEmpty())
                option.set(option.get().substring(0, (int) (option.get().length() - 1f)));
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            isTyping = false;
        }
            if (Screen.isCopy(keyCode)) {
                Minecraft.getInstance().keyboardListener.setClipboardString(option.get());
            } else if (Screen.isPaste(keyCode)) {
                option.set(Minecraft.getInstance().keyboardListener.getClipboardString());
            }
        }

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (option.isOnlyNumber() && !NumberUtils.isNumber(String.valueOf(codePoint))) {
            return;
        }
        if (isTyping && text.length() < 60) {
            text += codePoint;
            option.set(text);
        }
    }
}
