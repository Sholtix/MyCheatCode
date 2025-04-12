package wtf.sqwezz.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import wtf.sqwezz.utils.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GifPudgeUtil {
    private static final List<ResourceLocation> gifFrames = new ArrayList<>();
    private static int currentFrame = 0;
    private static long lastFrameTime = 0;
    private static long frameDelay = 55;
    public static void GifRender(String resourceLocation,int x,int y,int width,int height,int numImages) {
        for (int i = 0; i < numImages; i++) {
            gifFrames.add(new ResourceLocation("Vredux/bayan/" + resourceLocation + i + ".png"));
        }
        if (System.currentTimeMillis() - lastFrameTime > frameDelay) {
            lastFrameTime = System.currentTimeMillis();
            currentFrame = (currentFrame + 1) % gifFrames.size();
        }
        ResourceLocation currentImage = gifFrames.get(currentFrame);

        DisplayUtils.drawImage(currentImage, x, y, width, height, -1);
    }
}