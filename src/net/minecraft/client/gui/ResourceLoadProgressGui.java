package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import wtf.sqwezz.functions.api.FunctionRegistry;
import wtf.sqwezz.ui.midnight.ClickGui;
import wtf.sqwezz.utils.animations.Alternatetext;
import wtf.sqwezz.utils.animations.Direction;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.PropertiesOrdered;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.settings.impl.SelfDestruct;
import wtf.sqwezz.utils.animations.impl.EaseBackIn;
import wtf.sqwezz.utils.font.Fonts;

public class ResourceLoadProgressGui extends LoadingGui {
    private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int field_238627_b_ = ColorHelper.PackedColor.packColor(255, 239, 50, 61);
    private static final int field_238628_c_ = field_238627_b_ & 16777215;
    private final Minecraft mc;
    private long switchInterval = 1000;
    private Vector2f position = new Vector2f(0, 0);
    private int currentImageIndex = 1;
    public static Vector2f size = new Vector2f(500, 400);
    private final IAsyncReloader asyncReloader;
    private final Consumer<Optional<Throwable>> completedCallback;
    private final boolean reloading;
    int t_color = Color.WHITE.getRGB();
    private float progress;
    private long lastSwitchTime = 0;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;
    private int colorBackground = field_238628_c_;
    private int colorBar = field_238628_c_;
    private int colorOutline = 16777215;
    private int colorProgress = 16777215;
    private GlBlendState blendState = null;
    private boolean fadeOut = false;

