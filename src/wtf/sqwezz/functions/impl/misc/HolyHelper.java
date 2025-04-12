//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import wtf.sqwezz.events.EventKey;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BindSetting;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.player.InventoryUtil;

@FunctionRegister(
        name = "FTHelper",
        type = Category.Misc
)
public class HolyHelper extends Function {
    private final BindSetting disorientationKey = new BindSetting("Кнопка дезорентации", -1);
    private final BindSetting shulkerKey = new BindSetting("Кнопка шалкера", -1);
    private final BindSetting trapKey = new BindSetting("Кнопка трапки", -1);
    private final BindSetting flameKey = new BindSetting("Кнопка смерча", -1);
    private final BindSetting blatantKey = new BindSetting("Кнопка явной пыли", -1);
    private final BindSetting bowKey = new BindSetting("Кнопка арбалета", -1);
    private final BindSetting otrigaKey = new BindSetting("Кнопка отрыжки", -1);
    private final BindSetting serkaKey = new BindSetting("Кнопка серки", -1);
    private final BindSetting plastKey = new BindSetting("Кнопка пласта", -1);
    private final BindSetting godAuraKey = new BindSetting("Кнопка Божьи Ауры", -1);
    final StopWatch stopWatch = new StopWatch();
    InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    long delay;
    boolean disorientationThrow;
    boolean trapThrow;
    boolean flameThrow;
    boolean blatantThrow;
    boolean serkaThrow;
    boolean otrigaThrow;
    boolean bowThrow;
    boolean plastThrow;
    boolean shulkerThrow;
    boolean godAuraThrow;

    public HolyHelper() {
        this.addSettings(new Setting[]{this.disorientationKey, this.trapKey, this.flameKey, this.blatantKey, this.serkaKey, this.bowKey, this.otrigaKey, this.plastKey, this.shulkerKey, this.godAuraKey});
    }

