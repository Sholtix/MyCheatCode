package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.player.MoveUtils;

@FunctionRegister(name = "Jesus", type = Category.Movement)
public class Jesus extends Function {

    private final SliderSetting spedd12 = new SliderSetting("Скорость", 1, 0.5f, 1.5f, 0.1f);

    public Jesus() {
        addSettings( spedd12);
    }
    @Subscribe
    private void onUpdate(EventUpdate update) {
        if (mc.player.isInWater()) {
            float moveSpeed = 1f;
            moveSpeed /= 1;

            double moveX = mc.player.getForward().x * spedd12.get();
            double moveZ = mc.player.getForward().z * spedd12.get();
            mc.player.motion.y = 0;
            if (MoveUtils.isMoving()) {
                if (MoveUtils.getMotion() < 0.9f) {
                    mc.player.motion.x *= 0 * spedd12.get();
                    mc.player.motion.z *= 0 * spedd12.get();
                }
            }
        }
    }
}