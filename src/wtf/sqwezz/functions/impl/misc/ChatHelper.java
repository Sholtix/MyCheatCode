package wtf.sqwezz.functions.impl.misc;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeListSetting;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CChatMessagePacket;

@Getter
@FunctionRegister(name = "ChatHelper", type = Category.Misc)
public class ChatHelper extends Function {

    final ModeListSetting options = new ModeListSetting("Опции",
            new BooleanSetting("/ah me (Funtime)", true),
            new BooleanSetting("Чат калькулятор", false));

    public ChatHelper() {
        addSettings(options);
    }

    @Subscribe
    private void onChat(EventPacket event) {
        if (event.getPacket() instanceof CChatMessagePacket) {
            CChatMessagePacket packet = (CChatMessagePacket) event.getPacket();
            String message = packet.getMessage();

            if (options.getValueByName("/ah me").get() && message.startsWith("/ah me")) {
                String playerName = Minecraft.player.getName().getUnformattedComponentText();

                String newMessage = message.replace("me", playerName);

                Minecraft.player.sendChatMessage(newMessage);

                event.cancel();
                return;
            }

            if (options.getValueByName("Чат калькулятор").get()) {
                if (message.startsWith("/ah sell ")) {
                    String expression = message.substring(9).trim();
                    String result = calculateExpression(expression);
                    if (result != null) {
                        Minecraft.getInstance().player.sendChatMessage("/ah sell " + result);

                        event.cancel();
                    }
                }
            }
        }
    }

    private String calculateExpression(String expression) {
        try {

            expression = expression.replaceAll("\\s+", "");

            if (!expression.matches("\\d+[+*/-]\\d+")) {
                return null;
            }

            double result = 0.0;
            if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                if (parts.length == 2) {
                    result = Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
                }
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                if (parts.length == 2) {
                    result = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
                }
            } else if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                if (parts.length == 2) {
                    result = Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
                }
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                if (parts.length == 2) {
                    result = Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
                }
            }

            return String.valueOf(Math.round(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}