package wtf.sqwezz.command.impl.feature;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.command.*;
import wtf.sqwezz.command.impl.CommandException;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.functions.api.FunctionRegistry;
import wtf.sqwezz.functions.settings.impl.SelfDestruct;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GPSCommand implements Command, CommandWithAdvice, IMinecraft {
    final Prefix prefix;
    final Logger logger;
    Vector2f cordsMap = new Vector2f(0, 0);

    public GPSCommand(Prefix prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
        Vredux.getInstance().getEventBus().register(this);
    }

    @Override
    public void execute(Parameters parameters) {
        String commandType = parameters.asString(0).orElse("");

        switch (commandType) {
            case "add" -> addGPS(parameters);
            case "off" -> removeGPS();
            default ->
                    throw new CommandException(TextFormatting.RED + "Укажите тип команды:" + TextFormatting.GRAY + " add, off");
        }
    }

    private void addGPS(Parameters param) {
        int x = param.asInt(1)
                .orElseThrow(() -> new CommandException(TextFormatting.RED + "Укажите первую координату!"));
        int z = param.asInt(2)
                .orElseThrow(() -> new CommandException(TextFormatting.RED + "Укажите вторую координату!"));

        if (x == 0 && z == 0) {
            logger.log("Координаты должны быть больше нуля.");
            return;
        }

        cordsMap = new Vector2f(x, z);

    }

    private void removeGPS() {
        cordsMap = new Vector2f(0, 0);
    }

    @Override
    public String name() {
        return "gps";
    }

    @Override
    public String description() {
        return "Показывает стрелочку которая ведёт к координатам";
    }

    @Override
    public List<String> adviceMessage() {
        String commandPrefix = prefix.get();
        return List.of(commandPrefix + "gps add <x, z> - Проложить путь",
                commandPrefix + "gps off - Удалить GPS",
                "Пример: " + TextFormatting.RED + commandPrefix + "gps add 100 150"
        );
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        FunctionRegistry FunctionRegistry = Vredux.getInstance().getFunctionRegistry();
        SelfDestruct selfDestruct = FunctionRegistry.getSelfDestruct();

        if (selfDestruct.enabled || cordsMap.x == 0 && cordsMap.y == 0) {
            return;
        }

        Vector3d vec3d = new Vector3d(
                cordsMap.x + 0.5,
                100 + 0.5,
                cordsMap.y + 0.5
        );
        int dst = (int) Math.sqrt(Math.pow(vec3d.x - mc.player.getPosX(), 2) + Math.pow(vec3d.z - mc.player.getPosZ(), 2));

        String text = dst + "";
        Vector3d localVec = vec3d.subtract(mc.getRenderManager().info.getProjectedView());

        double x = localVec.getX();
        double z = localVec.getZ();

        double cos = MathHelper.cos((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
        double sin = MathHelper.sin((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);

        float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);

        double x2 = 80 * MathHelper.cos((float) Math.toRadians(angle)) + window.getScaledWidth() / 2f;
        double y2 = 80 * MathHelper.sin((float) Math.toRadians(angle)) + window.getScaledHeight() / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0);

        Fonts.montserrat.drawCenteredText(e.getMatrixStack(), text, 0, -15, -1, 6);

        GlStateManager.rotatef(angle, 0, 0, 1);

        int color = -1;


        ResourceLocation logo = new ResourceLocation("Vredux/images/gps.png");
        DisplayUtils.drawImage(logo, -9.0f, - 9.0f, 18.0f, 17.0f, ColorUtils.rgba(255,255,255, 120));

        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }
}
