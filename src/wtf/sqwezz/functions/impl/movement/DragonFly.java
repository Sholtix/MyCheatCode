//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.player.MoveUtils;

@FunctionRegister(
        name = "DragonFly",
        type = Category.Movement
)
public class DragonFly extends Function {
    private final BooleanSetting fly = new BooleanSetting("Включить", true);
    private final SliderSetting speed = new SliderSetting("Скорость", 1.15F, 0.0F, 5F, 0.05F);

    public DragonFly() {
        this.addSettings(new Setting[]{this.fly, this.speed});
    }

    @Subscribe
    public void onUpdate(EventUpdate var1) {
        if (var1 instanceof EventUpdate) {
            this.dragonFly();
        }

    }

    private void dragonFly() {
        String var10001 = "儳枍";
        String var10002 = "泃朩";
        var10001 = "儥惵";
        var10001 = "溽嶉";
        String var10000 = "撬挷";
        var10001 = "枕亷";
        var10002 = "尵忚";
        var10001 = "儻奧";
        if ((Boolean)this.fly.get() && mc.player.abilities.isFlying) {
            mc.player.motion.y = 0.0;
            ClientPlayerEntity var1 = mc.player;
            Vector3d var2;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                var2 = var1.motion;
                "匀敼昿毛".length();
                "奄".length();
                "椿懍昩".length();
                ++var2.y;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                var2 = var1.motion;
                "勀囈坿".length();
                double var4 = var2.y;
                "兒".length();
                var2.y = var4 - 1.15;
            }

            if (MoveUtils.isMoving()) {
                float var3 = (Float)this.speed.get();
                "桄壝".length();
                "喓斷".length();
                MoveUtils.setMotion((double)(var3 - 0.2F));
            }
        }

    }
}
