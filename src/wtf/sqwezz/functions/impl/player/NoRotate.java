package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.CPlayerPacket;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;

@FunctionRegister(name = "NoRotate", type = Category.Player)
public class NoRotate extends Function {
    private float targetYaw;
    private float targetPitch;
    private boolean isPacketSent;

    @Subscribe
    public void onPacket(EventPacket event) {
        if (event.isSend()) {
            if (this.isPacketSent) {
                if (event.getPacket() instanceof CPlayerPacket playerPacket) {
                    playerPacket.setRotation(targetYaw, targetPitch);
                    this.isPacketSent = false;
                }
            }
        }
    }

    public void sendRotationPacket(float yaw, float pitch) {
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.isPacketSent = true;
    }
}
