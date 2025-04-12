package wtf.sqwezz.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BindSetting;
import wtf.sqwezz.functions.settings.impl.StringSetting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FunctionRegister(name = "FakeCheck", type = Category.Misc)
public class FakeProv extends Function {
    private final StringSetting moder = new StringSetting("Ник модера", "gromaforz", "Введите ник модера который якобы вас вызвал на проверку");
    public static final BindSetting bind = new BindSetting("Выключение фейк проверки", -1);
    public FakeProv() {
        addSettings(moder, bind);
    }
    public static boolean proverka = false;
    public static boolean state = false;
    @Subscribe
    private void onUpdt(EventUpdate e) {
        if (bind.get() == -1) {
            print("" + TextFormatting.RED + TextFormatting.BOLD + "Кнопка выключения фейк проверки не назначена!");
            toggle();
            return;
        }
        printProverka(moder.get());
        toggle();
    }
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public void printProverka(String input) {
        if (mc.player == null)
            return;
        ITextComponent text = new StringTextComponent( TextFormatting.YELLOW + "[" + TextFormatting.RED + "gromaforz" + TextFormatting.YELLOW + " ->" + TextFormatting.RED + " я" + TextFormatting.YELLOW + " ]" + TextFormatting.RED + " Обратите внимание!" + TextFormatting.YELLOW + " Мы не собираемся причинить вред Вашему компьютеру, удаленный доступ используется исключительно для удобства проверки. В любой момент Вы можете закрыть соединение, но если сотрудник прав - бан.");
        if (!proverka) {
            clientMessages.add(text);
        }
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 0, false);
        if (!state) {
            ITextComponent text2 = new StringTextComponent(TextFormatting.YELLOW + "[" + TextFormatting.RED + "gromaforz" + TextFormatting.YELLOW + " ->" + TextFormatting.RED + " я" + TextFormatting.YELLOW + "]" + TextFormatting.RED + " у тебя 7 мин");
            clientMessages.add(text2);
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text2, 28, false);
        }
        state = true;
        proverka = true;
        scheduler.schedule(() -> proverka = false, 4, TimeUnit.MINUTES);
    }

    @Override
    public boolean onEnable() {
        super.onEnable();
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        state = false;
    }

    @Subscribe
    protected float[] rotations(PlayerEntity var1) {
        return new float[0];
    }
}