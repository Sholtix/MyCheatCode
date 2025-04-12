package wtf.sqwezz;

import com.google.common.eventbus.EventBus;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import dev.waveycapes.WaveyCapesBase;
import wtf.sqwezz.command.*;
import wtf.sqwezz.command.friends.FriendStorage;
import wtf.sqwezz.command.impl.*;
import wtf.sqwezz.command.impl.feature.*;
import wtf.sqwezz.command.staffs.StaffStorage;
import wtf.sqwezz.config.ConfigStorage;
import wtf.sqwezz.discord.DiscordLogger;
import wtf.sqwezz.events.EventKey;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegistry;
import wtf.sqwezz.functions.api.SqwezzBubenci;
import wtf.sqwezz.functions.impl.combat.killAura.rotation.RotationHandler;
import wtf.sqwezz.ui.NotificationManager;
import wtf.sqwezz.ui.ab.factory.ItemFactory;
import wtf.sqwezz.ui.ab.factory.ItemFactoryImpl;
import wtf.sqwezz.ui.ab.logic.ActivationLogic;
import wtf.sqwezz.ui.ab.model.IItem;
import wtf.sqwezz.ui.ab.model.ItemStorage;
import wtf.sqwezz.ui.ab.render.Window;
import wtf.sqwezz.ui.autobuy.AutoBuyConfig;
import wtf.sqwezz.ui.autobuy.AutoBuyHandler;
import wtf.sqwezz.ui.dropdown.DropDown;
import wtf.sqwezz.ui.mainmenu.AltConfig;
import wtf.sqwezz.ui.mainmenu.AltManager;
import wtf.sqwezz.ui.mainmenu.AltWidget;
import wtf.sqwezz.ui.midnight.ClickGui;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.ui.styles.StyleFactory;
import wtf.sqwezz.ui.styles.StyleFactoryImpl;
import wtf.sqwezz.ui.styles.StyleManager;
import wtf.sqwezz.utils.DiscordRPC;
import wtf.sqwezz.utils.TPSCalc;
import wtf.sqwezz.utils.client.ServerTPS;
import wtf.sqwezz.utils.drag.DragManager;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.font.Fonts;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import via.ViaMCP;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Я просто делаю свой клиент, я свободный человек, пасщю что хочу
 * Газ (А-а)
 * Бро, я напастил авто тотем уже в три лет
 * Не ври, что ты не пастер, это не писаный клиент
 * Деус — модель, уши размером с живот макавто
 * Ты нервничаешь, бесишь сам себя, когда сурсов нет
 * У, у, у, у, у, я больших мазиков съедатель (А-а)
 * Больших уши уничтожитель, мазикавыпиватель (А-а)
 * Пастирских читов игратель, тебя выключатель (У-у)
 * Белый, но во мне краситель, к пастингу привыкатель (Е-е)
 * Бро, это shit skidding, я им рассказыватель (отвечаю)
 * Большие бидоны мазика — силой слова я их поедатель (У)
 * У меня большой живот — я его показыватель
 * Бро реал скидет, зовёт ся falok(Fals3r)
 * Эй, чит восьми из десяти mc.player — говно, я таким сру утром
 * Четыре бегина делят дерьмо — это на двух клиентах
 * Мой любимый кодер стал пастером — уже не авто тотем макслоло
 * Мазик рушит стены моего желудка
 * Она долго запускаться, как будто лоудер говно
 * Кодеры купаются в мазике, бля, они срут в коде
 * У меня есть шкильники качки, да, они жрут мазик
 * Они сразу поломают кисть, они не жмут руку
 * Ты пастишь реже ,чем dedinside — тя там не видно (Ха)
 * Я сейчас official, иногда ворую с клиентов (У-у)
 * Бро откинулся в Актобе — щас он летит к нам (У-у)
 * Кто этот ебаный Пастер, чё он нам пиздит там? (У-у)
 * Я могу потыкать ему этим Кодом прям в бошку (Гр-ра)
 * Подлетай к моему аирДропу — я залутаю его (Ха, макавто)
 * Я могу пастить даже спиной к монику, ты не сможешь так (Не сможешь)
 * Пока мне делали glow, у тебя пастили под носом, е, а-а
 * [Аутро]
 * У, у, у, у, у, я больших мазиков съедатель
 * Больших уши уничтожитель, мазикавыпиватель
 * Пастирских читов игратель, тебя выключатель
 * Давай спастим wintware, короче
 */

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vredux {

    public boolean playerOnServer = false;
    public static final String CLIENT_NAME = "ctl+c ctrl+v solutions";

    // Экземпляр Vredux
    @Getter
    private static Vredux instance;

    // Менеджеры
    private FunctionRegistry FunctionRegistry;
    private ConfigStorage configStorage;
    private CommandDispatcher commandDispatcher;
    private ServerTPS serverTPS;
    private MacroManager macroManager;
    private StyleManager styleManager;

    private FriendStorage friendStorage;

    // Менеджер событий и скриптов
    private final EventBus eventBus = new EventBus();

    // Директории
    private final File clientDir = new File(Minecraft.getInstance().gameDir + "\\Neverlose");
    private final File filesDir = new File(Minecraft.getInstance().gameDir + "\\Neverlose\\files");
    public float interpolateState = 0;
    public float needValueInterpolate = 1;

    // Элементы интерфейса
    private AltManager altManager;
    private AltConfig altConfig;
    private ClickGui dropDown;
    private Window autoBuyUI;

    // Конфигурация и обработчики
    private AutoBuyConfig autoBuyConfig = new AutoBuyConfig();
    private AutoBuyHandler autoBuyHandler;
    private ViaMCP viaMCP;
    private TPSCalc tpsCalc;
    private ActivationLogic activationLogic;
    private ItemStorage itemStorage;
    private RotationHandler rotationHandler;
    private WaveyCapesBase waveyCapesBase;

    public Vredux() {
        instance = this;

        if (!clientDir.exists()) {
            clientDir.mkdirs();
        }
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }

        clientLoad();
        friendStorage.load();
        StaffStorage.load();
        DiscordRPC.startRPC();

        waveyCapesBase = new WaveyCapesBase();
        waveyCapesBase.init();
    }

    public FriendStorage getFriendStorage() {
        return friendStorage;
    }

    final IPCClient client = new IPCClient(1236549348909776957L);

    public void startRPC() {

        client.setListener(new IPCListener() {
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {
                IPCListener.super.onPacketReceived(client, packet);
            }

            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder

                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage("avatar", "Let's remember the well-forgotten old");
                client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect();
        } catch (NoDiscordClientException e) {
            System.out.println("DiscordRPC: " + e.getMessage());
        }
    }

    public void stopRPC() {
        if (client.getStatus() == PipeStatus.CONNECTED) client.close();
    }

    public Dragging createDrag(Function module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
    private void clientLoad() {
        FunctionRegistry = new FunctionRegistry();
        viaMCP = new ViaMCP();
        serverTPS = new ServerTPS();
        macroManager = new MacroManager();
        configStorage = new ConfigStorage();
        initCommands();
        initStyles();
        altManager = new AltManager();
        altConfig = new AltConfig();
        tpsCalc = new TPSCalc();
        SqwezzBubenci.NOTIFICATION_MANAGER = new NotificationManager();
        rotationHandler = new RotationHandler();



        try {
            autoBuyConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            altConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            configStorage.init();
        } catch (IOException e) {
            System.out.println("Ошибка при подгрузке конфига.");
        }
        try {
            macroManager.init();
        } catch (IOException e) {
            System.out.println("Ошибка при подгрузке конфига макросов.");
        }
        DragManager.load();
        dropDown = new ClickGui();
        initAutoBuy();
        autoBuyUI = new Window(new StringTextComponent(""), itemStorage);
        //autoBuyUI = new AutoBuyUI(new StringTextComponent("A"));
        autoBuyHandler = new AutoBuyHandler();
        autoBuyConfig = new AutoBuyConfig();
        Fonts.init();
        eventBus.register(this);
    }


    public void onKeyPressed(int key) {
        if (FunctionRegistry.getSelfDestruct().enabled) return;
        Vredux.getInstance().getEventBus().post(new EventKey(key));

        macroManager.onKeyPressed(key);

        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            Minecraft.getInstance().displayGuiScreen(dropDown);
        }
        if (this.FunctionRegistry.getAutoBuyUI().isState() && this.FunctionRegistry.getAutoBuyUI().setting.get() == key) {
            Minecraft.getInstance().displayGuiScreen(autoBuyUI);
        }


    }
    private void initAutoBuy() {
        ItemFactory itemFactory = new ItemFactoryImpl();
        CopyOnWriteArrayList<IItem> items = new CopyOnWriteArrayList<>();
        itemStorage = new ItemStorage(items, itemFactory);

        activationLogic = new ActivationLogic(itemStorage, eventBus);
    }
    private void initCommands() {
        Minecraft mc = Minecraft.getInstance();
        Logger logger = new MultiLogger(List.of(new ConsoleLogger(), new MinecraftLogger()));
        List<Command> commands = new ArrayList<>();
        Prefix prefix = new PrefixImpl();
        commands.add(new ListCommand(commands, logger));
        commands.add(new FriendCommand(prefix, logger, mc));
        commands.add(new BindCommand(prefix, logger));
        commands.add(new GPSCommand(prefix, logger));
        commands.add(new WayCommand(prefix, logger));
        commands.add(new ConfigCommand(configStorage, prefix, logger));
        commands.add(new MacroCommand(macroManager, prefix, logger));
        commands.add(new VClipCommand(prefix, logger, mc));
        commands.add(new HClipCommand(prefix, logger, mc));
        commands.add(new StaffCommand(prefix, logger));
        commands.add(new MemoryCommand(logger));
        commands.add(new RCTCommand(logger, mc));

        AdviceCommandFactory adviceCommandFactory = new AdviceCommandFactoryImpl(logger);
        ParametersFactory parametersFactory = new ParametersFactoryImpl();

        commandDispatcher = new StandaloneCommandDispatcher(commands, adviceCommandFactory, prefix, parametersFactory, logger);
    }
    private void initStyles() {
        StyleFactory styleFactory = new StyleFactoryImpl();
        List<Style> styles = new ArrayList();

        styles.add(styleFactory.createStyle("Дефолт", new Color(118, 8, 138, 255), new Color(209, 0, 255, 255)));
        styles.add(styleFactory.createStyle("Бирюзовый", new Color(0, 255, 121, 255), new Color(0, 255, 196, 255)));
        styles.add(styleFactory.createStyle("Астольфо", new Color(243, 160, 232, 255), new Color(171, 250, 243, 255)));
        styles.add(styleFactory.createStyle("Алмазный Хуй", new Color(87, 148, 253, 240), new Color(255, 255, 255, 255)));
        styles.add(styleFactory.createStyle("Ультра", new Color(41, 147, 182, 255), new Color(42, 229, 245, 255)));
        styles.add(styleFactory.createStyle("Феникс", new Color(255, 106, 0, 255), new Color(255, 192, 0, 255)));
        styles.add(styleFactory.createStyle("Мята", new Color(41, 151, 72, 255), new Color(59, 248, 104, 255)));
        styles.add(styleFactory.createStyle("Лайт", new Color(255, 43, 130, 255), new Color(144, 255, 111, 255)));
        styles.add(styleFactory.createStyle("Кибер", new Color(128, 255, 235, 255), new Color(193, 88, 255, 255)));
        styles.add(styleFactory.createStyle("Милота", new Color(255, 153, 116, 255), new Color(187, 132, 255, 255)));
        styles.add(styleFactory.createStyle("Арт", new Color(209, 239, 251, 255), new Color(183, 198, 246, 255)));
        styles.add(styleFactory.createStyle("Новогодний", new Color(255, 0, 0, 255), new Color(255, 255, 255, 255)));

        this.styleManager = new StyleManager(styles, (Style)styles.get(0));
    }
}

