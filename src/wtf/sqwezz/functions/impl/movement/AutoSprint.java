package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

@FunctionRegister(name = "AutoSprint", type = Category.Movement)
public class AutoSprint extends Function {
    public BooleanSetting saveSprint = new BooleanSetting("Сохранять спринт", true);
    private long lastSprintTime = 1000; // Время последнего спринта

    public AutoSprint() {
        addSettings(saveSprint);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (Minecraft.getInstance().player != null) {
            if (Minecraft.getInstance().player.isInWater()) {
                saveSprint.set(false);
            } else {
                saveSprint.set(true);
            }

            // Проверка задержки
            if (System.currentTimeMillis() - lastSprintTime >= 1000) {
                if (saveSprint.get()) {
                    Minecraft.getInstance().player.setSprinting(true);
                    lastSprintTime = System.currentTimeMillis(); // Обновляем время последнего спринта
                }
            }
        }
    }
}