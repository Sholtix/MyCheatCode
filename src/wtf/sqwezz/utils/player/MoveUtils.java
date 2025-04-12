//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wtf.sqwezz.utils.player;

import wtf.sqwezz.events.EventInput;
import wtf.sqwezz.events.MovingEvent;
import wtf.sqwezz.utils.client.IMinecraft;
import net.minecraft.block.AirBlock;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class MoveUtils implements IMinecraft {
    public static boolean isMoving() {
        boolean var0;
        if (mc.player.movementInput.moveForward == 0.0F) {
            MovementInput var10000 = mc.player.movementInput;
            if (MovementInput.moveStrafe == 0.0F) {
                var0 = false;
                return var0;
            }
        }

        var0 = true;
        return var0;
    }

    public static boolean isBlockUnder() {
        for(int i = (int)(mc.player.getPosY() - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.player.getPosX(), (double)i, mc.player.getPosZ());
            if (!(mc.world.getBlockState(pos).getBlock() instanceof AirBlock)) {
                return true;
            }
        }

        return false;
    }

    public static void setSpeed(double speed) {
        float f = mc.player.movementInput.moveForward;
        MovementInput var10000 = mc.player.movementInput;
        float f1 = MovementInput.moveStrafe;
        float f2 = mc.player.rotationYaw;
        if (f == 0.0F && f1 == 0.0F) {
            mc.player.motion.x = 0.0;
            mc.player.motion.z = 0.0;
        } else if (f != 0.0F) {
            if (f1 >= 1.0F) {
                f2 += (float)(f > 0.0F ? -35 : 35);
                f1 = 0.0F;
            } else if (f1 <= -1.0F) {
                f2 += (float)(f > 0.0F ? 35 : -35);
                f1 = 0.0F;
            }

            if (f > 0.0F) {
                f = 1.0F;
            } else if (f < 0.0F) {
                f = -1.0F;
            }
        }

        double d0 = Math.cos(Math.toRadians((double)(f2 + 90.0F)));
        double d1 = Math.sin(Math.toRadians((double)(f2 + 90.0F)));
        mc.player.motion.x = (double)f * speed * d0 + (double)f1 * speed * d1;
        mc.player.motion.z = (double)f * speed * d1 - (double)f1 * speed * d0;
    }

    public static void fixMovement(EventInput event, float yaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.isElytraFlying() ? yaw : mc.player.rotationYaw, (double)forward, (double)strafe)));
        if (forward != 0.0F || strafe != 0.0F) {
            float closestForward = 0.0F;
            float closestStrafe = 0.0F;
            float closestDifference = Float.MAX_VALUE;

            for(float predictedForward = -1.0F; predictedForward <= 1.0F; ++predictedForward) {
                for(float predictedStrafe = -1.0F; predictedStrafe <= 1.0F; ++predictedStrafe) {
                    if (predictedStrafe != 0.0F || predictedForward != 0.0F) {
                        double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, (double)predictedForward, (double)predictedStrafe)));
                        double difference = Math.abs(angle - predictedAngle);
                        if (difference < (double)closestDifference) {
                            closestDifference = (float)difference;
                            closestForward = predictedForward;
                            closestStrafe = predictedStrafe;
                        }
                    }
                }
            }

            event.setForward(closestForward);
            event.setStrafe(closestStrafe);
        }
    }

    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0F;
        }

        float forward = 1.0F;
        if (moveForward < 0.0) {
            forward = -0.5F;
        } else if (moveForward > 0.0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0F * forward;
        }

        if (moveStrafing < 0.0) {
            rotationYaw += 90.0F * forward;
        }

        return Math.toRadians((double)rotationYaw);
    }

    public static double getMotion() {
        return Math.hypot(mc.player.getMotion().x, mc.player.getMotion().z);
    }

    public static void setMotion(double speed) {
        if (isMoving()) {
            double yaw = getDirection(true);
            mc.player.setMotion(-Math.sin(yaw) * speed, mc.player.motion.y, Math.cos(yaw) * speed);
        }
    }

    public static boolean isBlockUnder(float under) {
        if (mc.player.getPosY() < 0.0) {
            return false;
        } else {
            AxisAlignedBB aab = mc.player.getBoundingBox().offset(0.0, (double)(-under), 0.0);
            return mc.world.getCollisionShapes(mc.player, aab).toList().isEmpty();
        }
    }

    public static double getDirection(boolean toRadians) {
        float rotationYaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0F) {
            rotationYaw += 180.0F;
        }

        float forward = 1.0F;
        if (mc.player.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0.0F) {
            rotationYaw -= 90.0F * forward;
        }

        if (mc.player.moveStrafing < 0.0F) {
            rotationYaw += 90.0F * forward;
        }

        return toRadians ? Math.toRadians((double)rotationYaw) : (double)rotationYaw;
    }

    public static double getSpeed() {
        return Math.hypot(mc.player.getMotion().x, mc.player.getMotion().z);
    }

    private MoveUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static class MoveEvent {
        public MoveEvent() {
        }

        public static void setMoveMotion(MovingEvent move, double motion) {
            double forward = (double)IMinecraft.mc.player.movementInput.moveForward;
            MovementInput var10000 = IMinecraft.mc.player.movementInput;
            double strafe = (double)MovementInput.moveStrafe;
            float yaw = IMinecraft.mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                move.getMotion().x = 0.0;
                move.getMotion().z = 0.0;
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += (float)(forward > 0.0 ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += (float)(forward > 0.0 ? 45 : -45);
                    }

                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }

                move.getMotion().x = forward * motion * (double)MathHelper.cos((float)Math.toRadians((double)(yaw + 90.0F))) + strafe * motion * (double)MathHelper.sin((float)Math.toRadians((double)(yaw + 90.0F)));
                move.getMotion().z = forward * motion * (double)MathHelper.sin((float)Math.toRadians((double)(yaw + 90.0F))) - strafe * motion * (double)MathHelper.cos((float)Math.toRadians((double)(yaw + 90.0F)));
            }

        }
    }
}
