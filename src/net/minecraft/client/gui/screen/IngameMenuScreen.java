package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.api.FunctionRegistry;
import wtf.sqwezz.functions.settings.impl.SelfDestruct;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import wtf.sqwezz.utils.client.ClientUtil;

public class IngameMenuScreen extends Screen {
    private final boolean isFullMenu;
    private final Minecraft mc;

    public IngameMenuScreen(boolean isFullMenu) {
        super(isFullMenu ? new TranslationTextComponent("menu.game") : new TranslationTextComponent("menu.paused"));
        this.isFullMenu = isFullMenu;
        mc = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        if (this.isFullMenu) {
            this.addButtons();
            this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, new StringTextComponent("Reconnect"), (button) -> {
                reconnectToServer();
            }));
        }
    }


    public void reconnectToServer() {
        if (!ClientUtil.isConnectedToServer("spooky")) {
        } else {
            int server = this.getAnarchyServerNumber();
            if (server == -1) {
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
    private void addButtons() {
        int i = -16;
        int j = 98;
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslationTextComponent("menu.returnToGame"), (button2) ->
        {
            this.minecraft.displayGuiScreen((Screen) null);
            this.minecraft.mouseHelper.grabMouse();
        }));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslationTextComponent("gui.advancements"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancementManager()));
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslationTextComponent("gui.stats"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));
        String s = SharedConstants.getVersion().isStable() ? "https://t.me/neverlosedlc" : "https://t.me/neverlosedlc";
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslationTextComponent("menu.sendFeedback"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen((open) -> {
                if (open) {
                    Util.getOSType().openURI(s);
                }

                this.minecraft.displayGuiScreen(this);
            }, s, true));
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslationTextComponent("menu.reportBugs"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen((open) -> {
                if (open) {
                    Util.getOSType().openURI("https://t.me/neverlosedlc");
                }

                this.minecraft.displayGuiScreen(this);
            }, "https://t.me/neverlosedlc", true));
        }));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslationTextComponent("menu.options"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
        }));
        Button button = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslationTextComponent("menu.shareToLan"), (button2) ->
        {
            this.minecraft.displayGuiScreen(new ShareToLanScreen(this));
        }));
        button.active = this.minecraft.isSingleplayer() && !this.minecraft.getIntegratedServer().getPublic();
        Button button1 = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslationTextComponent("menu.returnToMenu"), (button2) ->
        {
            boolean flag = this.minecraft.isIntegratedServerRunning();
            boolean flag1 = this.minecraft.isConnectedToRealms();
            button2.active = false;
            this.minecraft.world.sendQuittingDisconnectingPacket();

            if (flag) {
                this.minecraft.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                this.minecraft.unloadWorld();
            }

            FunctionRegistry FunctionRegistry = Vredux.getInstance().getFunctionRegistry();
            SelfDestruct selfDestruct = FunctionRegistry.getSelfDestruct();

            if (flag) {
                this.minecraft.displayGuiScreen(selfDestruct.unhooked ? new MainMenuScreen() : new MainMenuScreen());
            } else if (flag1) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.func_231394_a_(selfDestruct.unhooked ? new MainMenuScreen() : new MainMenuScreen());
            } else {
                this.minecraft.displayGuiScreen(new MultiplayerScreen(selfDestruct.unhooked ? new MainMenuScreen() : new MainMenuScreen()));
            }
        }));

        if (!this.minecraft.isIntegratedServerRunning()) {
            button1.setMessage(new TranslationTextComponent("menu.disconnect"));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isFullMenu) {
            this.renderBackground(matrixStack);
            drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 60, 16777215);
        } else {
            drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 10, 16777215);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
