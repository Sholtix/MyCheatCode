package wtf.sqwezz.functions.impl.misc;

import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;

@FunctionRegister(name = "ClientSounds", type = Category.Settings)
public class ClientSounds extends Function {

    public ModeSetting mode = new ModeSetting("Тип", "Обычный", "Обычный", "Пузырьки");
    public SliderSetting volume = new SliderSetting("Громкость", 70.0f, 0.0f, 100.0f, 1.0f);

    public ClientSounds() {
        addSettings(mode, volume);
    }


    public String getFileName(boolean state) {
        switch (mode.get()) {
            case "Обычный" -> {
                return state ? "enable" : "disable".toString();
            }
            case "Пузырьки" -> {
                return state ? "enableBubbles" : "disableBubbles";
            }
        }
        return "";
    }
}
