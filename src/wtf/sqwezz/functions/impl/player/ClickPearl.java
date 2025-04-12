package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventKey;
import wtf.sqwezz.events.EventMotion;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BindSetting;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.player.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

@FunctionRegister(
        name = "ClickPearl",
        type = Category.Player
)
public class ClickPearl extends Function {
    private final BindSetting throwKey = new BindSetting("Кнопка", -98);
    private final StopWatch stopWatch = new StopWatch();
    private final InventoryUtil.Hand handUtil = new InventoryUtil.Hand();
    private final ItemCooldown itemCooldown;
    private long delay;
    private boolean throwPearl;
    private final long pearlThrowDelay = 250L;
    private final long returnDelay = 50L;
    private long lastPearlThrowTime = 0L;
    private int originalSlot = -1;
    private int pearlSlot = -1;

    public ClickPearl(ItemCooldown itemCooldown) {
        this.itemCooldown = itemCooldown;
        this.addSettings(new Setting[]{this.throwKey});
    }

    @Subscribe
    public void onKey(EventKey e) {
        this.throwPearl = e.getKey() == (Integer)this.throwKey.get();
    }

    @Subscribe
    private void onMotion(EventMotion e) {
        if (this.throwPearl) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastPearlThrowTime >= 250L) {
                Minecraft var10000 = mc;
                if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL)) {
                    this.findPearl();
                    if (this.pearlSlot != -1) {
                        Minecraft var10001 = mc;
                        this.originalSlot = Minecraft.player.inventory.currentItem;
                        var10000 = mc;
                        Minecraft.player.inventory.currentItem = this.pearlSlot;
                        mc.playerController.updateController();
                        var10000 = mc;
                        Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(this.pearlSlot));
                        var10000 = mc;
                        Minecraft.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        var10000 = mc;
                        Minecraft.player.swingArm(Hand.MAIN_HAND);
                        this.delay = System.currentTimeMillis() + 50L;
                    }
                }

                this.throwPearl = false;
            }
        }

    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.delay > 0L && System.currentTimeMillis() >= this.delay) {
            Minecraft var10000 = mc;
            Minecraft.player.inventory.currentItem = this.originalSlot;
            mc.playerController.updateController();
            var10000 = mc;
            Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(this.originalSlot));
            this.delay = -1L;
        }

        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        this.handUtil.onEventPacket(e);
    }

    private void findPearl() {
        this.pearlSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(Items.ENDER_PEARL, true);
    }

    public void onDisable() {
        this.throwPearl = false;
        this.delay = -1L;
        super.onDisable();
    }
}
