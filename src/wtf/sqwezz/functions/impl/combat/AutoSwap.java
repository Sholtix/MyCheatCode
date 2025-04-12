//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventCooldown;
import wtf.sqwezz.events.EventKey;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BindSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.player.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.potion.Effects;

@FunctionRegister(
        name = "AutoSwap",
        type = Category.Combat
)
public class AutoSwap extends Function {
    private final ModeSetting swapMode = new ModeSetting("Тип", "Умный", new String[]{"Умный", "По бинду"});
    private final ModeSetting itemType = new ModeSetting("Предмет", "Щит", new String[]{"Щит", "Геплы", "Тотем", "Феерверк", "Шарик", "Aрбалет"});
    private final ModeSetting swapType = new ModeSetting("Свапать на", "Геплы", new String[]{"Щит", "Геплы", "Тотем", "Феерверк", "Шарик", "Aрбалет"});
    private final BindSetting keyToSwap = (new BindSetting("Кнопка", -1)).setVisible(() -> {
        return this.swapMode.is("По бинду");
    });
    private final SliderSetting health = (new SliderSetting("Здоровье", 11.0F, 5.0F, 19.0F, 0.5F)).setVisible(() -> {
        return this.swapMode.is("Умный");
    });
    private final StopWatch stopWatch = new StopWatch();
    private boolean shieldIsCooldown;
    private int oldItem = -1;
    private final StopWatch delay = new StopWatch();
    private AutoTotem autoTotem = null;

    public AutoSwap() {
        this.autoTotem = autoTotem;
        this.addSettings(new Setting[]{this.swapMode, this.itemType, this.swapType, this.keyToSwap, this.health});
    }
    private int getHotbarSlot(Item item) {
        for (int i = 0; i < 9; i++) { // Проверяем только слоты 0-8 (хотбар)
            if (Minecraft.player.inventory.getStackInSlot(i).getItem() == item) {
                return i; // Возвращаем индекс слота в хотбаре
            }
        }
        return -1; // Если предмет не найден
    }
    @Subscribe
    public void onEventKey(EventKey e) {
        if (this.swapMode.is("По бинду")) {
            ItemStack offhandItemStack = Minecraft.player.getHeldItemOffhand();
            boolean isOffhandNotEmpty = !(offhandItemStack.getItem() instanceof AirItem);

            if (e.isKeyDown((Integer)this.keyToSwap.get()) && this.stopWatch.isReached(200L)) {
                Item currentItem = offhandItemStack.getItem();
                Item swapItem = this.getSwapItem();
                Item selectedItem = this.getSelectedItem();
                boolean isHoldingSwapItem = currentItem == swapItem;
                boolean isHoldingSelectedItem = currentItem == selectedItem;

                // Проверка на предмет в хотбаре
                int selectedItemSlot = this.getHotbarSlot(selectedItem);
                int swapItemSlot = this.getHotbarSlot(swapItem);

                // Если в хотбаре есть выбранный предмет и он не в руке
                if (selectedItemSlot >= 0) {
                    InventoryUtil.moveItem(selectedItemSlot + 36, 45, isOffhandNotEmpty); // +36 для перехода к слоту в инвентаре
                    this.stopWatch.reset();
                    return;
                }

                // Если в хотбаре есть предмет для свапа и он не в руке
                if (swapItemSlot >= 0) {
                    InventoryUtil.moveItem(swapItemSlot + 36, 45, isOffhandNotEmpty); // +36 для перехода к слоту в инвентаре
                    this.stopWatch.reset();
                }
            }
        }
    }

