package wtf.sqwezz.utils;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import wtf.sqwezz.Vredux;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import java.time.OffsetDateTime;

public class DiscordRPC {
    public static Minecraft mc = Minecraft.getInstance();
    static IPCClient client = new IPCClient(1360364473071702077L);

    public static void startRPC() {
        client.setListener(new IPCListener() {
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {
                IPCListener.super.onPacketReceived(client, packet);
            }

            @Override
            public void onReady(IPCClient client) {
                PlayerEntity player = mc.player;
                String playerName = null;
                if (player != null) {
                    playerName = player.getName().getString();
                }

                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("★ Dev: shalnark36" + " »" + " UID: 36 ")
                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage(("https://media.tenor.com/7cfKT2I1wGUAAAM/neverlose-cheat-minecraft.gif"), "@shalnark36")
                        .setState("★ Build 3.6" );

                client.sendRichPresence(builder.build());
            }
        });

        try {
            client.connect();
        } catch (NoDiscordClientException e) {
            System.out.println("DiscordRPC: " + e.getMessage());
        }
    }
}