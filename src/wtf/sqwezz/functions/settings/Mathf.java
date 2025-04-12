//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.settings;

import wtf.sqwezz.functions.settings.Interpolator;
import wtf.sqwezz.utils.client.IMinecraft;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class Mathf {
    public Mathf() {
    }

    public static float clamp01(float x) {
        return (float)clamp(0.0, 1.0, (double)x);
    }

    public double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else {
            if (min > max) {
                double d = min;
                min = max;
                max = d;
            }

            return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
        }
    }

    public static float calculateDelta(float a, float b) {
        return a - b;
    }

    public static Vector3d getVector(LivingEntity target) {
        double wHalf = (double)(target.getWidth() / 2.0F);
        double yExpand = clamp(target.getPosYEye() - target.getPosY(), 0.0, (double)target.getHeight());
        double xExpand = clamp(IMinecraft.mc.player.getPosX() - target.getPosX(), -wHalf, wHalf);
        double zExpand = clamp(IMinecraft.mc.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);
        return new Vector3d(target.getPosX() - IMinecraft.mc.player.getPosX() + xExpand, target.getPosY() - IMinecraft.mc.player.getPosYEye() + yExpand, target.getPosZ() - IMinecraft.mc.player.getPosZ() + zExpand);
    }

    public double round(double target, int decimal) {
        double p = Math.pow(10.0, (double)decimal);
        return (double)Math.round(target * p) / p;
    }

    public Number round(double num, double increment) {
        if (increment <= 0.0) {
            throw new IllegalArgumentException("Increment must be greater than zero");
        } else {
            double roundedValue = (double)Math.round(num / increment) * increment;
            BigDecimal bigDecimal = BigDecimal.valueOf(roundedValue);
            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
            return bigDecimal.doubleValue();
        }
    }

    public String formatTime(long millis) {
        long hours = millis / 3600000L;
        long minutes = millis % 3600000L / 60000L;
        long seconds = millis % 360000L % 60000L / 1000L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public float slerp(float start, float end, float t) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        float startRadians = (float)Math.toRadians((double)start);
        float endRadians = (float)Math.toRadians((double)end);
        float dotProduct = (float)Math.cos((double)startRadians) * (float)Math.cos((double)endRadians) + (float)Math.sin((double)startRadians) * (float)Math.sin((double)endRadians);
        float angle = (float)Math.acos((double)dotProduct);
        if (Math.abs(angle) < 0.001F) {
            return start;
        } else {
            float factorStart = (float)(Math.sin((double)((1.0F - t) * angle)) / Math.sin((double)angle));
            float factorEnd = (float)(Math.sin((double)(t * angle)) / Math.sin((double)angle));
            float interpolatedValue = start * factorStart + end * factorEnd;
            return (float)MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees((double)interpolatedValue)), (double)start, (double)end);
        }
    }

    public double round(double value, int scale, double inc) {
        double halfOfInc = inc / 2.0;
        double floored = Math.floor(value / inc) * inc;
        return value >= floored + halfOfInc ? (new BigDecimal(Math.ceil(value / inc) * inc)).setScale(scale, RoundingMode.HALF_UP).doubleValue() : (new BigDecimal(floored)).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public double step(double value, double steps) {
        double a = (double)Math.round(value / steps) * steps;
        a *= 1000.0;
        a = (double)((int)a);
        a /= 1000.0;
        return a;
    }

    public double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double deltaZ = z2 - z1;
        return (double)MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public static double clamp(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }

    public static float limit(float current, float inputMin, float inputMax, float outputMin, float outputMax) {
        current = (float)clamp((double)inputMin, (double)inputMax, (double)current);
        float distancePercentage = (current - inputMin) / (inputMax - inputMin);
        Interpolator interpolator = new Interpolator();
        return interpolator.lerp(outputMin, outputMax, (double)distancePercentage);
    }

    public float normalize(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static double interporate(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
        return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
    }

    public float lerp(float min, float max, float delta) {
        return min + (max - min) * delta;
    }

    public float easeOutExpo(float x) {
        return x == 1.0F ? 1.0F : (float)(1.0 - Math.pow(2.0, (double)(-10.0F * x)));
    }
}
