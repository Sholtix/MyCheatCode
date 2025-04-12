package wtf.sqwezz.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionRegister(name = "AutoEvent", type = Category.Misc)
public class AutoEvent extends Function {

    private final boolean autoGPS = true; // я знаю что не лечится.

    public AutoEvent() {
    }

    @Subscribe
    public void onPacket(EventPacket event) {
        if (!autoGPS || !(event.getPacket() instanceof SChatPacket)) return;

        SChatPacket packet = (SChatPacket) event.getPacket();
        String chatMessage = packet.getChatComponent().getString();

        if (chatMessage.contains("Появился на координатах")) {
            // САЛАТ СПАСАЙ СПАСАЙ САЛАТИК МОЙ СПАСАЙ
            Pattern pattern = Pattern.compile("\\[(\\d+) (\\d+) (\\d+)]");
            Matcher matcher = pattern.matcher(chatMessage);
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(3));
                // доисторическая система добавления точки от динозавров кодинга(кстати работает быстрее чем отправляется сообщение с координатами!!!)
                sendCommand(".gps add Myst " + x + " 100 " + z); // "бро почему 100 почему нельзя брать высоту с сообщения?"
                // ало я даун и не вижу точку что делать не работает ваш autoevent нахуй
                System.out.println("Добавил точку с названием Myst!");
            }
        }
    }

    private void sendCommand(String command) {
        Minecraft.getInstance().player.sendChatMessage(command);
    }
}