    @Subscribe
    private void onKey(EventKey e) {
        if (e.getKey() == (Integer)this.disorientationKey.get()) {
            this.disorientationThrow = true;
        }

        if (e.getKey() == (Integer)this.shulkerKey.get()) {
            this.shulkerThrow = true;
        }

        if (e.getKey() == (Integer)this.trapKey.get()) {
            this.trapThrow = true;
        }

        if (e.getKey() == (Integer)this.flameKey.get()) {
            this.flameThrow = true;
        }

        if (e.getKey() == (Integer)this.blatantKey.get()) {
            this.blatantThrow = true;
        }

        if (e.getKey() == (Integer)this.otrigaKey.get()) {
            this.otrigaThrow = true;
        }

        if (e.getKey() == (Integer)this.serkaKey.get()) {
            this.serkaThrow = true;
        }

        if (e.getKey() == (Integer)this.bowKey.get()) {
            this.bowThrow = true;
        }

        if (e.getKey() == (Integer)this.plastKey.get()) {
            this.plastThrow = true;
        }

        if (e.getKey() == (Integer)this.godAuraKey.get()) {
            this.godAuraThrow = true;
        }

    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        Minecraft var10000;
        int hbSlot;
        int invSlot;
        int old;
        if (this.disorientationThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForName("дезориентация", true);
            invSlot = this.getItemForName("дезориентация", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Дезориентация не найдена!");
                this.disorientationThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                this.print("Заюзал дезориентацию!");
                old = this.findAndTrowItem(hbSlot, invSlot);
                if (old > 8) {
                    mc.playerController.pickItem(old);
                }
            }

            this.disorientationThrow = false;
        }

        int slot;
        if (this.shulkerThrow) {
            hbSlot = this.getItem(Items.SHULKER_BOX, true);
            invSlot = this.getItem(Items.SHULKER_BOX, false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Шалкер не найден");
                this.trapThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.SHULKER_BOX)) {
                this.print("Заюзал шалкер!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.shulkerThrow = false;
        }

        if (this.trapThrow) {
            hbSlot = this.getItemForName("трапка", true);
            invSlot = this.getItemForName("трапка", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Трапка не найдена");
                this.trapThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                this.print("Заюзал трапку!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.trapThrow = false;
        }

        if (this.flameThrow) {
            hbSlot = this.getItemForName("огненный", true);
            invSlot = this.getItemForName("огненный", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Огненный смерч не найден");
                this.flameThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.FIRE_CHARGE)) {
                this.print("Заюзал огненный смерч!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.flameThrow = false;
        }

        if (this.bowThrow) {
            hbSlot = this.getItem(Items.CROSSBOW, true);
            invSlot = this.getItem(Items.CROSSBOW, false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Арбалет не найден");
                this.bowThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.CROSSBOW)) {
                this.print("Заюзал арбалет!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.bowThrow = false;
        }

        if (this.serkaThrow) {
            hbSlot = this.getItemForName("серная", true);
            invSlot = this.getItemForName("серная", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Серка не найдена");
                this.serkaThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                this.print("Заюзал серку!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.serkaThrow = false;
        }

        if (this.otrigaThrow) {
            hbSlot = this.getItemForName("отрыжки", true);
            invSlot = this.getItemForName("отрыжки", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Отрыга не найдена");
                this.otrigaThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                this.print("Заюзал отрыгу!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.otrigaThrow = false;
        }

        if (this.plastThrow) {
            hbSlot = this.getItemForName("пласт", true);
            invSlot = this.getItemForName("пласт", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Пласт не найден");
                this.plastThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                this.print("Заюзал пласт!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.plastThrow = false;
        }

        if (this.blatantThrow) {
            hbSlot = this.getItemForName("явная", true);
            invSlot = this.getItemForName("явная", false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Явная пыль не найдена");
                this.blatantThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.TNT)) {
                this.print("Заюзал явную пыль!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.blatantThrow = false;
        }

        if (this.godAuraThrow) {
            hbSlot = this.getItem(Items.PHANTOM_MEMBRANE, true);
            invSlot = this.getItem(Items.PHANTOM_MEMBRANE, false);
            if (invSlot == -1 && hbSlot == -1) {
                this.print("Божья аура не найдена!");
                this.godAuraThrow = false;
                return;
            }

            var10000 = mc;
            if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.PHANTOM_MEMBRANE)) {
                this.print("Заюзал божью ауру!");
                var10000 = mc;
                old = Minecraft.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }

                if (InventoryUtil.findEmptySlot(true) != -1) {
                    var10000 = mc;
                    if (Minecraft.player.inventory.currentItem != old) {
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = old;
                    }
                }
            }

            this.godAuraThrow = false;
        }

        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        Minecraft var10000;
        Minecraft var10001;
        if (hbSlot != -1) {
            var10001 = mc;
            this.handUtil.setOriginalSlot(Minecraft.player.inventory.currentItem);
            var10000 = mc;
            Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            var10000 = mc;
            Minecraft.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            var10000 = mc;
            Minecraft.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        } else if (invSlot != -1) {
            var10001 = mc;
            this.handUtil.setOriginalSlot(Minecraft.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            var10000 = mc;
            Minecraft.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            var10000 = mc;
            Minecraft.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        } else {
            return -1;
        }
    }

    public void onDisable() {
        this.disorientationThrow = false;
        this.trapThrow = false;
        this.flameThrow = false;
        this.blatantThrow = false;
        this.plastThrow = false;
        this.otrigaThrow = false;
        this.serkaThrow = false;
        this.bowThrow = false;
        this.godAuraThrow = false;
        this.delay = 0L;
        super.onDisable();
    }

    private int getItemForName(String name, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;

        for(int i = firstSlot; i < lastSlot; ++i) {
            Minecraft var10000 = mc;
            ItemStack itemStack = Minecraft.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof AirItem)) {
                String displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString());
                if (displayName != null && displayName.toLowerCase().contains(name)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private int getItem(Item input, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;

        for(int i = firstSlot; i < lastSlot; ++i) {
            Minecraft var10000 = mc;
            ItemStack itemStack = Minecraft.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof AirItem) && itemStack.getItem() == input) {
                return i;
            }
        }

        return -1;
    }
}
