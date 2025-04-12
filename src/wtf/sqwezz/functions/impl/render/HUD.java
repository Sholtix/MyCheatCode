//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import jdk.jfr.Enabled;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.events.EventDisplay.Type;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeListSetting;
import wtf.sqwezz.ui.display.impl.*;
import wtf.sqwezz.ui.styles.StyleManager;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.render.ColorUtils;

@FunctionRegister(
        name = "HUD",
        type = Category.Render
)
public class HUD extends Function {
    public final ModeListSetting elements = new ModeListSetting("Элементы",
            new BooleanSetting("Ватермарка", true),
            new BooleanSetting("Эффекты", true),
            new BooleanSetting("ArrayList", true),
            new BooleanSetting("Список модерации", true),
            new BooleanSetting("Активные бинды", true),
            new BooleanSetting("Активный таргет", true),
            new BooleanSetting("Активный таргет 2", false),
            new BooleanSetting("Ивенты РВ", true),
            new BooleanSetting("Таймер-худ", true),
            new BooleanSetting("Броня", false));

    public final ModeListSetting nextgenWT = new ModeListSetting("Элементы ватермарки",
            new BooleanSetting("Ник", true),
            new BooleanSetting("Фпс", true),
            new BooleanSetting("Пинг", true)){
        public boolean isVisible() {
            return (Boolean)HUD.this.elements.getValueByName("Ватермарка").get();
        }

    };



    private final WatermarkNextgenRenderer watermarkNextgenRenderer = new WatermarkNextgenRenderer();
    private final ArraylistNextgenRenderer arraylistNextgenRenderer = new ArraylistNextgenRenderer();
    private final TimerNextgenRenderer timerNextgenRenderer;
    private final ArmorNextgenRenderer armorNextgenRenderer = new ArmorNextgenRenderer();
    private final PotionNextgenRenderer potionNextgenRenderer;
    private final KeyBindNextgenRenderer keyBindNextgenRenderer;
    private final TargetHUDNextgenRenderer2 targetHUDNextgenRenderer2;
    private final StaffListNextgenRenderer staffListRenderer;
    private final SchedulesNextgenRenderer schedulesNextgenRenderer;
    private final TargetHUDNextgenRenderer targetHUDNextgenRenderer;
    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (!mc.gameSettings.showDebugInfo) {
            if ((Boolean)this.elements.getValueByName("Список модерации").get()) {
                this.staffListRenderer.update(e);
            }

            if ((Boolean)this.elements.getValueByName("ArrayList").get()) {
                this.arraylistNextgenRenderer.update(e);
            }
             if ((Boolean)this.elements.getValueByName("Ивенты РВ").get()){
               this.schedulesNextgenRenderer.update(e);
}
        }
    }

    @Subscribe
    private void onDisplay(EventDisplay e) {
        if (!mc.gameSettings.showDebugInfo && e.getType() == Type.POST) {

            if ((Boolean)this.elements.getValueByName("Эффекты").get()) {
                this.potionNextgenRenderer.render(e);
            }
            if ((Boolean)this.elements.getValueByName("ArrayList").get()) {
                this.arraylistNextgenRenderer.render(e);
            }

            if ((Boolean)this.elements.getValueByName("Ватермарка").get()) {
                this.watermarkNextgenRenderer.render(e);
            }

            if ((Boolean)this.elements.getValueByName("Активные бинды").get()) {
                this.keyBindNextgenRenderer.render(e);
            }

            if ((Boolean)this.elements.getValueByName("Список модерации").get()) {
                this.staffListRenderer.render(e);
            }
            if ((Boolean)this.elements.getValueByName("Активный таргет").get()) {
                this.targetHUDNextgenRenderer2.render(e);
            }
            if ((Boolean)this.elements.getValueByName("Активный таргет 2").get()) {
                this.targetHUDNextgenRenderer.render(e);
            }

            if ((Boolean)this.elements.getValueByName("Броня").get()) {
                this.armorNextgenRenderer.render(e);
            }
            if ((Boolean)this.elements.getValueByName("Ивенты РВ").get()) {
                this.schedulesNextgenRenderer.render(e);
            }
            if ((Boolean)this.elements.getValueByName("Таймер-худ").get()) {
                this.timerNextgenRenderer.render(e);
            }


        }
    }

    public HUD() {
        Dragging potions = Vredux.getInstance().createDrag(this, "Potions", 278.0F, 5.0F);
        Dragging keyBinds = Vredux.getInstance().createDrag(this, "KeyBinds", 185.0F, 5.0F);
        Dragging dragging = Vredux.getInstance().createDrag(this, "TargetHUD", 74.0F, 128.0F);
        Dragging staffList = Vredux.getInstance().createDrag(this, "StaffList", 96.0F, 5.0F);
        Dragging schedules = Vredux.getInstance().createDrag(this, "Schedules", 96.0F, 5.0F);
        Dragging timer = Vredux.getInstance().createDrag(this, "Timer", 96.0F, 5.0F);
        Dragging targetHUD2 = Vredux.getInstance().createDrag(this, "TargetHUD2", 96.0F, 5.0F);
        targetHUDNextgenRenderer = new TargetHUDNextgenRenderer(targetHUD2);
        this.timerNextgenRenderer = new TimerNextgenRenderer(timer);
        this.schedulesNextgenRenderer = new SchedulesNextgenRenderer(schedules);
        this.potionNextgenRenderer = new PotionNextgenRenderer(potions);
        this.keyBindNextgenRenderer = new KeyBindNextgenRenderer(keyBinds);
        this.staffListRenderer = new StaffListNextgenRenderer(staffList);
        this.targetHUDNextgenRenderer2 = new TargetHUDNextgenRenderer2(dragging);
        this.addSettings(new Setting[]{this.elements, this.nextgenWT});
    }

    public static int getColor(int index) {
        StyleManager styleManager = Vredux.getInstance().getStyleManager();
        return ColorUtils.gradient(styleManager.getCurrentStyle().getFirstColor().getRGB(), styleManager.getCurrentStyle().getSecondColor().getRGB(), index * 16, 10);
    }

    public static int getColor(int index, float mult) {
        StyleManager styleManager = Vredux.getInstance().getStyleManager();
        return ColorUtils.gradient(styleManager.getCurrentStyle().getFirstColor().getRGB(), styleManager.getCurrentStyle().getSecondColor().getRGB(), (int)((float)index * mult), 10);
    }

    public static int getColor(int firstColor, int secondColor, int index, float mult) {
        return ColorUtils.gradient(firstColor, secondColor, (int)((float)index * mult), 10);
    }
}