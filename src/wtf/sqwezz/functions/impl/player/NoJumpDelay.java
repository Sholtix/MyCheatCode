package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;

@FunctionRegister(name = "NoJumpDelay", type = Category.Player)
public class NoJumpDelay extends Function {
    @Subscribe
    public void onUpdate(EventUpdate e) {
        mc.player.jumpTicks = 0;
    }
}
