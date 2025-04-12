package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.player.MoveUtils;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.network.play.client.CEntityActionPacket;

@FunctionRegister(name = "Pig Fly", type = Category.Movement)
public class BoatFly extends Function {
    final SliderSetting speed = new SliderSetting("Скорость", 10.f, 1.f, 20.f, 0.05f);
    final BooleanSetting noDismount = new BooleanSetting("Не вылезать", true);
    final BooleanSetting savePig = new BooleanSetting("Спасать свинью", true);
    final SliderSetting verticalSpeed = new SliderSetting("Скорость подъема", 0.5f, 0.1f, 1.0f, 0.05f); // Новая настройка для скорости подъема

    public BoatFly() {
        addSettings(speed, noDismount, savePig, verticalSpeed); // Добавьте вертикальную скорость в настройки
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (mc.player.getRidingEntity() != null) {
            if (mc.player.getRidingEntity() instanceof BoatEntity) {
                mc.player.getRidingEntity().motion.y = 0;
                if (mc.player.isPassenger()) {
                    // Автоматическое поднятие вверх с зависимостью от ползунка
                        mc.player.rotationPitchHead=0;
                    }

                    // Управление движением по горизонтали
                    if (MoveUtils.isMoving()) {
                        final double yaw = MoveUtils.getDirection(true);
                        mc.player.rotationPitchHead=0;
                        mc.player.getRidingEntity().motion.x = -Math.sin(yaw) * 0;
                        mc.player.getRidingEntity().motion.z = Math.cos(yaw) *0;
                    } else {
                        mc.player.getRidingEntity().motion.x = 0;
                        mc.player.getRidingEntity().motion.z = 0;
                    }
                mc.player.rotationPitchHead=0;
                    // Проверка на падение, чтобы не упасть
                    if ((!MoveUtils.isBlockUnder(4f) || mc.player.collidedHorizontally || mc.player.collidedVertically) && savePig.get()) {
                        mc.player.getRidingEntity().motion.y += 1;
                    }
                }
            }
        }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof CEntityActionPacket actionPacket) {
            if (!noDismount.get() || !(mc.player.getRidingEntity() instanceof BoatEntity)) return;
            CEntityActionPacket.Action action = actionPacket.getAction();
            if (action == CEntityActionPacket.Action.PRESS_SHIFT_KEY || action == CEntityActionPacket.Action.RELEASE_SHIFT_KEY)
                e.cancel();
        }
    }

    public boolean notStopRidding() {
        return this.isState() && noDismount.get();
    }
}