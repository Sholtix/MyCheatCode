package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventPacket;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CUseEntityPacket;

@FunctionRegister(
        name = "Criticals",
        type = Category.Combat
)
public class Criticals extends Function {
    public static final BooleanSetting rage = new BooleanSetting("PvP Criticals", false);
    public final BooleanSetting legit = new BooleanSetting("Vehicle Criticals", false);
    public static ModeSetting mod = (new ModeSetting("Мод", "NCP", new String[]{"Grim Air", "NCP", "Matrix", "Grim 1.17.1", "StormStand", "LowHop"})).setVisible(() -> {
        return (Boolean)rage.get();
    });
    public static boolean active;
    public static boolean criticals;
    public static boolean criticalsncp;
    public static boolean criticalsmatrix;
    public static boolean criticalsair;
    public static boolean criticalsstand;
    public static boolean criticalslhop;

    public Criticals() {
        this.addSettings(new Setting[]{rage, mod});
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        if ((Boolean)rage.get()) {
            if (mod.is("Grim Air")) {
                criticalsair = true;
            } else {
                criticalsair = false;
            }

            if (mod.is("NCP")) {
                criticalsncp = true;
            } else {
                criticalsncp = false;
            }

            if (mod.is("Matrix")) {
                criticalsmatrix = true;
            } else {
                criticalsmatrix = false;
            }

            if (mod.is("Grim 1.17.1")) {
                criticals = true;
            } else {
                criticals = false;
            }

            if (mod.is("StormStand")) {
                criticalsstand = true;
            } else {
                criticalsstand = false;
            }

            if (mod.is("LowHop")) {
                criticalslhop = true;
            } else {
                criticalslhop = false;
            }
        }

        IPacket var3 = e.getPacket();
        if (var3 instanceof CUseEntityPacket) {
            CUseEntityPacket useEntityPacket = (CUseEntityPacket)var3;
            if ((Boolean)this.legit.get()) {
                Entity entity = useEntityPacket.getEntityFromWorld(mc.world);
                if (entity instanceof BoatEntity || entity instanceof MinecartEntity) {
                    for(int i = 0; i < 1; ++i) {
                        mc.playerController.attackEntity(mc.player, entity);
                    }
                }
            }
        }

    }

    public boolean onEnable() {
        super.onEnable();
        active = true;
        return false;
    }

    public void onDisable() {
        super.onDisable();
        active = false;
        criticals = false;
        criticalsmatrix = false;
        criticalsncp = false;
        criticalsair = false;
        criticalsstand = false;
    }
}
