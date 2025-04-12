package wtf.sqwezz.functions.impl.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.utils.math.StopWatch;
import java.util.Random;

@FunctionRegister(
        name = "CasinoBOT",
        type = Category.Player
)
public class CasinoHW extends Function {
    private final StopWatch timer = new StopWatch(); // Таймер для первого сообщения
    private final StopWatch followUpTimer = new StopWatch(); // Таймер для второго сообщения
    private final StopWatch secondFollowUpTimer = new StopWatch(); // Таймер для третьего сообщения
    private boolean messageSent = false; // Флаг, указывающий, отправлено ли первое сообщение
    private boolean followUpSent = false; // Флаг для второго сообщения

    public CasinoHW() {
        // Конструктор без параметров
        timer.reset(); // Сброс таймера при создании
    }

    @Subscribe
    public void onGameTick(EventUpdate event) {
        ClientPlayerEntity player = Minecraft.getInstance().player; // Получение игрока
        if (player == null) return; // Проверка на null, чтобы избежать ошибок

        // Если 5 секунд прошло и первое сообщение еще не отправлено
        if (!messageSent && timer.isReached(5000L)) {
            String playerName = player.getScoreboardName();
            player.sendChatMessage("! Дарова анка, сейчас буду проводить конкурс /pay " + playerName +
                    " и сумма перевода (кто первый скинет деньги после слова СТАРТ получит в 2 раза больше. Минимальная сумма для игры 100000.");
            timer.reset(); // Сброс таймера первого сообщения
            messageSent = true; // Установка флага, что сообщение отправлено
            followUpTimer.reset(); // Сброс таймера для второго сообщения
        }

        // Если 5 секунд прошло с момента отправки первого сообщения
        if (messageSent && followUpTimer.isReached(3000L)) {
            player.sendChatMessage("СТРАТ");
            followUpSent = true; // Установка флага для второго сообщения
            followUpTimer.reset(); // Сброс таймера для второго сообщения после его отправки
        }

        // Если 2 секунды прошло с момента отправки второго сообщения
        if (followUpSent && secondFollowUpTimer.isReached(3000L)) {
            player.sendChatMessage("СТАРТ");
            followUpSent = false; // Сброс флага для отправки в следующий раз
            secondFollowUpTimer.reset(); // Сброс таймера для третьего сообщения
        }
        if (followUpSent && secondFollowUpTimer.isReached(4400L)) {
            String playerName = player.getScoreboardName();
            player.sendChatMessage("! Дарова анка, сейчас буду проводить конкурс /pay " + playerName +
                 "и сумма перевода (кто первый скинет деньги после слова СТАРТ получит в 2 раза больше. Минимальная сумма для игры 100000.");
            followUpSent = false; // Сброс флага для отправки в следующий раз
            secondFollowUpTimer.reset(); // Сброс таймера для третьего сообщения
        }
    }
}