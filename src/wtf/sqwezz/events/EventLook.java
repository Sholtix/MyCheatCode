package wtf.sqwezz.events;

import wtf.sqwezz.functions.impl.combat.killAura.rotation.VecRotation;
import wtf.sqwezz.utils.combat.Rotation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class EventLook {
    private VecRotation rotation;

    public VecRotation getRotation() {
        return this.rotation;
    }

    public void setRotation(VecRotation rotation) {
        this.rotation = rotation;
    }

    public EventLook(VecRotation rotation) {
        this.rotation = rotation;
    }
}