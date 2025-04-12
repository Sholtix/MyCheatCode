package wtf.sqwezz.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.*;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Getter
public class ThemePanelStyle extends ThemePanel {

    public ThemePanelStyle(Category category) {
        super(category);
        // TODO Auto-generated constructor stub
    }

    float max = 0;

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        float header = 25;
        float headerFont = 8;
        setAnimatedScrool(MathUtil.fast(getAnimatedScrool(), getScroll(), 10));

        DisplayUtils.drawRoundedRect(x - 180, y + 15, width + 80, height - 15, 6,
                ColorUtils.rgba(12, 12, 24, 245));
        DisplayUtils.drawRoundedRect(x - 180, y - 12.5f, width + 80, 25, 6,
                ColorUtils.rgba(12, 12, 24, 255));

        //DisplayUtils.drawGradientRoundedRect(x, y, width, 4, new Vector4f(4, 0, 4, 0));

        //DisplayUtils.drawShadow(x,y,width, 4,10,ColorUtils.getColor(0));

        //DisplayUtils.drawRectHorizontalW(x+5,y+24,width-10,0.5,-1,-1);

        Fonts.montserrat.drawCenteredText(stack, "Theme", x + width / 2f - 140,
                y + header / 2f - Fonts.montserrat.getHeight(headerFont) / 2f - 1 + 5 - 16, ColorUtils.rgb(255, 255, 255),
                headerFont, 0.1f);

        //DisplayUtils.drawRoundedRect(x, y + height - header, width, header, new Vector4f(0, 7, 0, 7),
                //new Vector4i(Color.TRANSLUCENT, ColorUtils.rgba(23, 23, 23, (int) (255 * 0.5)), Color.TRANSLUCENT,
                        //ColorUtils.rgba(23, 23, 23, (int) (255 * 0.5))));

        if (max > height - 24*2) {
            setScroll(MathHelper.clamp(getScroll(), -max + height - header - 10, 0));
            setAnimatedScrool(MathHelper.clamp(getAnimatedScrool(), -max + height - header - 10, 0));
        } else {
            setScroll(0);
            setAnimatedScrool(0);
        }

        float animationValue = (float) Theme.getAnimation().getValue() * Theme.scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float height = getHeight();
        float testX = getX() + (getWidth() * halfAnimationValueRest);
        float testY = getY() + 25 + (height * halfAnimationValueRest);
        float testW = getWidth() * animationValue;
        float testH = height * animationValue;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * halfAnimationValueRest);
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX - 180, testY, testW + 80, testH);

        int offset = 1;

        boolean hovered = false;

        float x = this.x + 5;
        float y = this.y + header + 5 +offset + getAnimatedScrool();

        float H = 12;

        for (Style style : Vredux.getInstance().getStyleManager().getStyleList()) {

            if (MathUtil.isHovered((int) mouseX, (int) mouseY, x + 5, y, width - 10 - 10, H)) {
                hovered = true;
            }

            if (Vredux.getInstance().getStyleManager().getCurrentStyle() == style) {
                DisplayUtils.drawRoundedRect(x + 5 - 180, y-1, width-19 + 80, H+2, new Vector4f(5, 5, 5, 6), new Vector4i(style.getFirstColor().getRGB(), style.getFirstColor().getRGB(), style.getSecondColor().getRGB(), style.getSecondColor().getRGB()));
                DisplayUtils.drawShadow(x + 5 - 180, y-1, width-19 + 80, H+2, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
            }

            DisplayUtils.drawRoundedRect(x + 6f - 180, y, width-21f + 80, H, new Vector4f(5, 5, 5, 5), ColorUtils.rgba(25,25,25,255));

            DisplayUtils.drawRoundedRect(x + 5f - 180, y, width/2-30, H, new Vector4f(5, 5, 0, 0), new Vector4i(style.getFirstColor().getRGB(), style.getFirstColor().getRGB(), style.getSecondColor().getRGB(), style.getSecondColor().getRGB()));

            Fonts.montserrat.drawText(stack, style.getStyleName(), x + 5*1.5f + width/2-28 - 140, y + H/2 - Fonts.montserrat.getHeight(6)/2, -1, 6f, 0.05f);

            y += 5+H;
            offset++;
        }

        if (MathUtil.isHovered((int) mouseX, (int) mouseY, x, y, width, height)) {
            if (hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
            } else {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            }
        }
        Scissor.unset();
        Scissor.pop();
        max = offset * Vredux.getInstance().getStyleManager().getStyleList().size() * 1.21f;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        float header = 25;
        int offset = 0;
        float x = this.x + 5;
        float y = this.y + offset + header + 5 + getAnimatedScrool() ;
        for (Style style : Vredux.getInstance().getStyleManager().getStyleList()) {
            if (MathUtil.isHovered((int) mouseX, (int) mouseY, x + 5 - 180, y, width - 10 - 10 + 80, 12)) {
                Vredux.getInstance().getStyleManager().setCurrentStyle(style);
            }
            y += 5+12;
            offset++;
        }

    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

}