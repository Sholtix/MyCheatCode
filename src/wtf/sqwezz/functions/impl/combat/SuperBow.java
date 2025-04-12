package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import com.jagrosh.discordipc.entities.Packet;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventMotion;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.eventbus.api.Event;

@FunctionRegister(name = "Superbow", type = Category.Player)
public class SuperBow extends Function {

    private final SliderSetting power = new SliderSetting("Сила", 30, 1, 100, 1);

    public SuperBow() {
        addSettings(power);
    }

    @Subscribe
    public void onEvent(EventPacket event) {
        if (mc.player == null || mc.world == null) return;

            if (event.getPacket() instanceof CPlayerDiggingPacket p) {
                if (p.getAction() == CPlayerDiggingPacket.Action.RELEASE_USE_ITEM) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                    for (int i = 0; i < power.get().intValue(); i++) {
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 0.000000001, mc.player.getPosZ(), true));
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.000000001, mc.player.getPosZ(), false));
                    }
            }
        }
    }
}
