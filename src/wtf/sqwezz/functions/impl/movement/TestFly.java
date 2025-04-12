package wtf.sqwezz.functions.impl.movement;

import com.google.common.eventbus.Subscribe;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.functions.api.FunctionRegister;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.client.Minecraft.getInstance;

@FunctionRegister(name = "AntiWeb", type = Category.Player)
public class TestFly extends Function {

    private BlockPos lastPlayerPos = null;

    @Subscribe
    public void onPlayerTick(EventUpdate event) {
        World world = getInstance().player.world;
        BlockPos playerPos = getInstance().player.getPosition();
        if (!playerPos.equals(lastPlayerPos)) {

            restoreCobwebs(world, lastPlayerPos);

            replaceCobwebsWithWater(world, playerPos);

            lastPlayerPos = playerPos;
        }
    }

    private void replaceCobwebsWithWater(World world, BlockPos playerPos) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = new BlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
                        world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
                    }
                }
            }
        }
    }
    private void restoreCobwebs(World world, BlockPos playerPos) {
        if (playerPos != null) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos pos = new BlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                        if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
                            world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}