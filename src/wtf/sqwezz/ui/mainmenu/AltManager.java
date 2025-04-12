package wtf.sqwezz.ui.mainmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.utils.ScaleMath;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.client.Vec2i;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.shader.ShaderUtil;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.glfw.GLFW;
import wtf.sqwezz.utils.player.MouseUtil;
import wtf.sqwezz.utils.font.Fonts;
import wtf.sqwezz.utils.render.*;
import via.MouseUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static wtf.sqwezz.utils.client.IMinecraft.mc;

public class AltManager extends Screen {

    public AltManager() {
        super(new StringTextComponent(""));
    }

    public final StopWatch timer = new StopWatch();
    public float o = 0;

    public ArrayList<Account> accounts = new ArrayList<>();

    public float scroll;
    public float scrollAn;

    private String altName = "";
    private boolean typing;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);


        MainWindow mainWindow = mc.getMainWindow();
        int windowWidth = ClientUtil.calc(mainWindow.getScaledWidth());
        int windowHeight = ClientUtil.calc(mainWindow.getScaledHeight());

        int logoWidth =
                1920 / 2;
        int logoHeight = 1080 / 2;

        scrollAn = MathUtil.lerp(scrollAn, scroll, 5);

        for (float i=0;i<1488;i++) {
            if(timer.isReached(10)){
                o++;
                i=0;
                timer.reset();
            }
        }


        float offset = 6f;
        float width = 250f, height = 270f;
        float x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f, y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        // Квадрат фона
        DisplayUtils.drawSexyRect(x - offset, y - offset, width + offset * 2f, height + offset * 2f, 5f);

        // Первый заголовок
        Fonts.gilroyBold[22].drawString(matrixStack, "Добавьте новый аккаунт!", x + offset + Fonts.icons1[16].getWidth("D") + 2 - 10, y + offset, -1);
        Fonts.gilroyBold[12].drawString(matrixStack, "Смени никнейм, и отправляйся в игру!", x + offset, y + offset + 18f, ColorUtils.rgb(180, 180, 180));

        // Квадратик для ввода ника
        DisplayUtils.drawRoundedRect(x + offset, y + offset + 25f, width - offset * 2f, 20f, 2f, ColorUtils.rgba(60, 60, 60, 215));
        Scissor.push();
        Scissor.setFromComponentCoordinates(x + offset, y + offset + 25f, width - offset * 2f, 20f);
        Fonts.msSemiBold[15].drawString(matrixStack, typing ? (altName + (typing ? "|" : "")) : "Введите сюда ваш ник!", x + offset + 2f, y + offset + 32.5f, ColorUtils.rgb(152, 152, 152));
        Scissor.unset();
        Scissor.pop();

        // Знак для ввода рандомного ника
        Fonts.msBold[22].drawString(matrixStack, "+", x + width - offset - 12.5f, y + offset + 31f, -1);

        // Второй заголовок
        Fonts.gilroyBold[22].drawString(matrixStack, "Аккаунты:", x + offset, y + offset + 60f, -1);
        Fonts.gilroyBold[12].drawString(matrixStack, "Выбери аккаунт из списка!", x + offset, y + offset + 73f, ColorUtils.rgb(180, 180, 180));

        // Вывод никнеймов
        DisplayUtils.drawRoundedRect(x + offset, y + offset + 80f, width - offset * 2f, 177.5f, 2f, ColorUtils.rgba(80, 80, 80, 215));

        // Надпись при пустом листе аккаунтов
        if (accounts.isEmpty()) Fonts.msBold[22].drawCenteredString(matrixStack, "Их нету..", x + width / 2f, y + offset + 165.75f, -1);

        // Основной функционал для показа аккаунтов
        float size = 0f, iter = scrollAn, offsetAccounts = 0f;
        Scissor.push();
        Scissor.setFromComponentCoordinates(x + offset, y + offset + 80f, width - offset * 2f, 177.5f);
        for (Account account : accounts) {
            float scrollY = y + iter * 22f;

            DisplayUtils.drawRoundedRect(x + offset + 2f, scrollY + offset + 82f + offsetAccounts, width - offset * 2f - 4f, 20f, 2f, ColorUtils.rgb(101, 101, 101));

            Fonts.msSemiBold[16].drawString(matrixStack, account.accountName, x + offset + 25f, scrollY + offset + 82f + 8f + offsetAccounts, -1);

            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(x + offset + 4f + 0.5f, scrollY + offset + 84f + offsetAccounts, 16, 16, 2f, Color.BLACK.getRGB());
            Stencil.readStencilBuffer(1);
            mc.getTextureManager().bindTexture(account.skin);
            AbstractGui.drawScaledCustomSizeModalRect(x + offset + 4f + 0.5f, scrollY + offset + 84f + offsetAccounts, 8F, 8F, 8F, 8F, 16, 16, 64, 64);
            Stencil.uninitStencilBuffer();

            iter++;
            size++;
        }
        scroll = MathHelper.clamp(scroll, size > 8 ? -size + 4 : 0, 0);
        Scissor.unset();
        Scissor.pop();

        // Показ текущего ника
        Fonts.gilroyBold[12].drawString(matrixStack, "Ваш ник - " + mc.session.getUsername() + ".", x + offset, y + height - offset / 2, ColorUtils.rgb(180, 180, 180));

        mc.gameRenderer.setupOverlayRendering();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!altName.isEmpty() && typing)
                altName = altName.substring(0, altName.length() - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (!altName.isEmpty()) {
                accounts.add(new Account(altName));
                AltConfig.updateFile();
            }
            typing = false;
            altName = "";
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (altName.length() <= 20) altName += Character.toString(codePoint);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        float offset = 6f;
        float width = 250f, height = 270f;
        float x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f, y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        if (MouseUtils.isHovered((int) mouseX, (int) mouseY, x + width - offset - 12.5f, y + offset + 31f, Fonts.msBold[22].getWidth("?"), Fonts.msBold[22].getFontHeight())) {
            accounts.add(new Account(RandomStringUtils.randomAlphabetic(8)));
            AltConfig.updateFile();
        }
        if (MouseUtils.isHovered((int) mouseX, (int) mouseY, x + offset, y + offset + 25f, width - offset * 2f, 20f) && !MouseUtils.isHovered((int) mouseX, (int) mouseY, x + width - offset - 12.5f, y + offset + 31f, Fonts.msBold[22].getWidth("?"), Fonts.msBold[22].getFontHeight())) {
            typing = !typing;
        }

        // Основной функционал позволяющий позволяющий брать/удалять ник
        float iter = scrollAn, offsetAccounts = 0f;
        Iterator<Account> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();

            float scrollY = y + iter * 22f;

            if (MouseUtils.isHovered((int) mouseX, (int) mouseY, x + offset + 2f, scrollY + offset + 82f + offsetAccounts, width - offset * 2f - 4f, 20f)) {
                if (button == 0) {
                    mc.session = new Session(account.accountName, "", "", "mojang");
                    return true; // Добавлено
                } else if (button == 1) {
                    iterator.remove();
                    AltConfig.updateFile();
                    return true; // Добавлено
                }
            }

            iter++;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        // Скролл
        float offset = 6f;
        float width = 250f, height = 270f;
        float x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f, y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        if (MouseUtils.isHovered((int) mouseX, (int) mouseY, x + offset, y + offset + 80f, width - offset * 2f, 177.5f)) scroll += delta * 1;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
    }

    @Override
    public void tick() {
        super.tick();
    }
}