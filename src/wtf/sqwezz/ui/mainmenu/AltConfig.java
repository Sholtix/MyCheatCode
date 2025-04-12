/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package wtf.sqwezz.ui.mainmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.ui.mainmenu.Account;
import wtf.sqwezz.utils.client.IMinecraft;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class AltConfig
implements IMinecraft {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File(AltConfig.mc.gameDir, "Neverlose/files/alts.cfg");

    public void init() throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        } else {
            this.readAlts();
        }
    }

    public static void updateFile() {
        try {
            StringBuilder builder = new StringBuilder();
            for (Account alt : Vredux.getInstance().getAltManager().accounts) {
                builder.append(alt.accountName + ":" + alt.dateAdded).append("\n");
            }
            Files.write(file.toPath(), builder.toString().getBytes(), new OpenOption[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readAlts() {
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file.getAbsolutePath()))));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String username = parts[0];
                Vredux.getInstance().getAltManager().accounts.add(new Account(username, Long.valueOf(parts[1])));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

