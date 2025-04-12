//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.events;

public class EventMotion extends CancelEvent {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
    Runnable postMotion;

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Runnable getPostMotion() {
        return this.postMotion;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setPostMotion(Runnable postMotion) {
        this.postMotion = postMotion;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EventMotion)) {
            return false;
        } else {
            EventMotion other = (EventMotion)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (Double.compare(this.getX(), other.getX()) != 0) {
                return false;
            } else if (Double.compare(this.getY(), other.getY()) != 0) {
                return false;
            } else if (Double.compare(this.getZ(), other.getZ()) != 0) {
                return false;
            } else if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
                return false;
            } else if (Float.compare(this.getPitch(), other.getPitch()) != 0) {
                return false;
            } else if (this.isOnGround() != other.isOnGround()) {
                return false;
            } else {
                Object this$postMotion = this.getPostMotion();
                Object other$postMotion = other.getPostMotion();
                if (this$postMotion == null) {
                    if (other$postMotion != null) {
                        return false;
                    }
                } else if (!this$postMotion.equals(other$postMotion)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventMotion;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        long $x = Double.doubleToLongBits(this.getX());
        result = result * 59 + (int)($x >>> 32 ^ $x);
        long $y = Double.doubleToLongBits(this.getY());
        result = result * 59 + (int)($y >>> 32 ^ $y);
        long $z = Double.doubleToLongBits(this.getZ());
        result = result * 59 + (int)($z >>> 32 ^ $z);
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        result = result * 59 + (this.isOnGround() ? 79 : 97);
        Object $postMotion = this.getPostMotion();
        result = result * 59 + ($postMotion == null ? 43 : $postMotion.hashCode());
        return result;
    }

    public String toString() {
        double var10000 = this.getX();
        return "EventMotion(x=" + var10000 + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ", postMotion=" + this.getPostMotion() + ")";
    }

    public EventMotion(double x, double y, double z, float yaw, float pitch, boolean onGround, Runnable postMotion) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.postMotion = postMotion;
    }
}
