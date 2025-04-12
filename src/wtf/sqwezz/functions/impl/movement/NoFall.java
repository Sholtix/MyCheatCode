package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

@FunctionRegister(
        name = "NoFall",
        type = Category.Movement
)
public class NoFall extends Function {

    public NoFall() {
    }

    @Subscribe
    public void onUpdate(EventUpdate var1) {

        if (Minecraft.player.fallDistance > 2.0F) {
            if (!Minecraft.player.isOnGround()) {
                Minecraft.player.motion.y = 0.0035000001080334187;

                Minecraft.player.rotationYaw = 0.0F;
            }
        }
    }

    protected float[] rotations(PlayerEntity var1) {
        return new float[0];
    }
}