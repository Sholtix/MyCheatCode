//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.events;

public class EventInput {
    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private double sneakSlowDownMultiplier;

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public boolean isJump() {
        return this.jump;
    }

    public boolean isSneak() {
        return this.sneak;
    }

    public double getSneakSlowDownMultiplier() {
        return this.sneakSlowDownMultiplier;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public void setSneakSlowDownMultiplier(double sneakSlowDownMultiplier) {
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EventInput)) {
            return false;
        } else {
            EventInput other = (EventInput)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (Float.compare(this.getForward(), other.getForward()) != 0) {
                return false;
            } else if (Float.compare(this.getStrafe(), other.getStrafe()) != 0) {
                return false;
            } else if (this.isJump() != other.isJump()) {
                return false;
            } else if (this.isSneak() != other.isSneak()) {
                return false;
            } else {
                return Double.compare(this.getSneakSlowDownMultiplier(), other.getSneakSlowDownMultiplier()) == 0;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventInput;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getForward());
        result = result * 59 + Float.floatToIntBits(this.getStrafe());
        result = result * 59 + (this.isJump() ? 79 : 97);
        result = result * 59 + (this.isSneak() ? 79 : 97);
        long $sneakSlowDownMultiplier = Double.doubleToLongBits(this.getSneakSlowDownMultiplier());
        result = result * 59 + (int)($sneakSlowDownMultiplier >>> 32 ^ $sneakSlowDownMultiplier);
        return result;
    }

    public String toString() {
        float var10000 = this.getForward();
        return "EventInput(forward=" + var10000 + ", strafe=" + this.getStrafe() + ", jump=" + this.isJump() + ", sneak=" + this.isSneak() + ", sneakSlowDownMultiplier=" + this.getSneakSlowDownMultiplier() + ")";
    }

    public EventInput(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDownMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }
}
