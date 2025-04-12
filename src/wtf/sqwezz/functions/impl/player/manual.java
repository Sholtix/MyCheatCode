

package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;

@FunctionRegister(
        name = "SpamModer",
        type = Category.Player
)
public class manual extends Function {
    private final BooleanSetting speeds = new BooleanSetting("Начать спам", false);

    public manual() {
        this.addSettings(new Setting[]{this.speeds});
    }

    @Subscribe
    public void manualone(EventDisplay var1) {
        String var10002 = "榿峤";
        String var10001 = "焜浓";
        String var10000 = "怒喲";
        var10001 = "伄桅";
        ClientPlayerEntity var2 = Minecraft.getInstance().player;
        if (var2 != null && var2.isAlive() && (Boolean)this.speeds.get()) {
            "拆槩怽儲得".length();
            Random var3 = new Random();
            int var4 = var3.nextInt(1, 7);
            if (var4 == 1) {
                mc.player.sendChatMessage("Crysense бустит шлюхи");
            }
            }
        }



    public boolean onEnable() {
        super.onEnable();
        this.print("Пиздец спамит, может кикнуть за спам");
        return false;
    }

    public void onDisable() {
        super.onDisable();
        this.print("Выебал >_0");
    }
}
