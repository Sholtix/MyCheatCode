package wtf.sqwezz.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Font;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.glfw.GLFW;

@Setter
@Getter
public class SearchField {
    private int x, y, width, height;
    private String text;
    private boolean isFocused;
    private boolean typing;
    private final String placeholder;

    public SearchField(int x, int y, int width, int height, String placeholder) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.placeholder = placeholder;
        this.text = "";
        this.isFocused = false;
        this.typing = false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        String textToDraw = text.isEmpty() && !typing ? placeholder : text;
        String cursor = typing && System.currentTimeMillis() % 1000 > 500 ? "_" : "";
        DisplayUtils.drawRoundedRect(x - 7, y - 60, 105, height + 5, 4, ColorUtils.rgba(16, 16, 26,255));
        Fonts.gilroy[14].drawString(matrixStack, textToDraw + cursor, x + 5 - 7, y + (height - 8) / 2 + 1 - 60 + 4.5f, ColorUtils.rgb(255, 255, 255));
        //DisplayUtils.drawRoundedRect(x - 7, y - 60, 105, height + 5, 4, ColorUtils.rgb(16, 16, 26));
        //Fonts.sfMedium.drawText(matrixStack, textToDraw + cursor, x + 5 - 7, y + (height - 8) / 2 + 1 - 60 + 2.5f, ColorUtils.rgb(255, 255, 255), 7);
    }
    public void renderWhite(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        String textToDraw = placeholder;
        DisplayUtils.drawRoundedRect(x - 7, y - 60, 105, height + 5, 4, ColorUtils.rgba(242, 242, 242, 195));
        Fonts.gilroy[14].drawString(matrixStack, textToDraw, x + 5 - 7, y + (height - 8) / 2 + 1 - 60 + 4.5f, ColorUtils.rgb(2, 2, 2));
        //DisplayUtils.drawRoundedRect(x - 7, y - 60, 105, height + 5, 4, ColorUtils.rgb(16, 16, 26));
        //Fonts.sfMedium.drawText(matrixStack, textToDraw + cursor, x + 5 - 7, y + (height - 8) / 2 + 1 - 60 + 2.5f, ColorUtils.rgb(255, 255, 255), 7);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (isFocused) {
            text += codePoint;
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused && keyCode == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE){
            typing = false;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        //if(MathUtil.isHovered((float) mouseX, (float) mouseY, 5, 5, 20, 20)){
            //panel.colorclienttt = true;
        //}
        //if (MathUtil.isHovered((float) mouseX, (float) mouseY, 30, 5, 20, 20)) {
        //    panel.colorclienttt = false;
        //}


        if(!MathUtil.isHovered((int) mouseX, (int) mouseY, x - 7, y - 60, 105, height + 5)){
            isFocused = false;
        }
        isFocused = MathUtil.isHovered((int) mouseX, (int) mouseY, x - 7, y - 60, 105, height + 5);
        typing = isFocused;
        return isFocused;
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }
    public void setFocused(boolean focused) { isFocused = focused; }
}