//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.utils.drag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;

public class DragManager {
    public static final Logger logger = Logger.getLogger(DragManager.class.getName());
    public static LinkedHashMap<String, Dragging> draggables = new LinkedHashMap();
    public static final File DRAG_DATA;
    private static final Gson GSON;

    public DragManager() {
    }

    public static void save() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(DRAG_DATA);
            writer.write(GSON.toJson(draggables.values()));
            writer.close();
            Files.writeString(DRAG_DATA.toPath(), GSON.toJson(draggables.values()));
        } catch (IOException var1) {
            IOException ex = var1;
            logger.log(Level.WARNING, "Not Found IOException", ex);
        }

    }

    public static void load() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        } else {
            Dragging[] draggings;
            try {
                draggings = (Dragging[])GSON.fromJson(Files.readString(DRAG_DATA.toPath()), Dragging[].class);
            } catch (IOException var6) {
                IOException ex = var6;
                logger.log(Level.WARNING, "Not Found IOException", ex);
                return;
            }

            Dragging[] var7 = draggings;
            int var2 = draggings.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Dragging dragging = var7[var3];
                if (dragging == null) {
                    return;
                }

                Dragging currentDrag = (Dragging)draggables.get(dragging.getName());
                if (currentDrag != null) {
                    currentDrag.setX(dragging.getX());
                    currentDrag.setY(dragging.getY());
                    draggables.put(dragging.getName(), currentDrag);
                }
            }

        }
    }

    static {
        DRAG_DATA = new File(Minecraft.getInstance().gameDir, "\\Neverlose\\files\\drags.cfg");
        GSON = (new GsonBuilder()).setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    }
}
