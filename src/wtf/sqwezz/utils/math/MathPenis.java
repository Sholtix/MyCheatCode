//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.client.Vec2i;

import static wtf.sqwezz.utils.math.MathUtil.random;

public final class MathPenis implements IMinecraft {
    private static final int SCALE = 2;

    public static <T extends Number> int lerp(T input, T target, double step) {
        double start = input.doubleValue();
        double end = target.doubleValue();
        double result = start + step * (end - start);
        if (input instanceof Integer) {
            return (int)Math.round(result);
        } else if (input instanceof Double) {
            return (int) result;
        } else if (input instanceof Float) {
            return (int) result;
        } else if (input instanceof Long) {
            return (int) Math.round(result);
        } else if (input instanceof Short) {
            return (short)((int)Math.round(result));
        } else if (input instanceof Byte) {
            return (byte)((int)Math.round(result));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + input.getClass().getSimpleName());
        }
    }

    public static int hpResolver(LivingEntity entity, Scoreboard scoreboard) {
        return scoreboard.getObjectiveNames().size();
    }

    private static double armor(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return 0.0;
        } else {
            Item var2 = stack.getItem();
            if (var2 instanceof ArmorItem) {
                ArmorItem armor = (ArmorItem)var2;
                return (double)armor.getDamageReduceAmount() + (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
            } else {
                return 0.0;
            }
        }
    }

    public static double armor(LivingEntity entity) {
        double armor = (double)entity.getTotalArmorValue();

        ItemStack item;
        for(Iterator var3 = entity.getArmorInventoryList().iterator(); var3.hasNext(); armor += armor(item)) {
            item = (ItemStack)var3.next();
        }

        return armor;
    }

    public static double health(LivingEntity entity) {
        return (double)(entity.getHealth() + entity.getAbsorptionAmount());
    }

    public static double buffs(LivingEntity entity) {
        double buffs = 0.0;
        Iterator var3 = entity.getActivePotionEffects().iterator();

        while(var3.hasNext()) {
            EffectInstance effect = (EffectInstance)var3.next();
            if (effect.getPotion() == Effects.ABSORPTION) {
                buffs += 1.2 * (double)(effect.getAmplifier() + 1);
            } else if (effect.getPotion() == Effects.RESISTANCE) {
                buffs += 1.0 * (double)(effect.getAmplifier() + 1);
            } else if (effect.getPotion() == Effects.REGENERATION) {
                buffs += 1.1 * (double)(effect.getAmplifier() + 1);
            }
        }

        return buffs;
    }

    public static double entity(LivingEntity entity, boolean health, boolean armor, boolean distance, double maxDistance, boolean buffs) {
        double a = 1.0;
        double b = 1.0;
        double c = 1.0;
        double d = 1.0;
        if (health) {
            a += health(entity);
        }

        if (armor) {
            b += armor(entity);
        }

        if (distance) {
            Minecraft.getInstance();
            c += entity.getDistanceSq(Minecraft.player) / maxDistance;
        }

        if (buffs) {
            d += buffs(entity);
        }

        return a * b * d * c;
    }

    public static Vector3d interpolatePos(float oldx, float oldy, float oldz, float x, float y, float z) {
        double getx = (double)(oldx + (x - oldx) * mc.getRenderPartialTicks()) - mc.getRenderManager().info.getProjectedView().getX();
        double gety = (double)(oldy + (y - oldy) * mc.getRenderPartialTicks()) - mc.getRenderManager().info.getProjectedView().getY();
        double getz = (double)(oldz + (z - oldz) * mc.getRenderPartialTicks()) - mc.getRenderManager().info.getProjectedView().getZ();
        return new Vector3d(getx, gety, getz);
    }

    public static double randomWithUpdate(double min, double max, long ms, StopWatch stopWatch) {
        double randomValue = 0.0;
        if (stopWatch.isReached(ms)) {
            randomValue = (double)random((float)min, (float)max);
            stopWatch.reset();
        }

        return randomValue;
    }

    public static Vector3d getVector(LivingEntity target) {
        double wHalf = (double)(target.getWidth() / 2.0F);
        double yExpand = MathHelper.clamp(target.getPosYEye() - target.getPosY(), 0.0, (double)target.getHeight());
        Minecraft var10000 = mc;
        double xExpand = MathHelper.clamp(Minecraft.player.getPosX() - target.getPosX(), -wHalf, wHalf);
        var10000 = mc;
        double zExpand = MathHelper.clamp(Minecraft.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);
        double var10002 = target.getPosX();
        Minecraft var10003 = mc;
        var10002 = var10002 - Minecraft.player.getPosX() + xExpand;
        double var9 = target.getPosY();
        Minecraft var10004 = mc;
        var9 = var9 - Minecraft.player.getPosYEye() + yExpand;
        double var10 = target.getPosZ();
        Minecraft var10005 = mc;
        return new Vector3d(var10002, var9, var10 - Minecraft.player.getPosZ() + zExpand);
    }

    public static float calculateDelta(float a, float b) {
        return a - b;
    }

    public static float random(float min, float max) {
        return (float)(Math.random() * (double)(max - min) + (double)min);
    }


    public class Vector2d {
        public double x;
        public double y;
        public Vector2d(double x, double y) {
            this.x = x;
            this.y = y;
        }
    public Vector2d getMouse(double mouseX, double mouseY) {
        return new Vector2d(mouseX * mc.getMainWindow().getGuiScaleFactor() / 2.0, mouseY * mc.getMainWindow().getGuiScaleFactor() / 2.0);
    }

    public static Vec2i getMouse2i(int mouseX, int mouseY) {
        return new Vec2i((int)((double)mouseX * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0), (int)((double)mouseY * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0));
    }

        public static Vector3d getVector(LivingEntity target) {
            double wHalf = (double)(target.getWidth() / 2.0F);
            double yExpand = MathHelper.clamp(target.getPosYEye() - target.getPosY(), 0.0, (double)target.getHeight());
            Minecraft var10000 = mc;
            double xExpand = MathHelper.clamp(Minecraft.player.getPosX() - target.getPosX(), -wHalf, wHalf);
            var10000 = mc;
            double zExpand = MathHelper.clamp(Minecraft.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);
            double var10002 = target.getPosX();
            Minecraft var10003 = mc;
            var10002 = var10002 - Minecraft.player.getPosX() + xExpand;
            double var9 = target.getPosY();
            Minecraft var10004 = mc;
            var9 = var9 - Minecraft.player.getPosYEye() + yExpand;
            double var10 = target.getPosZ();
            Minecraft var10005 = mc;
            return new Vector3d(var10002, var9, var10 - Minecraft.player.getPosZ() + zExpand);
        }

    public static int randomInt(int min, int max) {
        if (min >= max) {
            System.out.println("Ну ты и дцп...");
            return -1;
        } else {
            Random random = new Random();
            return random.nextInt(max - min + 1) + min;
        }
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public static int calculatePing() {
        Minecraft var10000 = mc;
        Minecraft var10001 = mc;
        int var0;
        if (Minecraft.player.connection.getPlayerInfo(Minecraft.player.getUniqueID()) != null) {
            var10000 = mc;
            var10001 = mc;
            var0 = Minecraft.player.connection.getPlayerInfo(Minecraft.player.getUniqueID()).getResponseTime();
        } else {
            var0 = 0;
        }

        return var0;
    }

    public static float random(float min, float max) {
        return (float)(Math.random() * (double)(max - min) + (double)min);
    }

    public static double randomWithUpdate(double min, double max, long ms, StopWatch stopWatch) {
        double randomValue = 0.0;
        if (stopWatch.isReached(ms)) {
            randomValue = (double)random((float)min, (float)max);
            stopWatch.reset();
        }

        return randomValue;
    }

    public static Vector2f rotationToVec(Vector3d vec) {
        Minecraft var10000 = mc;
        Vector3d eyesPos = Minecraft.player.getEyePosition(1.0F);
        double diffX = vec != null ? vec.x - eyesPos.x : 0.0;
        double var12;
        Minecraft var10002;
        if (vec != null) {
            var12 = vec.y;
            Minecraft var10001 = mc;
            double var13 = Minecraft.player.getPosY();
            var10002 = mc;
            var12 -= var13 + (double)Minecraft.player.getEyeHeight() + 0.5;
        } else {
            var12 = 0.0;
        }

        double diffY = var12;
        double diffZ = vec != null ? vec.z - eyesPos.z : 0.0;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        var10000 = mc;
        var10002 = mc;
        yaw = Minecraft.player.rotationYaw + MathHelper.wrapDegrees(yaw - Minecraft.player.rotationYaw);
        var10000 = mc;
        var10002 = mc;
        pitch = Minecraft.player.rotationPitch + MathHelper.wrapDegrees(pitch - Minecraft.player.rotationPitch);
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f rotationToEntity(Entity target) {
        Vector3d var10000 = target.getPositionVec();
        Minecraft.getInstance();
        Vector3d vector3d = var10000.subtract(Minecraft.player.getPositionVec());
        double magnitude = Math.hypot(vector3d.x, vector3d.z);
        return new Vector2f((float)Math.toDegrees(Math.atan2(vector3d.z, vector3d.x)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(vector3d.y, magnitude))));
    }

    public static Vector2f rotationToVec(Vector2f rotationVector, Vector3d target) {
        Minecraft var10001 = mc;
        double x = target.x - Minecraft.player.getPosX();
        var10001 = mc;
        double y = target.y - Minecraft.player.getEyePosition(1.0F).y;
        var10001 = mc;
        double z = target.z - Minecraft.player.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yaw - rotationVector.x);
        float pitchDelta = pitch - rotationVector.y;
        if (Math.abs(yawDelta) > 180.0F) {
            yawDelta -= Math.signum(yawDelta) * 360.0F;
        }

        return new Vector2f(yawDelta, pitchDelta);
    }

    public static double round(double num, double increment) {
        double v = (double)Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt(x * x + y * y);
    }

    public static double deltaTime() {
        Minecraft var10000 = mc;
        double var0;
        if (Minecraft.debugFPS > 0) {
            Minecraft var10001 = mc;
            var0 = 1.0 / (double)Minecraft.debugFPS;
        } else {
            var0 = 1.0;
        }

        return var0;
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0F - MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F)) * end + MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F) * start;
    }

    public static Vector3d interpolate(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(interpolate(end.getX(), start.getX(), (double)multiple), interpolate(end.getY(), start.getY(), (double)multiple), interpolate(end.getZ(), start.getZ(), (double)multiple));
    }

    public static Vector2f interpolate(Vector2f end, Vector2f start, float multiple) {
        return new Vector2f((float)interpolate((double)end.x, (double)start.x, (double)multiple), (float)interpolate((double)end.y, (double)start.y, (double)multiple));
    }

    public static Vector3d fast(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d((double)fast((float)end.getX(), (float)start.getX(), multiple), (double)fast((float)end.getY(), (float)start.getY(), multiple), (double)fast((float)end.getZ(), (float)start.getZ(), multiple));
    }

    public static Vector2f fast(Vector2f end, Vector2f start, float multiple) {
        return new Vector2f(fast(end.x, start.x, multiple), fast(end.y, start.y, multiple));
    }

    public static float lerp(float end, float start, float multiple) {
        return (float)((double)end + (double)(start - end) * MathHelper.clamp(deltaTime() * (double)multiple, 0.0, 1.0));
    }

    public static double lerp(double end, double start, double multiple) {
        return end + (start - end) * MathHelper.clamp(deltaTime() * multiple, 0.0, 1.0);
    }

    public static float calculateDelta(float a, float b) {
        return a - b;
    }

}
}
