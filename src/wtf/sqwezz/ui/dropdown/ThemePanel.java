package wtf.sqwezz.ui.dropdown;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.ui.dropdown.components.ModuleComponent;
import wtf.sqwezz.ui.dropdown.impl.Component;
import wtf.sqwezz.ui.dropdown.impl.IBuilder;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.Stencil;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

@Getter
@Setter
public class ThemePanel implements IBuilder {

    private final Category category;
    protected float x;
    protected float y;
    protected final float width = 105f;
    protected final float height = 220f;


    private List<ModuleComponent> modules = new ArrayList<>();
    private float scroll, animatedScrool;


    public ThemePanel(Category category) {
        this.category = category;

        for (Function function : Vredux.getInstance().getFunctionRegistry().getFunctions()) {
            if (function.getCategory() == category) {
                ModuleComponent component = new ModuleComponent(function);
                modules.add(component);
            }
        }

    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {

        animatedScrool = MathUtil.fast(animatedScrool, scroll, 10);
        float header = 20;
        float headerFont = 8;


        //DisplayUtils.drawRoundedRect(x, y + 15, width, height - 15, 6,
                //ColorUtils.rgba(255, 255, 255, 245));
        //DisplayUtils.drawRoundedRect(x, y - 12.5f, width, 25, 6,
                //ColorUtils.rgba(245, 245, 245, 255));
        DisplayUtils.drawRoundedRect(x + 5 - 210, y + 15, width + 160, height - 15, 6,
                ColorUtils.rgba(12, 12, 24, 245));

        //DisplayUtils.drawGradientRoundedRect(x, y, width, 4, new Vector4f(4, 0, 4, 0));

        //DisplayUtils.drawShadow(x,y,width, 4,10,ColorUtils.getColor(0));

        //DisplayUtils.drawRectHorizontalW(x+5,y+24,width-10,0.5,-1,-1);

        //Fonts.montserrat.drawCenteredText(stack, category.name(), x + width / 2f,
                //y + header / 2f - Fonts.montserrat.getHeight(headerFont) / 2f - 1 + 5 - 15, ColorUtils.rgb(5, 5, 5),
                //headerFont, 0.1f);

        drawComponents(stack, mouseX, mouseY);


        //DisplayUtils.drawRoundedRect(x, y + height - header, width, header, new Vector4f(0, 7, 0, 7),
                //new Vector4i(Color.TRANSLUCENT, ColorUtils.rgba(23, 23, 23, (int) (255 * 0.5)), Color.TRANSLUCENT,
                        //ColorUtils.rgba(23, 23, 23, (int) (255 * 0.5))));


    }


    float max = 0;

    private void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        float animationValue = (float) Theme.getAnimation().getValue() * Theme.scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float height = getHeight();
        float testX = getX() + (getWidth() * halfAnimationValueRest);
        float testY = getY() + 25 + (height * halfAnimationValueRest);
        float testW = getWidth() * animationValue;
        float testH = height * animationValue;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) *
                halfAnimationValueRest);

        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        float offset = -1;
        float header = 25;

        if (max > height - header - 10) {
            scroll = MathHelper.clamp(scroll, -max + height - header - 10, 0);
            animatedScrool = MathHelper.clamp(animatedScrool, -max + height - header - 10, 0);
        }
        else {
            scroll = 0;
            animatedScrool = 0;
        }

        for (ModuleComponent component : modules) {
            component.setX(getX() + 5);
            component.setY(getY() + header + offset + 6 + animatedScrool);
            component.setWidth(getWidth() - 10);
            component.setHeight(20);
            component.animation.update();
            if (component.animation.getValue() > 0) {
                float componentOffset = 0;
                for (Component component2 : component.getComponents()) {
                    if (component2.isVisible())
                        componentOffset += component2.getHeight();
                }
                componentOffset *= component.animation.getValue();
                component.setHeight(component.getHeight() + componentOffset);
            }
            component.render(stack, mouseX, mouseY);
            offset += component.getHeight() + 3.5f;
        }
        max = offset;

        Scissor.unset();
        Scissor.pop();

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : modules) {
            component.mouseClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (ModuleComponent component : modules) {
            component.keyPressed(key, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : modules) {
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : modules) {
            component.mouseRelease(mouseX, mouseY, button);
        }
    }

}
