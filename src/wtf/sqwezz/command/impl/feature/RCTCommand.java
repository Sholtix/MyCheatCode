//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.command.impl.feature;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import wtf.sqwezz.command.Command;
import wtf.sqwezz.command.Logger;
import wtf.sqwezz.command.MultiNamedCommand;
import wtf.sqwezz.command.Parameters;
import wtf.sqwezz.utils.client.ClientUtil;

public class RCTCommand implements Command, MultiNamedCommand {
    private final Logger logger;
    private final Minecraft mc;

    public void execute(Parameters parameters) {
        if (!ClientUtil.isConnectedToServer("spooky")) {
            this.logger.log("Этот RCT работает только на сервере Spookytime");
        } else {
            int server = this.getAnarchyServerNumber();
            if (server == -1) {
                this.logger.log("Не удалось получить номер анархии.");
            } else {
                Minecraft var10000 = this.mc;
                Minecraft.player.sendChatMessage("/hub");

                try {
                    Thread.sleep(400L);
                } catch (InterruptedException var4) {
                    InterruptedException e = var4;
                    throw new RuntimeException(e);
                }

                var10000 = this.mc;
                Minecraft.player.sendChatMessage("/an" + server);
            }
        }
    }

    private int getAnarchyServerNumber() {
        if (this.mc.ingameGUI.getTabList().header != null) {
            String serverHeader = TextFormatting.getTextWithoutFormattingCodes(this.mc.ingameGUI.getTabList().header.getString());
            if (serverHeader != null && serverHeader.contains("Анархия-")) {
                return Integer.parseInt(serverHeader.split("Анархия-")[1].trim());
            }
        }

        return -1;
    }

    public String name() {
        return "rct";
    }

    public String description() {
        return "Перезаходит на анархию";
    }

    public List<String> aliases() {
        return Collections.singletonList("reconnect");
    }

    public RCTCommand(Logger logger, Minecraft mc) {
        this.logger = logger;
        this.mc = mc;
    }
}
