package wtf.sqwezz.ui.mainmenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.client.Vec2i;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.KawaseBlur;
import wtf.sqwezz.utils.render.Stencil;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class                                    MainScreen extends Screen implements IMinecraft {
    public MainScreen() {
        super(ITextComponent.getTextComponentOrEmpty(""));

    }

    private final ResourceLocation backmenu = new ResourceLocation("VreduxBeta/images/mainmenu.png");
    private final ResourceLocation logo = new ResourceLocation("VreduxBeta/images/Mainmenu_logo.png");

    private final List<Button> buttons = new ArrayList<>();

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        float widthButton = 430 / 3f;


        for (Particle particle : particles) {
            particle.y = ThreadLocalRandom.current().nextInt(-5, height);
        }
        float x = ClientUtil.calc(width) / 2f - widthButton / 2f;
        float y = Math.round(ClientUtil.calc(height) / 2f + 1);
        buttons.clear();

        buttons.add(new Button(x, y, widthButton, 58 / 2f, "Singleplayer", () -> {
            mc.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        y += 50 / 2f + 5;
        buttons.add(new Button(x, y, widthButton, 58 / 2f, "Multiplayer", () -> {
            mc.displayGuiScreen(new MultiplayerScreen(this));
        }));
        y += 50 / 2f + 5;

        buttons.add(new Button(x, y, widthButton, 58 / 2f, "Options", () -> {
            mc.displayGuiScreen(new OptionsScreen(this, mc.gameSettings));
        }));
        y -= 8 / 2f + 5;

        y += 68 / 2f + 5;
        buttons.add(new Button(x, y, widthButton, 58 / 2f, "Exit", mc::shutdownMinecraftApplet));
    }



    private static final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

    private final StopWatch stopWatch = new StopWatch();
    static boolean start = false;



    private void drawButtons(MatrixStack stack, int mX, int mY, float pt) {

        buttons.forEach(b -> b.render(stack, mX, mY, pt));
    }

    public static final ResourceLocation button = new ResourceLocation("VreduxBeta/images/button.png");

    private class Particle {

        private final float x;
        private float y;
        private float size;

        public Particle() {
            x = ThreadLocalRandom.current().nextInt(0, mc.getMainWindow().getScaledWidth());
            y = 0;
            size = 0;
        }

        public void update() {
            y += 1f;
        }

        public void render(MatrixStack stack) {;
            size += 0.1f;
            GlStateManager.pushMatrix();
            GlStateManager.translated((x + Math.sin((System.nanoTime() / 1000000000f)) * 5), y, 0);
            GlStateManager.rotatef(size * 20, 0, 0, 1);
            GlStateManager.translated(-(x + Math.sin((System.nanoTime() / 1000000000f)) * 5), -y, 0);
            float multi = 1 - MathHelper.clamp((y / mc.getMainWindow().getScaledHeight()), 0, 1);
            y += 1;
            Fonts.damage.drawText(stack, "A", (float) (x + Math.sin((System.nanoTime() / 1000000000f)) * 5), y, -1, MathHelper.clamp(size * multi, 0, 9));
            GlStateManager.popMatrix();
            if (y >= mc.getMainWindow().getScaledHeight()) {
                particles.remove(this);
            }
        }

    }

    @AllArgsConstructor
    private class Button {
        @Getter
        private final float x, y, width, height;
        private String text;
        private Runnable action;

        public void render(MatrixStack stack, int mouseX, int mouseY, float pt) {
            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(x, y + 2, width, height, 5, -1);
            Stencil.readStencilBuffer(1);
            KawaseBlur.blur.BLURRED.draw();
            Stencil.uninitStencilBuffer();

            DisplayUtils.drawImage(button, x, y + 2, width, height, ColorUtils.rgba(10, 10, 10, 120));
            int color = ColorUtils.rgba(255, 255, 255, 130);
            if (MathUtil.isInRegion(mouseX, mouseY, x, y, width, height)) {
                color = ColorUtils.rgba(115, 115, 170 ,255);
            }
            Fonts.sfMedium.drawCenteredText(stack, text, x + width / 2f, y + height / 2f - 5.5f + 2, color, 9f);


        }

        public void click(int mouseX, int mouseY, int button) {
            if (MathUtil.isInRegion(mouseX, mouseY, x, y, width, height)) {
                action.run();
            }
        }

    }

}