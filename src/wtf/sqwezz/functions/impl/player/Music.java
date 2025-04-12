package wtf.sqwezz.functions.impl.player;

import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

@FunctionRegister(
        name = "Music",
        type = Category.Settings
)
public class Music extends Function {
    private boolean isActionEnabled;
    private Clip clip;
    private final String musicFolderPath = "neverlose"; // укаж свой путь к папке с музыкой пжж сука
    private final Random random = new Random();


    @Override
    public boolean onEnable() {
        playRandomSong();
        this.performActionIfRequired(false);
        super.onEnable();
        return true;
    }

    private void performActionIfRequired(boolean DO) {
        if (DO) {
            mc.gameSettings.keyBindSneak.pressed = mc.player.ticksExisted % 2 == 0;
            if (!this.isActionEnabled) {
                this.isActionEnabled = true;
            }
        } else if (this.isActionEnabled) {
            mc.gameSettings.keyBindSneak.pressed = false;
            this.isActionEnabled = false;
        }
    }

    private void playRandomSong() {
        File musicFolder = new File(musicFolderPath);
        File[] musicFiles = musicFolder.listFiles((dir, name) -> name.endsWith(".wav"));

        if (musicFiles != null && musicFiles.length > 0) {
            int randomIndex = random.nextInt(musicFiles.length);
            File selectedFile = musicFiles[randomIndex];

            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(selectedFile)) {
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Нет доступных музыкальных файлов в папке: " + musicFolderPath);
        }
    }

    @Override
    public void onDisable() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        super.onDisable();
    }
}