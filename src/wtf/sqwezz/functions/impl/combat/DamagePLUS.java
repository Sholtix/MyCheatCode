package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import io.netty.util.ResourceLeakDetector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;


@FunctionRegister(name = "Damage+", type = Category.Combat)
public class DamagePLUS extends Function {

    private static boolean effectActive = false;

    @Subscribe
    public void onUpdate(EventUpdate e) {
            PlayerEntity player = mc.player;
            if (player.isOnGround() ) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 300, 5, false, false));
            }

    }
}