    @Subscribe
    private void onCooldown(EventCooldown e) {
        this.shieldIsCooldown = this.isCooldown(e);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (this.swapMode.is("Умный")) {
            Minecraft var10000 = mc;
            Item currentItem = Minecraft.player.getHeldItemOffhand().getItem();
            if (this.stopWatch.isReached(400L)) {
                this.swapIfShieldIsBroken(currentItem);
                this.swapIfHealthToLow(currentItem);
                this.stopWatch.reset();
            }

            boolean isRightClickWithGoldenAppleActive = false;
            if (currentItem == Items.GOLDEN_APPLE) {
                var10000 = mc;
                if (!Minecraft.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE)) {
                    isRightClickWithGoldenAppleActive = mc.gameSettings.keyBindUseItem.isKeyDown();
                }
            }

            if (isRightClickWithGoldenAppleActive) {
                this.stopWatch.reset();
            }
        }

    }

    public void onDisable() {
        this.shieldIsCooldown = false;
        this.oldItem = -1;
        super.onDisable();
    }

    private void swapIfHealthToLow(Item currentItem) {
        boolean isOffhandNotEmpty = !(currentItem instanceof AirItem);
        boolean isHoldingGoldenApple = currentItem == this.getSwapItem();
        boolean isHoldingSelectedItem = currentItem == this.getSelectedItem();
        Minecraft var10000 = mc;
        boolean gappleIsNotCooldown = !Minecraft.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
        int goldenAppleSlot = this.getSlot(this.getSwapItem());
        if (!this.shieldIsCooldown && gappleIsNotCooldown) {
            if (this.isLowHealth() && !isHoldingGoldenApple && isHoldingSelectedItem) {
                InventoryUtil.moveItem(goldenAppleSlot, 45, isOffhandNotEmpty);
                if (isOffhandNotEmpty && this.oldItem == -1) {
                    this.oldItem = goldenAppleSlot;
                }
            } else if (!this.isLowHealth() && isHoldingGoldenApple && this.oldItem >= 0) {
                InventoryUtil.moveItem(this.oldItem, 45, isOffhandNotEmpty);
                this.oldItem = -1;
            }
        }

    }

    private void swapIfShieldIsBroken(Item currentItem) {
        boolean isOffhandNotEmpty = !(currentItem instanceof AirItem);
        boolean isHoldingGoldenApple = currentItem == this.getSwapItem();
        boolean isHoldingSelectedItem = currentItem == this.getSelectedItem();
        Minecraft var10000 = mc;
        boolean gappleIsNotCooldown = !Minecraft.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
        int goldenAppleSlot = this.getSlot(this.getSwapItem());
        if (this.shieldIsCooldown && !isHoldingGoldenApple && isHoldingSelectedItem && gappleIsNotCooldown) {
            InventoryUtil.moveItem(goldenAppleSlot, 45, isOffhandNotEmpty);
            if (isOffhandNotEmpty && this.oldItem == -1) {
                this.oldItem = goldenAppleSlot;
            }

            this.print("" + this.shieldIsCooldown);
        } else if (!this.shieldIsCooldown && isHoldingGoldenApple && this.oldItem >= 0) {
            InventoryUtil.moveItem(this.oldItem, 45, isOffhandNotEmpty);
            this.oldItem = -1;
        }

    }

    private boolean isLowHealth() {
        Minecraft var10000 = mc;
        float var2 = Minecraft.player.getHealth();
        Minecraft var10001 = mc;
        float var3;
        if (Minecraft.player.isPotionActive(Effects.ABSORPTION)) {
            var10001 = mc;
            var3 = Minecraft.player.getAbsorptionAmount();
        } else {
            var3 = 0.0F;
        }

        float currentHealth = var2 + var3;
        return currentHealth <= (Float)this.health.get();
    }

    private boolean isCooldown(EventCooldown cooldown) {
        Item item = cooldown.getItem();
        if (!this.itemType.is("Shield")) {
            return false;
        } else {
            return cooldown.isAdded() && item instanceof ShieldItem;
        }
    }

    private Item getSwapItem() {
        return this.getItemByType((String)this.swapType.get());
    }

    private Item getSelectedItem() {
        return this.getItemByType((String)this.itemType.get());
    }

    private Item getItemByType(String itemType) {
        Item var10000;
        switch (itemType) {
            case "Щит" -> var10000 = Items.SHIELD;
            case "Тотем" -> var10000 = Items.TOTEM_OF_UNDYING;
            case "Геплы" -> var10000 = Items.GOLDEN_APPLE;
            case "Феерверк" -> var10000 = Items.FIREWORK_ROCKET;
            case "Шарик" -> var10000 = Items.PLAYER_HEAD;
            case "Aрбалет" -> var10000 = Items.CROSSBOW;
            default -> var10000 = Items.AIR;
        }

        return var10000;
    }

    private int getSlot(Item item) {
        int finalSlot = -1;

        for(int i = 0; i < 36; ++i) {
            Minecraft var10000 = mc;
            if (Minecraft.player.inventory.getStackInSlot(i).getItem() == item) {
                var10000 = mc;
                if (Minecraft.player.inventory.getStackInSlot(i).isEnchanted()) {
                    finalSlot = i;
                    break;
                }

                finalSlot = i;
            }
        }

        if (finalSlot < 9 && finalSlot != -1) {
            finalSlot += 36;
        }

        return finalSlot;
    }
}
