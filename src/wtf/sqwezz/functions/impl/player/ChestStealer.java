//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.StopWatch;

@FunctionRegister(
        name = "ChestStealer",
        type = Category.Player
)
public class ChestStealer extends Function {
    private final ModeSetting mode = new ModeSetting("Мод", "Дефолт", new String[]{"Дефолт"});
    private final BooleanSetting chestClose = new BooleanSetting("Закрывать при полном", true);
    private final SliderSetting stealDelay = new SliderSetting("Задержка", 75.0F, 0.0F, 300.0F, 1.0F);
    private final ModeSetting filterLoot = new ModeSetting("Лут", "Funtime", new String[]{"Funtime", "StormHVH"});
    private final StopWatch timerUtil = new StopWatch();

    public ChestStealer() {
        this.addSettings(new Setting[]{this.chestClose, this.stealDelay, this.filterLoot});
    }

    private boolean filterItem(Item item) {
        boolean Funtime = this.filterLoot.is("Funtime");
        boolean Storm = this.filterLoot.is("StormHVH");
        if ((!Funtime || item != Items.PHANTOM_MEMBRANE) && item != Items.GUNPOWDER && item != Items.NAUTILUS_SHELL && item != Items.GRAY_DYE) {
            return Storm && item == Items.PLAYER_HEAD || item == Items.TOTEM_OF_UNDYING || item == Items.POTION || item == Items.SPLASH_POTION || item == Items.NETHERITE_SWORD || item == Items.NETHERITE_INGOT || item == Items.ELYTRA || item == Items.POPPED_CHORUS_FRUIT || item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.NETHERITE_SCRAP || item == Items.TRIDENT || item == Items.CROSSBOW;
        } else {
            return true;
        }
    }

    @Subscribe
    public void onEvent(EventUpdate event) {
        if (this.mode.is("Дефолт")) {
            Minecraft var10000 = mc;
            if (Minecraft.player.openContainer instanceof ChestContainer) {
                var10000 = mc;
                ChestContainer container = (ChestContainer)Minecraft.player.openContainer;
                IInventory inventory = container.getLowerChestInventory();
                List<Integer> validSlots = new ArrayList();

                int randomIndex;
                for(randomIndex = 0; randomIndex < inventory.getSizeInventory(); ++randomIndex) {
                    if (inventory.getStackInSlot(randomIndex).getItem() != Item.getItemById(0) && inventory.getStackInSlot(randomIndex).getCount() <= 64 && this.filterItem(inventory.getStackInSlot(randomIndex).getItem())) {
                        validSlots.add(randomIndex);
                    }
                }

                if (!validSlots.isEmpty() && this.timerUtil.isReached((long)Math.round((Float)this.stealDelay.get()))) {
                    randomIndex = (new Random()).nextInt(validSlots.size());
                    int slotToSteal = (Integer)validSlots.get(randomIndex);
                    Minecraft var10005 = mc;
                    mc.playerController.windowClick(container.windowId, slotToSteal, 0, ClickType.QUICK_MOVE, Minecraft.player);
                    this.timerUtil.reset();
                }
            }
        }

    }
}
