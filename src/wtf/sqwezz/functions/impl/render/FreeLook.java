package wtf.sqwezz.functions.impl.render;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventMotion;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.impl.combat.KillAura;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import net.minecraft.client.settings.PointOfView;

@FunctionRegister(name = "FreeLook", type = Category.Render)
public class FreeLook extends Function {

    public BooleanSetting free = new BooleanSetting("Auto F5", false);
    public FreeLook(){
        addSettings( free);
    }

    private float startYaw, startPitch;
    @Override
    public boolean onEnable(){
        if(isFree()) {
            startYaw = mc.player.rotationYaw;
            startPitch = mc.player.rotationPitch;
        }
        super.onEnable();
        return false;
    }

    @Override
    public void onDisable(){
        if(isFree()) {
            mc.player.rotationYawOffset = Integer.MIN_VALUE;
            mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
            mc.player.rotationYaw = startYaw;
            mc.player.rotationPitch = startPitch;
        }
        super.onDisable();
    }


    @Subscribe
    public void onUpdate(EventUpdate e) {
        KillAura aura = Vredux.getInstance().getFunctionRegistry().getKillAura();
        if (free.get()) {
            if (! aura.isState() && aura.getTarget() == null) {
                mc.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
                mc.player.rotationYawOffset = startYaw;
            } else {
            }
        }
    }

    @Subscribe
    public void onMotion(EventMotion e){
        if(free.get()) {
            e.setYaw(startYaw);
            e.setPitch(startPitch);
            e.setOnGround(mc.player.isOnGround());
            mc.player.rotationYawHead = mc.player.rotationYawOffset;
            mc.player.renderYawOffset = mc.player.rotationYawOffset;
            mc.player.rotationPitchHead = startPitch;
        }
    }

    public boolean isFree(){
        return free.get();
    }
}