    public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
        this.mc = p_i225928_1_;
        this.asyncReloader = p_i225928_2_;
        this.completedCallback = p_i225928_3_;
        this.reloading = false;
    }

    public static void loadLogoTexture(Minecraft mc) {
        mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new MojangLogoTexture());
    }

    public EaseBackIn animation = new EaseBackIn(1250, 1, 2F, Direction.FORWARDS);

    private Alternatetext text = new Alternatetext(Arrays.asList("...", "...", "...", "...", "...", "...", "...", "...", "..."), 400);


    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        FunctionRegistry functionRegistry = Vredux.getInstance().getFunctionRegistry();

        SelfDestruct selfDestruct = functionRegistry.getSelfDestruct();
        if (selfDestruct.unhooked) {
            int i = this.mc.getMainWindow().getScaledWidth();
            int j = this.mc.getMainWindow().getScaledHeight();
            long k = Util.milliTime();
            if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.fadeInStart == -1L)
            {
                this.fadeInStart = k;
            }
            float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
            float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
            float f2;

            if (f >= 1.0F)
            {
                this.fadeOut = true;

                if (this.mc.currentScreen != null)
                {
                    this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
                }

                int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
                fill(matrixStack, 0, 0, i, j, this.colorBackground | l << 24);
                f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
            }
            else if (this.reloading)
            {
                if (this.mc.currentScreen != null && f1 < 1.0F)
                {
                    this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
                }

                int i2 = MathHelper.ceil(MathHelper.clamp((double)f1, 0.15D, 1.0D) * 255.0D);
                fill(matrixStack, 0, 0, i, j, this.colorBackground | i2 << 24);
                f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
            }
            else
            {
                fill(matrixStack, 0, 0, i, j, this.colorBackground | -16777216);
                f2 = 1.0F;
            }

            int j2 = (int)((double)this.mc.getMainWindow().getScaledWidth() * 0.5D);
            int i1 = (int)((double)this.mc.getMainWindow().getScaledHeight() * 0.5D);
            double d0 = Math.min((double)this.mc.getMainWindow().getScaledWidth() * 0.75D, (double)this.mc.getMainWindow().getScaledHeight()) * 0.25D;
            int j1 = (int)(d0 * 0.5D);
            double d1 = d0 * 4.0D;
            int k1 = (int)(d1 * 0.5D);
            this.mc.getTextureManager().bindTexture(MOJANG_LOGO_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.blendFunc(770, 1);
            RenderSystem.alphaFunc(516, 0.0F);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, f2);
            boolean flag = true;

            if (this.blendState != null)
            {
                this.blendState.apply();

                if (!this.blendState.isEnabled() && this.fadeOut)
                {
                    flag = false;
                }
            }

            if (flag)
            {
                blit(matrixStack, j2 - k1, i1 - j1, k1, (int)d0, -0.0625F, 0.0F, 120, 60, 120, 120);
                blit(matrixStack, j2, i1 - j1, k1, (int)d0, 0.0625F, 60.0F, 120, 60, 120, 120);
            }

            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableBlend();
            int l1 = (int)((double)this.mc.getMainWindow().getScaledHeight() * 0.8325D);
            float f3 = this.asyncReloader.estimateExecutionSpeed();
            this.progress = MathHelper.clamp(this.progress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
            Reflector.ClientModLoader_renderProgressText.call();

            if (f < 1.0F)
            {
                this.func_238629_a_(matrixStack, i / 2 - k1, l1 - 5, i / 2 + k1, l1 + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
            }

            if (f >= 2.0F)
            {
                this.mc.setLoadingGui((LoadingGui)null);
            }

            if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F))
            {
                this.fadeOutStart = Util.milliTime();

                try
                {
                    this.asyncReloader.join();
                    this.completedCallback.accept(Optional.empty());
                }
                catch (Throwable throwable)
                {
                    this.completedCallback.accept(Optional.of(throwable));
                }

                if (this.mc.currentScreen != null)
                {
                    this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
                }
            }
            return;
        }
        int width = this.mc.getMainWindow().getScaledWidth();
        int height = this.mc.getMainWindow().getScaledHeight();
        long k = Util.milliTime();

        if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float) (k - this.fadeOutStart) / 1000F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float) (k - this.fadeInStart) / 500.0F : -1.0F;
        float f2 = 1;

        if (f >= 1.0F) {
            this.fadeOut = true;

            animation.setDirection(Direction.BACKWARDS);

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
            }
            int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            DisplayUtils.drawRect(0, 0, width, height, new Color(100, 100, 100, l).getRGB());
            f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);

        } else if (this.reloading) {
            if (this.mc.currentScreen != null && f1 < 1.0F) {
                this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
            }

        }
        DisplayUtils.drawRect(0, 0, width, height, ColorUtils.rgba(25, 25, 25, (int) (255 * f2)));

        GlStateManager.pushMatrix();

        GlStateManager.translated(width / 2f, height / 2f, 0);
        GlStateManager.rotatef((float) (animation.getEndPoint() * 360), 0, 0, 100);
        float scale = (float) ((float) 2 - MathHelper.clamp(animation.getEndPoint(), 0, 100));
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-(width / 2f), -(height / 2f), 0);

        GlStateManager.popMatrix();
        float finalF = f2;
        {
            GlStateManager.pushMatrix();

            GlStateManager.translated(width / 2f, height / 2f, 0);
            GlStateManager.rotatef((float) (animation.getEndPoint() * 360), 0, 0, 100);

            GlStateManager.scaled(scale, scale, scale);
            GlStateManager.translated(-(width / 2f), -(height / 2f), 0);
            GlStateManager.popMatrix();

// === Кастомная картинка ===
            final ResourceLocation CUSTOM_IMAGE = new ResourceLocation("Vredux/images/loading.png");
            this.mc.getTextureManager().bindTexture(CUSTOM_IMAGE);
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, finalF);
            int imageW = 64, imageH = 64;

// Используем width и height, которые автоматически обновляются в зависимости от размера окна
            int imageX = width / 2 - imageW / 2;

// Устанавливаем imageY, чтобы картинка оставалась на нужной высоте
            int imageY = height / 2 - 100;  // поднята картинка на 60 пикселей выше центра

            blit(matrixStack, imageX, imageY, 0, 0, imageW, imageH, imageW, imageH);
            RenderSystem.disableBlend();

// Время анимации (общий период 2 секунды для букв + точки)
            float animationTime = (System.currentTimeMillis() % 2000) / 2000f; // 0.0 до 1.0
            String baseText = "Loading";
            int maxChars = baseText.length(); // 7 букв

// Прогресс для букв (первая половина анимации: 0.0 до 0.5)
            float letterProgress = Math.min(animationTime / 0.5f, 1.0f); // 0.0 до 1.0 за 1 секунду
            int charsToShow = (int) (maxChars * letterProgress); // От 0 до 7
            String animatedText = baseText.substring(0, Math.min(charsToShow + 1, maxChars));

