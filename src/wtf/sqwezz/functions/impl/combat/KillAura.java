package wtf.sqwezz.functions.impl.combat;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.command.friends.FriendStorage;
import wtf.sqwezz.events.EventInput;
import wtf.sqwezz.events.EventMotion;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import wtf.sqwezz.functions.settings.Setting;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.functions.settings.impl.BooleanSetting;
import wtf.sqwezz.functions.settings.impl.ModeListSetting;
import wtf.sqwezz.functions.settings.impl.ModeSetting;
import wtf.sqwezz.functions.settings.impl.SliderSetting;
import wtf.sqwezz.utils.math.SensUtils;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.player.InventoryUtil;
import wtf.sqwezz.utils.player.MouseUtil;
import wtf.sqwezz.utils.player.MoveUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import static java.lang.Math.abs;
import static net.minecraft.util.math.MathHelper.clamp;

// Hello dalbaeb osel!
// yeeaaa, lets go!

// Пельменй поедатель
// твоей матери ебатель (я, я, я)
// кайнер Поедатель(да, да, да)
// я его матери ебатель(я, я, я)

@FunctionRegister(
        name = "Aura",
        type = Category.Combat
)
public class KillAura extends Function {
    private final ModeSetting type = new ModeSetting("Тип", "Grim", "Grim", "HolyWorld", "FTsnap", "Плавная", "GrimNew");
    @Getter
    private final ModeSetting ElType = new ModeSetting("Тип элитра таргета", "Старый", "Старый", "Новый");
    public static Vector2f rotate = new Vector2f(0.0F, 0.0F);

    private final SliderSetting attackRange = new SliderSetting("Дистанция аттаки", 3.0F, 2.5F, 6.0F, 0.1F);

    private final SliderSetting elRange = new SliderSetting("Дист аттаки на элитре", 3f, 1f, 5f, 0.1f);
    private final SliderSetting elDist = new SliderSetting("Дистанция на элитре", 0f, 0f, 50f, 1f);

    private final BooleanSetting speedrot = (new BooleanSetting("ускорить ротацию", false)).setVisible(() -> {
        return this.type.is("GrimNew");
    });
    private final BooleanSetting head = (new BooleanSetting("Плавная тряска", false)).setVisible(() -> {
        return this.type.is("GrimNew");
    });

    private final BooleanSetting HolyWorld = (new BooleanSetting("Тряска Тела", false)).setVisible(() -> {
        return this.type.is("HolyWorld");
    });

    final ModeListSetting targets = new ModeListSetting("Таргеты", new BooleanSetting[]{new BooleanSetting("Игроки", true), new BooleanSetting("Голые", true), new BooleanSetting("Мобы", false), new BooleanSetting("Животные", false), new BooleanSetting("Друзья", false), new BooleanSetting("Голые невидимки", true), new BooleanSetting("Невидимки", true)});
    public static ModeListSetting options = new ModeListSetting("Опции", new BooleanSetting[]{new BooleanSetting("Только криты", true), new BooleanSetting("Ломать щит", true), new BooleanSetting("Отжимать щит", true), new BooleanSetting("Ускорять ротацию при атаке", false), new BooleanSetting("Синхронизировать атаку с ТПС", false), new BooleanSetting("Фокусировать одну цель", true), new BooleanSetting("Коррекция движения", true)});
    final ModeSetting correctionType = new ModeSetting("Тип коррекции", "Сфокусированный", new String[]{"Сфокусированный", "Незаметный"});
   // private final BooleanSetting criticals = new BooleanSetting("Criticals", false);
    private final BooleanSetting checkWallObstruction = new BooleanSetting("Не бить через стену", true);
    private final StopWatch stopWatch = new StopWatch();
    public static Vector2f rotateVector = new Vector2f(0.0F, 0.0F);
    @Getter
    public static LivingEntity target;
    private Entity selected;
    int ticks = 0;
    boolean isRotated;
    final AutoPotion autoPotion;
    float lastYaw;
    float lastPitch;
    float speedX = 0.0F;
    float speedY = 0.0F;
    StopWatch yawUpdate = new StopWatch();
    StopWatch pitchUpdate = new StopWatch();

