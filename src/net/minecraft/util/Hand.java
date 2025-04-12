package net.minecraft.util;

import net.minecraft.entity.Entity;

public enum Hand
{
    MAIN_HAND,
    OFF_HAND;

    private Entity entityHit;

    public void setEntityHit(Entity entityHit) {
        this.entityHit = entityHit;
    }

    public Entity getEntityHit() {
        return entityHit;
    }
}
