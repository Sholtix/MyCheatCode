package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.StopWatch;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

@FunctionRegister(
        name = "AutoExp",
        type = Category.Misc
)
public class AutoExp extends Function {
    private final SliderSetting throwDelay = new SliderSetting("Задержка между бросками", 40.0F, 5.0F, 1000.0F, 5.0F);
    private final StopWatch stopWatch = new StopWatch();

    public AutoExp() {
        this.addSettings(new Setting[]{this.throwDelay});
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        Minecraft var10000 = mc;
        if (Minecraft.player != null && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            var10000 = mc;
            ItemStack heldItem = Minecraft.player.getHeldItem(Hand.MAIN_HAND);
            if (heldItem.getItem() == Items.EXPERIENCE_BOTTLE && this.stopWatch.isReached((long)(this.throwDelay.get().floatValue()))) {
                var10000 = mc;
                Minecraft.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                this.stopWatch.reset();
            }
        }

    }
}