    public KillAura(AutoPotion autoPotion) {
        this.autoPotion = autoPotion;
        this.addSettings(new Setting[]{this.type, this.ElType, this.attackRange, this.elRange, elDist, this.head, this.speedrot, this.HolyWorld, this.targets, this.options, this.correctionType, this.checkWallObstruction});
    }

    @Subscribe
    public void onInput(EventInput eventInput) {
        if ((Boolean) this.options.getValueByName("Коррекция движения").get() && this.correctionType.is("Незаметная") && this.target != null) {
            Minecraft var10000 = mc;
            if (Minecraft.player != null) {
                MoveUtils.fixMovement(eventInput, this.rotateVector.x);
            }
        }

    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if ((Boolean) this.options.getValueByName("Фокусировать одну цель").get() && (this.target == null || !this.isValid(this.target)) || !(Boolean) this.options.getValueByName("Фокусировать одну цель").get()) {
            this.updateTarget();
        }

        if (this.target != null && (!this.autoPotion.isState() || !this.autoPotion.isActive())) {
            this.isRotated = false;
            if (this.shouldPlayerFalling() && this.stopWatch.hasTimeElapsed()) {
                this.updateAttack();
                this.ticks = 2;
            }

            if (this.type.is("FTsnap")) {
                if (this.ticks > 1) {
                    updateRotation(true, 180, 90);
                    --this.ticks;
                } else {
                    this.reset();
                }
            } else if (!this.isRotated) {
                this.updateRotation(false, 80.0F, 35.0F);
            }

            if (this.shouldPlayerFalling() && this.stopWatch.hasTimeElapsed()) {
                this.updateAttack();
                this.ticks = 2;
            }
        } else {
            this.stopWatch.setLastMS(0L);
            this.reset();
        }

    }


    @Subscribe
    private void onWalking(EventMotion e) {
        if (this.target != null && (!this.autoPotion.isState() || !this.autoPotion.isActive())) {
            float yaw = this.rotateVector.x;
            float pitch = this.rotateVector.y;
            e.setYaw(yaw);
            e.setPitch(pitch);
            Minecraft.player.rotationYawHead = yaw;
            ;
            Minecraft.player.renderYawOffset = yaw;
            //    mc.player.renderYawOffset = PlayerUtils.calculateCorrectYawOffset(yaw);
            Minecraft.player.rotationPitchHead = pitch;
        }
    }

    private void updateTarget() {
        List<LivingEntity> targets = new ArrayList();
        Minecraft var10000 = mc;
        Iterator var2 = Minecraft.world.getAllEntities().iterator();

        while (var2.hasNext()) {
            Entity entity = (Entity) var2.next();
            if (entity instanceof LivingEntity living) {
                if (this.isValid(living)) {
                    targets.add(living);
                }
            }
        }

        if (targets.isEmpty()) {
            this.target = null;
        } else if (targets.size() == 1) {
            this.target = (LivingEntity) targets.get(0);
        } else {
            targets.sort(Comparator.comparingDouble((object) -> {
                if (object instanceof PlayerEntity player) {
                    return -this.getEntityArmor(player);
                } else if (object instanceof LivingEntity base) {
                    return (double) (-base.getTotalArmorValue());
                } else {
                    return 0.0;
                }
            }).thenComparing((object, object2) -> {
                double d2 = this.getEntityHealth((LivingEntity) object);
                double d3 = this.getEntityHealth((LivingEntity) object2);
                return Double.compare(d2, d3);
            }).thenComparing((object, object2) -> {
                double d2 = (double) Minecraft.player.getDistance((LivingEntity) object);
                double d3 = (double) Minecraft.player.getDistance((LivingEntity) object2);
                return Double.compare(d2, d3);
            }));
            this.target = (LivingEntity) targets.get(0);
        }
    }

    private boolean LookTarget(LivingEntity target) {
        Vector3d playerDirection = mc.player.getLook(0.0F);
        Vector3d targetDirection = target.getPositionVec().subtract(mc.player.getEyePosition(1.0F)).normalize();
        double angle = Math.toDegrees(Math.acos(playerDirection.dotProduct(targetDirection)));
        return angle <= 40.5;
    }


