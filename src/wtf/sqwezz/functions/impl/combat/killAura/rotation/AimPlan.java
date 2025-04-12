package wtf.sqwezz.functions.impl.combat.killAura.rotation;

public interface AimPlan {

    VecRotation getRotation(VecRotation targetRotation, VecRotation previousRotation);

    String getName();
}
