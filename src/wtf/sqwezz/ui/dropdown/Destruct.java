package wtf.sqwezz.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.utils.math.MathUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Destruct {
    private int x, y, width, height;
    private String text;
    private final String placeholder;

    public Destruct(int x, int y, int width, int height, String placeholder) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.placeholder = placeholder;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        String textToDraw = placeholder;
        //DisplayUtils.drawRoundedRect(x - 7, y - 60 - 280, 105, height + 5, 4, ColorUtils.rgba(242, 242, 242, 195));
        //FontManager.gilroy[14].drawString(matrixStack, textToDraw, x + 5 - 7, y + (height - 8) / 2 + 1 - 60 + 4.5f -280, ColorUtils.rgb(2, 2, 2));
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered((int) mouseX, (int) mouseY, x - 7, y - 60 - 280, 105, height + 5)) {
            //SelfDestruct selfDestruct = Vredux.getInstance().getFunctionRegistry().getSelfDestruct();
            //selfDestruct.toggle();
        }
        return true;
    }
}