    private void updateRotation(boolean attack, float rotationYawSpeed, float rotationPitchSpeed) {
        if (type.is("FTsnap") && !LookTarget(target)) {
            return;
        }

        Vector3d var10000 = this.target.getPositionVec().add(0.0, clamp(1.1, 0.3, (double) this.target.getHeight()), 0.0);
        Vector3d vec = var10000.subtract(Minecraft.player.getEyePosition(1.0F));
        this.isRotated = true;
        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));
        float yawDelta = MathHelper.wrapDegrees(yawToTarget - this.rotateVector.x);
        int roundYawDelta = (int) abs(yawDelta);
        float pitchDelta = MathHelper.wrapDegrees(pitchToTarget - this.rotateVector.y);
        int roundedYaw = (int) yawDelta;
        float clampedYaw;
        float clampedPitch;
        float yaw;
        float pitch;
        float gcd;
        int roundYawDelta1 = (int) abs(yawDelta);
        int roundPitchDelta1 = (int) abs(pitchDelta);
        int roundPitchDelta = (int) abs(pitchDelta);

        Minecraft var17;
        switch ((String) this.type.get()) {
            case "Grim":
                clampedYaw = Math.min(Math.max(abs(yawDelta), 1.0F), rotationYawSpeed);
                clampedPitch = Math.min(Math.max(abs(pitchDelta), 1.0F), rotationPitchSpeed);
                if (attack && this.selected != this.target && (Boolean) this.options.getValueByName("Ускорять ротацию при атаке").get()) {
                    clampedPitch = Math.max(abs(pitchDelta), 1.0F);
                } else {
                    clampedPitch /= 3.0F;
                }

                if (abs(clampedYaw - this.lastYaw) <= 3.0F) {
                    clampedYaw = this.lastYaw + 3.1F;
                }

                yaw = this.rotateVector.x + (yawDelta > 0.0F ? clampedYaw : -clampedYaw);
                pitch = clamp(this.rotateVector.y + (pitchDelta > 0.0F ? clampedPitch : -clampedPitch), -89.0F, 89.0F);
                gcd = SensUtils.getGCDValue();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                this.lastYaw = clampedYaw;
                this.lastPitch = clampedPitch;
                if ((Boolean) this.options.getValueByName("Коррекция движения").get()) {
                    Minecraft.player.rotationYawOffset = yaw;
                }
        }
        switch ((String) this.type.get()) {
            case "Плавная" -> {
                clampedYaw = Math.min(Math.max(abs(yawDelta), 0.4f), rotationYawSpeed);
                clampedPitch = Math.min(Math.max(abs(pitchDelta), 0.4f), rotationPitchSpeed);

                if (attack && selected != target && options.getValueByName("Ускорять ротацию при атаке").get()) {
                    clampedPitch = Math.max(abs(pitchDelta), 0.4f);
                } else {
                    clampedPitch /= 9f;
                }


                if (abs(clampedYaw - this.lastYaw) <= 0.0f) {
                    clampedYaw = this.lastYaw + 0.1f;
                }

                yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);


                gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;


                rotateVector = new Vector2f(yaw, pitch);
                lastYaw = clampedYaw;
                lastPitch = clampedPitch;
                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }
        }

        switch ((String) this.type.get()) {
            case "Matrix" -> {
                double yawSpeed, pitchSpeed;

                if (MouseUtil.getMouseOver(target, rotateVector.x, rotateVector.y, attackDistance()) != null) {
                    yawSpeed = MathUtil.randomWithUpdate(5, 25, 200, yawUpdate);
                    pitchSpeed = 0;
                } else {
                    yawSpeed = MathUtil.randomWithUpdate(19, 113, 60, yawUpdate);
                    pitchSpeed = MathUtil.randomWithUpdate(2, 17, 40, pitchUpdate);
                }

                clampedYaw = (float) Math.min(Math.max(roundYawDelta, 1.0f), yawSpeed);
                clampedPitch = (float) Math.min(Math.max(roundPitchDelta * 0.33f, 1.0f), pitchSpeed);

                yaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
                pitch = clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -90, 90);

                gcd = SensUtils.getGCDValue();
                yaw -= (yaw - rotateVector.x) % gcd;
                pitch -= (pitch - rotateVector.y) % gcd;

                rotateVector = new Vector2f(yaw, pitch);

                lastYaw = clampedYaw;
                lastPitch = clampedPitch;

                if (options.getValueByName("Коррекция движения").get()) {
                    mc.player.rotationYawOffset = yaw;
                }
            }
            case "HolyWorld" -> {
                clampedYaw = Math.min(Math.max(abs(yawDelta), 1.0F), rotationYawSpeed);
                clampedPitch = Math.min(Math.max(abs(pitchDelta), 1.0F), rotationPitchSpeed);
                clampedPitch /= 1.0F;
                clampedYaw /= 1.0F;
                if (this.HolyWorld.get() && abs(clampedYaw - this.lastYaw) <= 5.0F) {
                    clampedYaw = this.lastYaw + 5.1F;
                }

                yaw = this.rotateVector.x + (yawDelta > 0.0F ? clampedYaw : -clampedYaw);
                pitch = clamp(this.rotateVector.y + (pitchDelta > 0.0F ? clampedPitch : -clampedPitch), -89.0F, 89.0F);
                gcd = SensUtils.getGCD();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                this.lastYaw = clampedYaw;
                this.lastPitch = clampedPitch;
                if (this.options.getValueByName("Коррекция движения").get()) {
                    Minecraft.player.rotationYawOffset = yaw;
                }
            }
            case "FTsnap" -> {
                yaw = this.rotateVector.x + (float) roundedYaw;
                pitch = clamp(this.rotateVector.y + pitchDelta, -90.0F, 90.0F);
                gcd = SensUtils.getGCDValue();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                if (this.options.getValueByName("Коррекция движения").get()) {
                    Minecraft.player.rotationYawOffset = yaw;
                }
            }
            case "GrimNew" -> {
                clampedYaw = Math.min(Math.max(abs(yawDelta), 1.0F), rotationYawSpeed);
                clampedPitch = Math.min(Math.max(abs(pitchDelta), 1.0F), rotationPitchSpeed);
                if (this.speedrot.get()) {
                    clampedPitch /= 0.85F;
                    clampedYaw /= 0.85F;
                }

                if (!this.speedrot.get()) {
                    clampedPitch /= 2.0F;
                    clampedYaw /= 2.0F;
                }

                if (this.head.get() && abs(clampedYaw - this.lastYaw) <= 4.0F) {
                    clampedYaw = this.lastYaw + 4.1F;
                }
                yaw = this.rotateVector.x + (yawDelta > 0.0F ? clampedYaw : -clampedYaw);
                pitch = clamp(this.rotateVector.y + (pitchDelta > 0.0F ? clampedPitch : -clampedPitch), -90.0F, 90.0F);
                gcd = SensUtils.getGCD();
                yaw -= (yaw - this.rotateVector.x) % gcd;
                pitch -= (pitch - this.rotateVector.y) % gcd;
                this.rotateVector = new Vector2f(yaw, pitch);
                this.lastYaw = clampedYaw;
                this.lastPitch = clampedPitch;
                if (this.options.getValueByName("Коррекция движения").get()) {
                    Minecraft.player.rotationYawOffset = yaw;
                }
            }
        }
    }

    private void updateAttack() {
        if (type.is("FTsnap") && !LookTarget(target)) {
            return;
        }

        if (!(Boolean) this.checkWallObstruction.get() || this.canSeeThroughWall(this.target)) {
            this.selected = MouseUtil.getMouseOver(this.target, this.rotateVector.x, this.rotateVector.y, (double) (Float) this.attackRange.get());
            if ((Boolean) this.options.getValueByName("Ускорять ротацию при атаке").get()) {
                this.updateRotation(true, 60.0F, 35.0F);
            }


            if (this.selected == null || this.selected != this.target) {

                if (!Minecraft.player.isElytraFlying()) {
                    return;
                }
            }

            if (Minecraft.player.isBlocking() && (Boolean) this.options.getValueByName("Отжимать щит").get()) {

                mc.playerController.onStoppedUsingItem(Minecraft.player);
            }

            this.stopWatch.setLastMS(500L);

            mc.playerController.attackEntity(Minecraft.player, this.target);

            Minecraft.player.swingArm(Hand.MAIN_HAND);
            LivingEntity var2 = this.target;
            if (var2 instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) var2;
                if ((Boolean) this.options.getValueByName("Ломать щит").get()) {
                    this.breakShieldPlayer(player);
                }
            }

        }
    }

    public float attackDistance() {
        if (!mc.player.isSwimming()) {
            return 3.6f;
        } else {
            return 3.0f;
        }
    }

    private boolean canSeeThroughWall(Entity entity) {
        Minecraft var10000 = mc;
        ClientWorld var3 = Minecraft.world;
        Minecraft var10003 = mc;
        Vector3d var4 = Minecraft.player.getEyePosition(1.0F);
        Vector3d var10004 = entity.getEyePosition(1.0F);
        Minecraft var10007 = mc;
        RayTraceResult result = var3.rayTraceBlocks(new RayTraceContext(var4, var10004, BlockMode.COLLIDER, FluidMode.NONE, Minecraft.player));
        return result == null || result.getType() == Type.MISS;
    }

    public boolean shouldPlayerFalling() {
        Minecraft var10000;
        boolean var3;
        label46:
        {
            label45:
            {
                var10000 = mc;
                if (Minecraft.player.isInWater()) {
                    var10000 = mc;
                    if (Minecraft.player.areEyesInFluid(FluidTags.WATER)) {
                        break label45;
                    }
                }

                var10000 = mc;
                if (!Minecraft.player.isInLava()) {
                    var10000 = mc;
                    if (!Minecraft.player.isOnLadder()) {
                        var10000 = mc;
                        if (!Minecraft.player.isPassenger()) {
                            var10000 = mc;
                            if (!Minecraft.player.abilities.isFlying) {
                                var3 = false;
                                break label46;
                            }
                        }
                    }
                }
            }

            var3 = true;
        }

        boolean cancelReason = var3;
        boolean onSpace = Minecraft.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown();
        float attackStrength = Minecraft.player.getCooledAttackStrength((Boolean) this.options.getValueByName("Синхронизировать атаку с ТПС").get() ? Vredux.getInstance().getTpsCalc().getAdjustTicks() : 1.5F);
        if (attackStrength < 0.92F) {
            return false;
        } else if (!cancelReason && (Boolean) this.options.getValueByName("Только криты").get()) {
            if (!Minecraft.player.isOnGround()) {
                if (Minecraft.player.fallDistance > 0.0F) {
                    var3 = true;
                    return var3;
                }
            }

            var3 = false;
            return var3;
        } else {
            return true;
        }
    }

    private boolean isValid(LivingEntity entity) {
        float extra = 0f;
        float dist = 0f;
        if (entity instanceof ClientPlayerEntity) return false;

        if (type.is("Кастом")) {
            if (mc.player.isElytraFlying() && !mc.player.isInWater()) {
                extra = elDist.get();
            } else {
                extra = elDist.get();
            }
        }
        if (!type.is("Кастом")) {
            if (mc.player.isElytraFlying() && !mc.player.isInWater()) {
                extra = elDist.get();
            }
        }
        if (mc.player.isElytraFlying() && !mc.player.inWater) {
            dist = elRange.get();
        } else {
            dist = attackRange.get();
        }

        if (entity.ticksExisted < 3) return false;
        if (mc.player.getDistanceEyePos(entity) > extra + dist) return false;
        if (mc.player.getDistance(entity) > extra + dist) return false;

        if (mc.player.getDistanceEyePos(entity) > (double) dist + (double) extra) {
            return false;
        }

        if (entity instanceof PlayerEntity p) {
            if (AntiBot.isBot(entity)) {
                return false;
            }
            if (!targets.getValueByName("Друзья").get() && FriendStorage.isFriend(p.getName().getString())) {
                return false;
            }
            if (p.getName().getString().equalsIgnoreCase(mc.player.getName().getString())) return false;
        }

        if (entity instanceof PlayerEntity && !targets.getValueByName("Игроки").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.getTotalArmorValue() == 0 && !targets.getValueByName("Голые").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && entity.getTotalArmorValue() == 0 && !targets.getValueByName("Голые невидимки").get()) {
            return false;
        }
        if (entity instanceof PlayerEntity && entity.isInvisible() && !targets.getValueByName("Невидимки").get()) {
            return false;
        }

        if (entity instanceof MonsterEntity && !targets.getValueByName("Мобы").get()) {
            return false;
        }
        if (entity instanceof AnimalEntity && !targets.getValueByName("Животные").get()) {
            return false;
        }

        return !entity.isInvulnerable() && entity.isAlive() && !(entity instanceof ArmorStandEntity);
    }
    private void breakShieldPlayer(PlayerEntity entity) {
        if (entity.isBlocking()) {
            int invSlot = InventoryUtil.getInstance().getAxeInInventory(false);
            int hotBarSlot = InventoryUtil.getInstance().getAxeInInventory(true);
            Minecraft var10000;
            Minecraft var10001;
            Minecraft var10003;
            if (hotBarSlot == -1 && invSlot != -1) {
                int bestSlot = InventoryUtil.getInstance().findBestSlotInHotBar();
                Minecraft var10005 = mc;
                mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, Minecraft.player);
                int var10002 = bestSlot + 36;
                var10005 = mc;
                mc.playerController.windowClick(0, var10002, 0, ClickType.PICKUP, Minecraft.player);
                var10000 = mc;
                Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                var10001 = mc;
                mc.playerController.attackEntity(Minecraft.player, entity);
                var10000 = mc;
                Minecraft.player.swingArm(Hand.MAIN_HAND);
                var10000 = mc;
                var10003 = mc;
                Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(Minecraft.player.inventory.currentItem));
                var10002 = bestSlot + 36;
                var10005 = mc;
                mc.playerController.windowClick(0, var10002, 0, ClickType.PICKUP, Minecraft.player);
                var10005 = mc;
                mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, Minecraft.player);
            }

            if (hotBarSlot != -1) {
                var10000 = mc;
                Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                var10001 = mc;
                mc.playerController.attackEntity(Minecraft.player, entity);
                var10000 = mc;
                Minecraft.player.swingArm(Hand.MAIN_HAND);
                var10000 = mc;
                var10003 = mc;
                Minecraft.player.connection.sendPacket(new CHeldItemChangePacket(Minecraft.player.inventory.currentItem));
            }
        }

    }

    private void reset() {
        if ((Boolean)this.options.getValueByName("Коррекция движения").get()) {
            Minecraft var10000 = mc;
            Minecraft.player.rotationYawOffset = -2.14748365E9F;
        }

        Minecraft var10003 = mc;
        Minecraft var10004 = mc;
        this.rotateVector = new Vector2f(Minecraft.player.rotationYaw, Minecraft.player.rotationPitch);
    }

    public boolean onEnable() {
        super.onEnable();
        this.reset();
        this.target = null;
        return false;
    }

    public void onDisable() {
        super.onDisable();
        this.reset();
        this.stopWatch.setLastMS(0L);
        this.target = null;
    }

    private double getEntityArmor(PlayerEntity entityPlayer2) {
        double d2 = 0.0;

        for(int i2 = 0; i2 < 4; ++i2) {
            ItemStack is = (ItemStack)entityPlayer2.inventory.armorInventory.get(i2);
            if (is.getItem() instanceof ArmorItem) {
                d2 += this.getProtectionLvl(is);
            }
        }

        return d2;
    }

    private double getProtectionLvl(ItemStack stack) {
        Item var3 = stack.getItem();
        if (var3 instanceof ArmorItem i) {
            double damageReduceAmount = (double)i.getDamageReduceAmount();
            if (stack.isEnchanted()) {
                damageReduceAmount += (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
            }

            return damageReduceAmount;
        } else {
            return 0.0;
        }
    }

    private double getEntityHealth(LivingEntity ent) {
        if (ent instanceof PlayerEntity player) {
            return (double)(player.getHealth() + player.getAbsorptionAmount()) * (this.getEntityArmor(player) / 20.0);
        } else {
            return (double)(ent.getHealth() + ent.getAbsorptionAmount());
        }
    }

    public ModeSetting getType() {
        return this.type;
    }

    public ModeListSetting getOptions() {
        return this.options;
    }

    public StopWatch getStopWatch() {
        return this.stopWatch;
    }
}