// Анимация точек (ускорена: цикл 0.5 секунды)
            String dots = "";
            if (letterProgress >= 1.0f) { // Точки начинаются, когда "Loading" полностью появилось
                float dotsTime = (System.currentTimeMillis() % 500) / 500f; // Прогресс для точек (0.0 до 1.0 за 0.5 секунды)
                int dotCount = (int) (dotsTime * 4) % 4; // 0-3 точки
                dots = ".".repeat(dotCount);
            }
            animatedText += dots;

// Рисуем текст
            Fonts.gilroyBold[16].drawCenteredString(
                    matrixStack,
                    animatedText,
                    width / 2f,
                    height / 3f + 84,
                    ColorUtils.rgba(255, 255, 255, (int) (255 * finalF))
            );

// Отрисовка аниме-тянки (пока без условия, показываем сразу)
            float animeTime = (System.currentTimeMillis() % 1000) / 1000f; // Цикл 1 секунда
            scale = 1.0f + 0.1f * (float) Math.sin(animeTime * Math.PI * 2);
            float alpha = Math.min(animeTime / 0.5f, 1.0f); // Плавное появление за 0.5 секунды

// Позиция и размер спрайта (нижний правый угол)
            float animeWidth = 64.0f; // Ширина спрайта
            float animeHeight = 64.0f; // Высота спрайта
            float posX = width - animeWidth - 10; // Отступ 10 пикселей от края
            float posY = height - animeHeight - 10;

// Сохраняем текущее состояние матрицы
            matrixStack.push();
// Применяем масштабирование и прозрачность
            matrixStack.translate(posX + animeWidth / 2, posY + animeHeight / 2, 0);
            matrixStack.scale(scale, scale, 1.0f);
            matrixStack.translate(-animeWidth / 2, -animeHeight / 2, 0);

// Рисуем текстуру тянки
// Предполагаем, что текстура будет зарегистрирована как Identifier("yourmodid", "textures/gui/anime_girl.png")
/*
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
RenderSystem.setShader(GameRenderer::getPositionTexShader);
RenderSystem.setShaderTexture(0, new Identifier("yourmodid", "textures/gui/anime_girl.png"));
RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha * finalF);
drawTexture(matrixStack, 0, 0, 0, 0, (int) animeWidth, (int) animeHeight, (int) animeWidth, (int) animeHeight);
*/
// Заглушка: текст вместо текстуры
            Fonts.gilroyBold[16].drawCenteredString(
                    matrixStack,
                    "Anime Girl Here!",
                    animeWidth / 2,
                    animeHeight / 2,
                    ColorUtils.rgba(255, 192, 203, (int) (255 * alpha * finalF))
            );

