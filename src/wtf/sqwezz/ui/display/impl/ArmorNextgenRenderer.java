package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.ui.display.ElementRenderer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import wtf.sqwezz.utils.drag.Dragging;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ArmorNextgenRenderer implements ElementRenderer {

    public ArmorNextgenRenderer(Dragging armor) {
    }

    @Subscribe
    public void render(EventDisplay eventDisplay) {

        int posX = IMinecraft.window.getScaledWidth() / 2 + 95;
        int posY = IMinecraft.window.getScaledHeight() - (16 + 2);

        for (ItemStack itemStack : IMinecraft.mc.player.getArmorInventoryList()) {
            if (itemStack.isEmpty()) continue;

            IMinecraft.mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, posX, posY);
            IMinecraft.mc.getItemRenderer().renderItemOverlayIntoGUI(IMinecraft.mc.fontRenderer, itemStack, posX, posY, null);

            posX += 16 + 2;
        }
    }
}
