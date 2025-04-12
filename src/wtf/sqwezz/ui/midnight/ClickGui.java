package wtf.sqwezz.ui.midnight;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.ui.midnight.component.impl.ColorComponent;
import wtf.sqwezz.ui.midnight.component.impl.Component;
import wtf.sqwezz.ui.midnight.component.impl.ModuleComponent;
import wtf.sqwezz.ui.midnight.component.impl.ThemeComponent;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.client.Vec2i;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.Stencil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static wtf.sqwezz.utils.client.IMinecraft.mc;

public class ClickGui extends Screen {
    public ClickGui() {
        super(new StringTextComponent("GUI"));
        for (Function function : Vredux.getInstance().getFunctionRegistry().getFunctions()) {
            objects.add(new ModuleComponent(function));
        }

    }

    double xPanel, yPanel;
    Category current = Category.Combat;

    float animation;

    public ArrayList<ModuleComponent> objects = new ArrayList<>();

    public float scroll = 0;
    public float scrollT = 0;
    public float animateScroll = 0;
    public float animateScrollT = 0;

    @Override
    public void onClose() {
            super.onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Vec2i a = ClientUtil.getMouse((int) mouseX, (int) mouseY);
        mouseX = a.getX();
        mouseY = a.getY();
        float scale = 2f;
        float width = 900 / scale + 20 + 40;
        float height = 650 / scale;
        float leftPanel = 200 / scale;
        float x = MathUtil.calculateXPosition(mc.getMainWindow().scaledWidth() / 2f, width);
        float y = MathUtil.calculateXPosition(mc.getMainWindow().scaledHeight() / 2f, height);

        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + leftPanel + 17.5f, y + 64 / 2f, width - leftPanel * 3 - 11, height - 64 / 2f)) {
            scroll += delta * 30;
        }
        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + (100f + 12) + 155 + 15 + 20 - 1 + 40, y + 85, 143 + 10, height - 100)) {
            scrollT += delta * 15;
        }

        ColorComponent.opened = null;
        return super.mouseScrolled(mouseX, mouseY, delta);

    }


    @Override
    public void closeScreen() {
        if (typing || !searchText.isEmpty()) {
            typing = false;
            searchText = "";
        }
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    boolean searchOpened;
    float seacrh;

    private String searchText = "";
    public static boolean typing;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        float scale = 2f;
        float width = 900 / scale + 20 + 40;
        float height = 650 / scale;
        float leftPanel = 200 / scale;
        float x = MathUtil.calculateXPosition(mc.getMainWindow().scaledWidth() / 2f, width);
        float y = MathUtil.calculateXPosition(mc.getMainWindow().scaledHeight() / 2f, height);
        xPanel = x;
        yPanel = y;
        animation = MathUtil.lerp(animation, 0, 5);

        Vec2i fixed = ClientUtil.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        int finalMouseX = mouseX;
        int finalMouseY = mouseY;

        mc.gameRenderer.setupOverlayRendering(2);
        renderBackground(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);
        renderThemes(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);
        renderCategories(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);
        renderComponents(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);
        renderColorPicker(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);
        renderSearchBar(matrixStack, x, y, width, height, leftPanel, finalMouseX, finalMouseY);

        mc.gameRenderer.setupOverlayRendering();


    }

    void renderColorPicker(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        if (ColorComponent.opened != null) {
            ColorComponent.opened.draw(matrixStack, mouseX, mouseY);
        }
    }

    void renderBackground(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeString = sdf.format(new Date());
        String time = timeString;

        // Исправляем формат даты: "dd.MM" вместо "DD.MM"
        SimpleDateFormat sss = new SimpleDateFormat("dd.MM");
        String timeString1 = sss.format(new Date());
        String time1 = timeString1;

        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        DisplayUtils.drawShadow(x, y, width, height, 20, new Color(22, 24, 28).getRGB());
        DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1, height + 1, 20, new Color(80, 85, 95).getRGB());
        DisplayUtils.drawRoundedRect(x, y, width, height, 20, new Color(10, 10, 10).getRGB());
        DisplayUtils.drawRectVerticalW(xPanel + (100f + 12) + 155 + 10 + 40, y, 1, height, new Color(53, 55, 60).getRGB(), new Color(53, 55, 60).getRGB());
        DisplayUtils.drawCircle2(x + 5 + leftPanel + 6 - 75 - 4, y + 30, 1, 500, 10, 2, true, -1);
        Fonts.msBold[19].drawString(matrixStack, "Crysense", x + 5 + leftPanel - 75 + 17.5 + 2.5f - 4, y + 22.5f, -1);
        Fonts.msBold[12].drawString(matrixStack, time, x + 5 + leftPanel - 75 + 17.5 + 7.5f + 2.5f - 4, y + 22.5f + 10 + 1, new Color(100, 100, 100).getRGB());
        Fonts.msBold[12].drawString(matrixStack, " | " + time1, x + 5 + leftPanel - 75 + 37.5 - 10 - 4 + Fonts.msBold[12].getWidth(time), y + 22.5f + 10 + 1, new Color(100, 100, 100).getRGB());
        Fonts.icons[12].drawString(matrixStack, "V", x + 5 + leftPanel - 75 + 17.5 + 2.5f - 4, y + 22.5f + 11 + 1, new Color(100, 100, 100).getRGB());
    }

    int index1 = Math.min(38, Fonts.msBold.length -1);
    int index2 = Math.min(15, Fonts.msBold.length -1);

    void renderThemes(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        List<Style> availableStyles = Vredux.getInstance().getStyleManager().getStyleList();

        Fonts.msBold[index1].drawCenteredString(matrixStack, "Theme", x + (100f + 12) + 155 + 20 + 45 + 40 - 8, y + 25 + 5, style.getFirstColor().getRGB());
        Fonts.msBold[index2].drawString(matrixStack, "Этот раздел добавлен для игры с", x + (100f + 12) + 155 + 20 + 15 + 40, y + 55, new Color(100, 100, 100).getRGB());
        Fonts.msBold[index2].drawString(matrixStack, "цветами клиента. Новый цвет - новое", x + (100f + 12) + 155 + 20 + 15 + 40, y + 65, new Color(100, 100, 100).getRGB());
        Fonts.msBold[index2].drawString(matrixStack, "настроение", x + (100f + 12) + 155 + 15 + 20 + 40, y + 75, new Color(100, 100, 100).getRGB());

        DisplayUtils.drawRoundedRect(x + (100f + 12) + 155 + 15 + 20 - 1 + 40, y + 85 - 1, 143 + 10 + 2, height - 100 + 2, 10, new Color(80, 85, 95).getRGB());
        DisplayUtils.drawRoundedRect(x + (100f + 12) + 155 + 15 + 20 + 40, y + 85, 143 + 10, height - 100, 10, new Color(13, 10, 10).getRGB());
        animateScrollT = MathUtil.lerp(animateScrollT, scrollT, 10);

        float startX = x + (100f + 12) + 155 + 30 + 20 + 40;
        float startY = y + 95 + animateScrollT;
        float boxWidth = 55;
        float boxHeight = 40;
        float padding = 10;
        int columns = 2;


        for (int i = 0; i < availableStyles.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float boxX = startX + col * (boxWidth + padding);
            float boxY = startY + row * (boxHeight + padding);

            ThemeComponent themeComponent = new ThemeComponent(availableStyles.get(i));
            themeComponent.setPosition(boxX, boxY, boxWidth, boxHeight);
            themeComponent.drawComponent(matrixStack, mouseX, mouseY);
            //themeComponent.mouseClicked(mouseX,mouseY,0);
        }
        int size1 = availableStyles.size() * 30;

        if (size1 < boxHeight) {
            scrollT = 0;
        } else {
            scrollT = MathHelper.clamp(scrollT, -(size1 - height), 0);
        }
    }



    void renderCategories(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        float heightCategory = 60 / 2f;
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        for (Category t : Category.values()) {

            if (t == current) {
                Stencil.initStencilToWrite();
                DisplayUtils.drawRectW(x, y + 41 - 11 + 41 + 2   + t.ordinal() * heightCategory, 100 + 20, 64 / 2f - 14 + 5 + 20, -1);
                Stencil.readStencilBuffer(1);
                DisplayUtils.drawRoundedRect(x - 5 - 10,y + 41 - 7 + 41 + t.ordinal() * heightCategory, 100 + 17.5f + 10 + 5, 64 / 2 + 5 - 14 + 2, 8, new Color(80, 85, 95).getRGB());
                DisplayUtils.drawRoundedRect(x - 5 + 1 - 10,y + 41 - 7 + 41 + t.ordinal() * heightCategory + 0.5f, 100 + 17.5f + 10 - 1.5f + 5, 64 / 2 + 5 - 14 + 2 - 1f, 8, new Color(10, 10, 10).getRGB());
                DisplayUtils.drawRoundedRect(x - 5 + 1 - 12.5f,y + 41 + 2.5f / 2 - 7 + 41 + 3 + t.ordinal() * heightCategory + 1f, 10 + 12 - 1.5f + 2, 32 / 2 + 2.5f - 14 + 2 + 8, 5, style.getFirstColor().getRGB());
                Stencil.uninitStencilBuffer();
            }
            t.anim = MathUtil.lerp(t.anim, (0), 10);
            if (t == Category.Theme || t == Category.Configurations) return;
                DisplayUtils.drawImage(new ResourceLocation("Vredux/images/clickgui/" + t.name().toLowerCase() + ".png"), (float) (x + 12 + t.anim), y + 3 + 38 + 2 + 41 + t.ordinal() * heightCategory, 8, 8, t == current ? style.getFirstColor().getRGB() : new Color(63, 75, 78).getRGB());
                Fonts.msMedium[20].drawString(matrixStack, t.name(), x + 20 + t.anim + 8, y + 41 + 2.5f + 41 + t.ordinal() * heightCategory, t == current ? style.getFirstColor().getRGB() : new Color(63, 75, 78).getRGB());

        }
        DisplayUtils.drawRectHorizontalW(x + leftPanel, y + 64 / 2f, 5, height - 64 / 2f, new Color(12, 13, 15, 50).getRGB(), new Color(12, 13, 15, 0).getRGB());
    }

    void renderComponents(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        Scissor.push();
        Scissor.setFromComponentCoordinates(x, y + 64 / 2f - 30, width, height - 64 / 2f + 29);
        drawComponents(matrixStack, mouseX, mouseY);
        Scissor.unset();
        Scissor.pop();
    }

    void renderSearchBar(MatrixStack matrixStack, float x, float y, float width, float height, float leftPanel, int mouseX, int mouseY) {
        seacrh = MathUtil.lerp(seacrh, 1, 15);
        DisplayUtils.drawRoundedRect(x + 5 + leftPanel + 6 + 5 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) - 191 - 100 - 0.5f, y + 7 + 40 - 0.5f, 10 + (width - leftPanel - 28 - (64 / 2f) / 2f) - 20 * (seacrh) - 215 + 1 - 40, 64 / 2f - 14 + 1, 8, new Color(80, 85, 95).getRGB());
            DisplayUtils.drawShadow(x + 5 + leftPanel + 6 + 5 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) - 191 - 100, y + 7 + 40, 10 + (width - leftPanel - 28 - (64 / 2f) / 2f) - 20 * (seacrh) - 215 - 40, 64 / 2f - 14, 12, new Color(17, 18, 21, (int) (seacrh * 255f)).darker().getRGB());
            DisplayUtils.drawRoundedRect(x + 5 + leftPanel + 6 + 5 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) - 191 - 100, y + 7 + 40, 10 + (width - leftPanel - 28 - (64 / 2f) / 2f) - 20 * (seacrh) - 215 - 40, 64 / 2f - 14, 8, new Color(17, 18, 21, (int) (seacrh * 255f)).getRGB());
            matrixStack.push();
            matrixStack.translate(x + leftPanel + 6 + ((width - leftPanel - 28 - (64 / 2f) / 2f) / 2f) * (1 - seacrh), y + 14, 0);
            matrixStack.scale(seacrh, seacrh, 1);
            matrixStack.translate(-(x + leftPanel + 6 + ((width - leftPanel - 28 - (64 / 2f) / 2f) / 2f) * (1 - seacrh)), -(y + 14), 0);
            float xOffset = 0;
            float fontTextWidth = Fonts.msBold[16].getWidth(searchText);

            if (fontTextWidth > (width - leftPanel - 32 - (64 / 2f) / 2f) * (seacrh)) {
                xOffset = fontTextWidth - ((width - leftPanel - 32 - (64 / 2f) / 2f) * (seacrh));
            }
            Stencil.initStencilToWrite();
            DisplayUtils.drawRectW(x + 5 + leftPanel + 6 - 3 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) * ((1 - seacrh)) - 90, y + 7 + 40, 10 + 3 - 20 - 40 + (width - leftPanel - 32 - (64 / 2f) / 2f) * (seacrh) - 220, 64 / 2f - 14, -1);
            Stencil.readStencilBuffer(1);
            if (searchText.isEmpty())
            Fonts.icons[12].drawString(matrixStack, searchText + (typing ? "" : "B"), x + 10 + leftPanel + 20 - 7.5f - 5 - 3f + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) * (1 - seacrh) + 5 - xOffset - 90, y + 14 + 2.5f + 40, new Color(53, 55, 60).getRGB());
            Fonts.msBold[16].drawString(matrixStack, searchText + (typing ? System.currentTimeMillis() % 1000 > 500 ? "_" : "" : searchText.isEmpty() ? "Поиск..." : ""), searchText.isEmpty() ? x + 10 + leftPanel + 20 - 7.5f + 3 + 5.5f - 5 + 5 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) - 191 - xOffset - 90 :
                    x + 10 + leftPanel + 20 - 7.5f + 3 + 5.5f - 5 + 5 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) - 191 - xOffset - 90 - 15, y + 14 + 40, new Color(53, 55, 60).getRGB());
            Stencil.uninitStencilBuffer();
            matrixStack.pop();
    }

    void drawComponents(MatrixStack stack, int mouseX, int mouseY) {

        List<ModuleComponent> moduleComponentList = objects.stream()
                .filter(moduleObject -> {
                    if (!searchText.isEmpty()) {
                        return true;
                    } else {
                        return moduleObject.function.getCategory() == current;
                    }
                }).toList();

        float scale = 2f;
        animateScroll = MathUtil.lerp(animateScroll, scroll, 15);
        float width = 900 / scale;
        float height = 650 / scale + 20;
        float leftPanel = 200 / scale;
        float x = MathUtil.calculateXPosition(mc.getMainWindow().scaledWidth() / 2f, width);
        float y = MathUtil.calculateXPosition(mc.getMainWindow().scaledHeight() / 2f, height);

        float offset = (float) (yPanel - 30 + (64 / 2f) + 12) + animateScroll;
        float size1 = 0;
        for (ModuleComponent component : moduleComponentList) {
            if (searchText.isEmpty()) {
                if (component.function.getCategory() != current) continue;
            } else {
                if (!component.function.getName().toLowerCase().contains(searchText.toLowerCase())) continue;
            }
            component.parent = this;
            component.setPosition((float) (xPanel + (100f + 12) + 10) + 30, offset, 285 / 2f, 37);
            component.drawComponent(stack, mouseX, mouseY);
            if (!component.components.isEmpty()) {
                for (Component settingComp : component.components) {
                    if (settingComp.setting != null && settingComp.setting.visible.get()) {
                        offset += settingComp.height;
                        size1 += settingComp.height;
                    }
                }
            }
            offset += component.height + 8;
            size1 += component.height + 8;
        }

        if (size1 < height) {
            scroll = 0;
        } else {
            scroll = MathHelper.clamp(scroll, -(size1 - height + 50), 0);
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        ColorComponent.opened = null;
        typing = false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        Vec2i fixed = ClientUtil.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        for (ModuleComponent m : objects) {
            if (searchText.isEmpty()) {
                if (m.function.getCategory() != current) continue;
            } else {
                if (!m.function.getName().toLowerCase().contains(searchText.toLowerCase())) continue;
            }
            m.mouseReleased((int) mouseX, (int) mouseY, button);
        }
        if (ColorComponent.opened != null) {
            ColorComponent.opened.unclick((int) mouseX, (int) mouseY);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            this.minecraft.keyboardListener.enableRepeatEvents(false);
        }
        boolean ctrlDown = Screen.hasControlDown();
        if (typing) {
            if (ctrlDown && keyCode == GLFW.GLFW_KEY_V) {
                String pasteText = GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
                searchText += pasteText;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchText.isEmpty()) {
                    searchText = searchText.substring(0, searchText.length() - 1);
                }
            }
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                searchText = "";
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                typing = false;
            }
        }

        for (ModuleComponent m : objects) {
            if (searchText.isEmpty()) {
                if (m.function.getCategory() != current) continue;
            } else {
                if (!m.function.getName().toLowerCase().contains(searchText.toLowerCase())) continue;
            }
            m.keyTyped(keyCode, scanCode, modifiers);
        }

        if (ModuleComponent.binding != null) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                ModuleComponent.binding.function.setBind(0);
            } else {
                ModuleComponent.binding.function.setBind(keyCode);
            }
            ModuleComponent.binding = null;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (typing)
            searchText += codePoint;

        for (ModuleComponent m : objects) {
            if (searchText.isEmpty()) {
                if (m.function.getCategory() != current) continue;
            } else {
                if (!m.function.getName().toLowerCase().contains(searchText.toLowerCase())) continue;
            }
            m.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        Vec2i fixed = ClientUtil.getMouse((int) mouseX, (int) mouseY);

        mouseX = fixed.getX();
        mouseY = fixed.getY();

        float scale = 2f;
        float width = 900 / scale + 20 + 40;
        float height = 650 / scale;
        float leftPanel = 199 / scale;
        float x = MathUtil.calculateXPosition(mc.getMainWindow().scaledWidth() / 2f, width);
        float y = MathUtil.calculateXPosition(mc.getMainWindow().scaledHeight() / 2f, height);
        float heightCategory = 60 / 2f;


        if (ColorComponent.opened != null) {
            if (!ColorComponent.opened.click((int) mouseX, (int) mouseY))
                return super.mouseClicked(mouseX, mouseY, button);
        }

        for (Category t : Category.values()) {
            if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x, y + 32.5f + 51 - 10 + t.ordinal() * heightCategory, leftPanel + 17.5f, heightCategory)) {
                if (current == t) continue;
                if ((t == Category.Theme || t == Category.Configurations)) continue;
                current = t;
                animation = 1;
                scroll = 0;
                searchText = "";
                ColorComponent.opened = null;
                typing = false;
            }
        }


        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x, y + 64 / 2f, width, height - 64 / 2f)) {
            for (ModuleComponent m : objects) {
                if (searchText.isEmpty()) {
                    if (m.function.getCategory() != current) continue;
                } else {
                    if (!m.function.getName().toLowerCase().contains(searchText.toLowerCase())) continue;
                }
                m.mouseClicked((int) mouseX, (int) mouseY, button);
            }
        }


        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + width - (64 / 2f) / 2f - 1, y + (64 / 2f) / 2f - 5, 10, 10)) {
            typing = false;
            searchText = "";
            searchOpened = !searchOpened;
        }


        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + leftPanel + 5 + 6 + ((width - leftPanel - 12 - (64 / 2f) / 2f) / 2f) * (1 - seacrh) - 100 - 10, y + 7 + 40, (width - leftPanel - 12 - (64 / 2f) / 2f) - 6 * (seacrh) - 225, 64 / 2f - 14)) {
            typing = !typing;
        } else {
            typing = false;
        }
        mouseX = fixed.getX();
        mouseY = fixed.getY();
           /* if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + leftPanel + 6, y + 64 / 2f, width - leftPanel - 12, height - 64 / 2f)) {
                List<Style> availableStyles = sqwezz.getInstance().getStyleManager().getStyleList();
                float startX = x + (100f + 12) + 155 + 30;
                float startY = y + 95;
                float boxWidth = 55;
                float boxHeight = 40;
                float padding = 10;
                int columns = 2;

                for (int i = 0; i < availableStyles.size(); i++) {
                    int row = i / columns;
                    int col = i % columns;
                    float boxX = startX + col * (boxWidth + padding);
                    float boxY = startY + row * (boxHeight + padding);
                    if (row > 3) break;

                    if (MathUtil.isInRegion((float) mouseX, (float) mouseY, boxX, boxY, boxWidth, boxHeight)) {
                        sqwezz.getInstance().getStyleManager().setCurrentStyle(availableStyles.get(i));
                        break;
                    }
                }
            }*/

        if (button == 0) {
            if (ColorComponent.opened == null) {
                if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + 40 + leftPanel + 6, y + 64 / 2f + 50, width - leftPanel - 12, height - 64 / 2f - 70)) {
                    float startX = x + (100f + 12) + 155 + 30 + 20 + 40;
                    float startY = y + 95;
                    float boxWidth = 55;
                    float boxHeight = 40;
                    float padding = 10;
                    int columns = 2;

                    List<Style> availableStyles = Vredux.getInstance().getStyleManager().getStyleList();
                    for (int i = 0; i < availableStyles.size(); i++) {
                        int row = i / columns;
                        int col = i % columns;
                        float boxX = startX + col * (boxWidth + padding);
                        float boxY = startY + row * (boxHeight + padding) + scrollT;

                        if (MathUtil.isHovered((float) mouseX, (float) mouseY, boxX, boxY, boxWidth, boxHeight)) {
                            Vredux.getInstance().getStyleManager().setCurrentStyle(availableStyles.get(i));
                        }
                    }
                }
            }
        }

//        Style style = sqwezz.getInstance().getStyleManager().getCurrentStyle();
//        if (MathUtil.isInRegion((float) mouseX, (float) mouseY, x + leftPanel + 6, y + 64 / 2f, width - leftPanel - 12, height - 64 / 2f)) {
//            ThemeComponent component = new ThemeComponent(style);
//            component.mouseClicked((int) mouseX, (int) mouseY,0);
//        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
