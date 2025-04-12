package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.utils.player.MoveUtils;

@FunctionRegister(name = "Parkour", type = Category.Movement)
public class Parkour extends Function {

    @Subscribe
    private void onUpdate(EventUpdate e) {

        if (MoveUtils.isBlockUnder(0.001f) && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

}
