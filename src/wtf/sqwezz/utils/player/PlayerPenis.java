//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.utils.player;

import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public final class PlayerPenis {
    static Minecraft mc = Minecraft.getInstance();
    private static final Pattern NAME_REGEX = Pattern.compile("^[A-zА-я0-9_]{3,16}$");

    public static boolean isNameValid(String name) {
        return NAME_REGEX.matcher(name).matches();
    }

    public static boolean isInHell() {
        Minecraft var10000 = mc;
        if (Minecraft.world == null) {
            return false;
        } else {
            var10000 = mc;
            return Objects.equals(Minecraft.world.getDimensionKey(), "the_nether");
        }
    }

    public static float calculateCorrectYawOffset(float yaw) {
        Minecraft var10000 = mc;
        double var9 = Minecraft.player.getPosX();
        Minecraft var10001 = mc;
        double xDiff = var9 - Minecraft.player.prevPosX;
        var10000 = mc;
        var9 = Minecraft.player.getPosZ();
        var10001 = mc;
        double zDiff = var9 - Minecraft.player.prevPosZ;
        float distSquared = (float)(xDiff * xDiff + zDiff * zDiff);
        var10000 = mc;
        float renderYawOffset = Minecraft.player.prevRenderYawOffset;
        float offset = renderYawOffset;
        if (distSquared > 0.0025000002F) {
            offset = (float)MathHelper.atan2(zDiff, xDiff) * 180.0F / 3.1415927F - 90.0F;
        }

        var10000 = mc;
        if (Minecraft.player != null) {
            var10000 = mc;
            if (Minecraft.player.swingProgress > 0.0F) {
                offset = yaw;
            }
        }

        float yawOffsetDiff = MathHelper.wrapDegrees(yaw - (renderYawOffset + MathHelper.wrapDegrees(offset - renderYawOffset) * 0.3F));
        yawOffsetDiff = MathHelper.clamp(yawOffsetDiff, -75.0F, 75.0F);
        renderYawOffset = yaw - yawOffsetDiff;
        if (yawOffsetDiff * yawOffsetDiff > 2500.0F) {
            renderYawOffset += yawOffsetDiff * 0.2F;
        }

        return renderYawOffset;
    }

    private PlayerPenis() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
