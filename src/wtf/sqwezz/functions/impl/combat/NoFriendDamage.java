
package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.command.friends.FriendStorage;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.client.CUseEntityPacket.Action;

@FunctionRegister(
        name = "NoFriendDamage",
        type = Category.Combat
)
public class NoFriendDamage extends Function {
    public NoFriendDamage() {
    }

    @Subscribe
    public void onEvent(EventPacket event) {
        if (event.getPacket() instanceof CUseEntityPacket) {
            CUseEntityPacket cUseEntityPacket = (CUseEntityPacket)event.getPacket();
            Minecraft var10001 = mc;
            Entity entity = cUseEntityPacket.getEntityFromWorld(Minecraft.world);
            if (entity instanceof RemoteClientPlayerEntity && FriendStorage.isFriend(entity.getName().getString()) && cUseEntityPacket.getAction() == Action.ATTACK) {
                event.cancel();
            }
        }

    }
}
