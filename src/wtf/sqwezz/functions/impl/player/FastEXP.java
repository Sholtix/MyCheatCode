//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;

@FunctionRegister(
        name = "FastEXP",
        type = Category.Player
)
public class FastEXP extends Function {
    public FastEXP() {
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        this.fastEXP();
    }

    public void fastEXP() {
        Minecraft var10000 = mc;
        if (Minecraft.player != null) {
            var10000 = mc;
            ItemStack mainhandItem = Minecraft.player.getHeldItemMainhand();
            if (!mainhandItem.isEmpty() && mainhandItem.getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
                return;
            }

            var10000 = mc;
            ItemStack offhandItem = Minecraft.player.getHeldItemOffhand();
            if (!offhandItem.isEmpty() && offhandItem.getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
            }
        }

    }
}
