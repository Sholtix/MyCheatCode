//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.movement;

public class EventUpdate {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EventUpdate)) {
            return false;
        } else {
            EventUpdate other = (EventUpdate)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventUpdate;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "EventUpdate()";
    }

    public EventUpdate() {
    }
}