// Восстанавливаем матрицу
            matrixStack.pop();


            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSwitchTime >= switchInterval) {
                switchImage();
                lastSwitchTime = currentTime;
            }
            //  final ResourceLocation resourceLocation = new ResourceLocation("velon/images/hud/destomeiconslogo2.png");
            // DisplayUtils.drawImage(resourceLocation,width / 2f - 24, height / 2f + 4, imageWidth, imageHeight, ColorUtils.rgba(255, 255, 255, (int) (255 * finalF)));
        };


        int l1 = (int) ((double) this.mc.getMainWindow().getScaledHeight() * 0.8325D);
        float f3 = this.asyncReloader.estimateExecutionSpeed();
        this.progress = MathHelper.clamp(this.progress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
        Reflector.ClientModLoader_renderProgressText.call();

        if (f >= 2.0F) {
            this.mc.setLoadingGui((LoadingGui) null);
        }

        if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F)) {
            this.fadeOutStart = Util.milliTime();

            try {
                this.asyncReloader.join();
                this.completedCallback.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.completedCallback.accept(Optional.of(throwable));
            }

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
            }
        }
    }
    private static final int TOTAL_IMAGES = 8;
    private void switchImage() {
        currentImageIndex++;
        if (currentImageIndex > TOTAL_IMAGES) {
            currentImageIndex = 1;
        }
    }
    private void func_238629_a_(MatrixStack p_238629_1_, int p_238629_2_, int p_238629_3_, int p_238629_4_, int p_238629_5_, float p_238629_6_) {
        int i = MathHelper.ceil((float) (p_238629_4_ - p_238629_2_ - 2) * this.progress);
        int j = Math.round(p_238629_6_ * 255.0F);

        if (this.colorBar != this.colorBackground) {
            int k = this.colorBar >> 16 & 255;
            int l = this.colorBar >> 8 & 255;
            int i1 = this.colorBar & 255;
            int j1 = ColorHelper.PackedColor.packColor(j, k, l, i1);
            fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_4_, p_238629_5_, j1);
        }

        int j2 = this.colorOutline >> 16 & 255;
        int k2 = this.colorOutline >> 8 & 255;
        int l2 = this.colorOutline & 255;
        int i3 = ColorHelper.PackedColor.packColor(j, j2, k2, l2);
        fill(p_238629_1_, p_238629_2_ + 1, p_238629_3_, p_238629_4_ - 1, p_238629_3_ + 1, i3);
        fill(p_238629_1_, p_238629_2_ + 1, p_238629_5_, p_238629_4_ - 1, p_238629_5_ - 1, i3);
        fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_2_ + 1, p_238629_5_, i3);
        fill(p_238629_1_, p_238629_4_, p_238629_3_, p_238629_4_ - 1, p_238629_5_, i3);
        int k1 = this.colorProgress >> 16 & 255;
        int l1 = this.colorProgress >> 8 & 255;
        int i2 = this.colorProgress & 255;
        i3 = ColorHelper.PackedColor.packColor(j, k1, l1, i2);
        fill(p_238629_1_, p_238629_2_ + 2, p_238629_3_ + 2, p_238629_2_ + i, p_238629_5_ - 2, i3);
    }

    public boolean isPauseScreen() {
        return true;
    }

    public void update() {
        this.colorBackground = field_238628_c_;
        this.colorBar = field_238628_c_;
        this.colorOutline = 16777215;
        this.colorProgress = 16777215;

        if (Config.isCustomColors()) {
            try {
                String s = "optifine/color.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation)) {
                    return;
                }

                InputStream inputstream = Config.getResourceStream(resourcelocation);
                Config.dbg("Loading " + s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                this.colorBackground = readColor(properties, "screen.loading", this.colorBackground);
                this.colorOutline = readColor(properties, "screen.loading.outline", this.colorOutline);
                this.colorBar = readColor(properties, "screen.loading.bar", this.colorBar);
                this.colorProgress = readColor(properties, "screen.loading.progress", this.colorProgress);
                this.blendState = ShaderPackParser.parseBlendState(properties.getProperty("screen.loading.blend"));
            } catch (Exception exception) {
                Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_, int p_readColor_2_) {
        String s = p_readColor_0_.getProperty(p_readColor_1_);

        if (s == null) {
            return p_readColor_2_;
        } else {
            s = s.trim();
            int i = parseColor(s, p_readColor_2_);

            if (i < 0) {
                Config.warn("Invalid color: " + p_readColor_1_ + " = " + s);
                return i;
            } else {
                Config.dbg(p_readColor_1_ + " = " + s);
                return i;
            }
        }
    }

    private static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        } else {
            p_parseColor_0_ = p_parseColor_0_.trim();

            try {
                return Integer.parseInt(p_parseColor_0_, 16) & 16777215;
            } catch (NumberFormatException numberformatexception) {
                return p_parseColor_1_;
            }
        }
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    static class MojangLogoTexture extends SimpleTexture {
        public MojangLogoTexture() {
            super(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }

        protected TextureData getTextureData(IResourceManager resourceManager) {
            Minecraft minecraft = Minecraft.getInstance();
            VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();

            try (InputStream inputstream = getLogoInputStream(resourceManager, vanillapack)) {
                return new TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
            } catch (IOException ioexception1) {
                return new TextureData(ioexception1);
            }
        }

        private static InputStream getLogoInputStream(IResourceManager p_getLogoInputStream_0_, VanillaPack p_getLogoInputStream_1_) throws IOException {
            return p_getLogoInputStream_0_.hasResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE) ? p_getLogoInputStream_0_.getResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE).getInputStream() : p_getLogoInputStream_1_.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }
    }
}