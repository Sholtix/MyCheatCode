package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;

import java.util.Iterator;

@FunctionRegister(name = "ItemElytraStealer",type = Category.Movement)
public class ElytraStealer extends Function {
    @Subscribe
    public boolean onEvent(EventUpdate event) {
        boolean skullItemNoNull = false;
        boolean eggItemNoNull = false;
        boolean elytraItemNoNull = false;
        boolean armorItemNoNull = false;
        boolean potionItemNoNull = false;
        Iterator var5;
        Entity entity;
        if (event instanceof EventUpdate) {
            var5 = mc.world.getAllEntities().iterator();

            while(var5.hasNext()) {
                entity = (Entity)var5.next();
                if (entity instanceof ItemEntity) {
                    if (((ItemEntity)entity).getItem().getItem() instanceof SkullItem) {
                        skullItemNoNull = true;
                    }
                    if (((ItemEntity)entity).getItem().getItem() instanceof PotionItem) {
                        potionItemNoNull = true;
                    }
                    if (((ItemEntity)entity).getItem().getItem() instanceof ArmorItem){
                        armorItemNoNull = true;
                    }
                    if (((ItemEntity)entity).getItem().getItem() instanceof ElytraItem) {
                        elytraItemNoNull = true;
                    }

                    if (((ItemEntity)entity).getItem().getItem() instanceof SpawnEggItem) {
                        eggItemNoNull = true;
                    }
                }
            }
        }

        if (event instanceof EventUpdate) {
            var5 = mc.world.getAllEntities().iterator();

            while(var5.hasNext()) {
                entity = (Entity)var5.next();
                if (entity instanceof ItemEntity) {
                    if (((ItemEntity)entity).getItem().getItem() instanceof SkullItem) {
                        mc.player.rotationYaw = this.rotations(entity)[0];
                        mc.player.rotationPitch = this.rotations(entity)[1];
                        break;
                    }

                    if (((ItemEntity)entity).getItem().getItem() instanceof ElytraItem && !skullItemNoNull) {
                        mc.player.rotationYaw = this.rotations(entity)[0];
                        mc.player.rotationPitch = this.rotations(entity)[1];
                        break;
                    }

                    if (((ItemEntity)entity).getItem().getItem() instanceof SpawnEggItem && !elytraItemNoNull && !skullItemNoNull) {
                        mc.player.rotationYaw = this.rotations(entity)[0];
                        mc.player.rotationPitch = this.rotations(entity)[1];
                        break;

                    }
                    if (((ItemEntity)entity).getItem().getItem() instanceof ElytraItem && !armorItemNoNull) {
                        mc.player.rotationYaw = this.rotations(entity)[0];
                        mc.player.rotationPitch = this.rotations(entity)[1];
                        break;
                    }

                }
            }
            //totem = true;
            skullItemNoNull = true;
            elytraItemNoNull = true;
            eggItemNoNull = true;
            armorItemNoNull = true;
        }

        return skullItemNoNull;
    }

    public float[] rotations(Entity entity) {
        double x = entity.getPosX() - mc.player.getPosX();
        double y = entity.getPosY() - mc.player.getPosY() - 1.5;
        double z = entity.getPosZ() - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
}