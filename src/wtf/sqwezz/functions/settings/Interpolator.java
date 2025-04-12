//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.settings;

public class Interpolator {
    public static final Interpolator INSTANCE = new Interpolator();

    public Interpolator() {
    }

    public <T extends Number> int lerp(T input, T target, double step) {
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
}
