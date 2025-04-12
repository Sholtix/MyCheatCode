
package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventMotion;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

@FunctionRegister(
        name = "SpeedMotion",
        type = Category.Movement
)

public class Crit extends Function {
    public SliderSetting sneakspeed = new SliderSetting("Скорость", 1.21f, 0.7f, 2.50f, 0.01f);

    public Crit() {
        this.sneakspeed = sneakspeed;
        addSettings( sneakspeed );
    }

    @Subscribe
    public void onPlayerUpdate(EventMotion event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.isAlive() && mc.player.isOnLadder()) {
            player.setMotion(player.getMotion().x * sneakspeed.get(), player.getMotion().y +0.08, player.getMotion().z * sneakspeed.get());
        }

    }
}