package wtf.sqwezz.functions.api;

import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.impl.misc.ClientSounds;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.ui.NotificationManager;
import wtf.sqwezz.utils.client.ClientUtil;
import wtf.sqwezz.utils.client.IMinecraft;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.TextFormatting;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class Function implements IMinecraft {

    public static boolean unhooked;
    final Category category;
    protected int animationSize;

    boolean state;
    @Setter
    int bind;
    final List<Setting<?>> settings = new ObjectArrayList<>();
    final Animation animation = new Animation();

    // Добавляем поле name
    private String name;

    public Function() {
        this.name = getClass().getAnnotation(FunctionRegister.class).name();
        this.category = getClass().getAnnotation(FunctionRegister.class).type();
        this.bind = getClass().getAnnotation(FunctionRegister.class).key();
    }

    public Function(String name) {
        this.name = name;
        this.category = Category.Combat;
    }

    public void addSettings(Setting<?>... settings) {
        this.settings.addAll(List.of(settings));
    }

    public boolean onEnable() {
        animation.animate(1, 0.25f, Easings.CIRC_OUT);
        Vredux.getInstance().getEventBus().register(this);
        return false;
    }

    public void onDisable() {
        animation.animate(0, 0.25f, Easings.CIRC_OUT);
        Vredux.getInstance().getEventBus().unregister(this);
    }

    public final void toggle() {
        this.state = !this.state; // Переключаем состояние

        FunctionRegistry FunctionRegistry = Vredux.getInstance().getFunctionRegistry();
        if (FunctionRegistry == null) {
            System.err.println("Ошибка: FunctionRegistry не инициализирован.");
            return;
        }

        ClientSounds clientSounds = FunctionRegistry.getClientSounds();
        if (clientSounds == null) {
            System.err.println("Ошибка: clientSounds не инициализирован.");
            return;
        }

        try {
            if (!this.state) {
                this.onDisable();
                if (clientSounds.isState()) {
                    String fileName = clientSounds.getFileName(state);
                    float volume = clientSounds.volume.get();
                    System.out.println("Playing sound on disable: " + fileName + " at volume: " + volume);
                    ClientUtil.playSound(fileName, volume, false);
                }
                if (SqwezzBubenci.NOTIFICATION_MANAGER != null) {
                    SqwezzBubenci.NOTIFICATION_MANAGER.add("Модуль " + name + " выключен", "", 1);
                } else {
                    System.err.println("Ошибка: NOTIFICATION_MANAGER не инициализирован.");
                }
            } else {
                this.onEnable();
                if (SqwezzBubenci.NOTIFICATION_MANAGER != null) {
                    SqwezzBubenci.NOTIFICATION_MANAGER.add("Модуль " + name + " включен", "", 1);
                } else {
                    System.err.println("Ошибка: NOTIFICATION_MANAGER не инициализирован.");
                }

                if (clientSounds.isState()) {
                    String fileName = clientSounds.getFileName(state);
                    float volume = clientSounds.volume.get();
                    System.out.println("Playing sound on enable: " + fileName + " at volume: " + volume);
                    ClientUtil.playSound(fileName, volume, false);
                }
            }
        } catch (Exception e) {
            handleException("toggle", e);
        }
    }


    public final void setState(boolean newState, boolean config) {
        if (state == newState) {
            return;
        }

        state = newState;

        try {
            if (state) {
                onEnable();
            } else {
                onDisable();
            }

            if (!config) {
                FunctionRegistry FunctionRegistry = Vredux.getInstance().getFunctionRegistry();
                ClientSounds clientSounds = FunctionRegistry.getClientSounds();

                if (clientSounds != null && clientSounds.isState()) {
                    String fileName = clientSounds.getFileName(state);
                    float volume = clientSounds.volume.get();
                    ClientUtil.playSound(fileName, volume, false);
                }
            }
        } catch (Exception e) {
            handleException(state ? "onEnable" : "onDisable", e);
        }
    }

    private void handleException(String methodName, Exception e) {
        if (mc.player != null) {
            print("[" + name + "] Произошла ошибка в методе " + TextFormatting.RED + methodName + TextFormatting.WHITE
                    + "() Предоставьте это сообщение разработчику: " + TextFormatting.GRAY + e.getMessage());
            e.printStackTrace();
        } else {
            System.out.println("[" + name + " Error" + methodName + "() Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}