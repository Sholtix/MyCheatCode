package wtf.sqwezz.functions.impl.misc;

import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeListSetting;
import lombok.Getter;

@Getter
@FunctionRegister(name = "AntiPush", type = Category.Player)
public class AntiPush extends Function {

    private final ModeListSetting modes = new ModeListSetting("Тип",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Вода", false),
            new BooleanSetting("Блоки", true));

    public AntiPush() {
        addSettings(modes);
    }

}
