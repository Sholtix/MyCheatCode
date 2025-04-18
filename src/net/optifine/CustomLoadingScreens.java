package net.optifine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import net.optifine.util.WorldUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomLoadingScreens
{
    private static CustomLoadingScreen[] screens = null;
    private static int screensMinDimensionId = 0;

    public static CustomLoadingScreen getCustomLoadingScreen()
    {
        if (screens == null)
        {
            return null;
        }
        else
        {
            RegistryKey<World> registrykey = PacketThreadUtil.lastDimensionType;

            if (registrykey == null)
            {
                return null;
            }
            else
            {
                int i = WorldUtils.getDimensionId(registrykey);
                int j = i - screensMinDimensionId;
                CustomLoadingScreen customloadingscreen = null;

                if (j >= 0 && j < screens.length)
                {
                    customloadingscreen = screens[j];
                }

                return customloadingscreen;
            }
        }
    }

    public static void update()
    {
        screens = null;
        screensMinDimensionId = 0;
        Pair<CustomLoadingScreen[], Integer> pair = parseScreens();
        screens = pair.getLeft();
        screensMinDimensionId = pair.getRight();
    }

    private static Pair<CustomLoadingScreen[], Integer> parseScreens()
    {
        // Убираем загрузку изображений
        String s = "optifine/gui/loading/background";
        String s1 = ".png";
        String[] astring = ResUtils.collectFiles(s, s1);
        Map<Integer, String> map = new HashMap<>();

        for (int i = 0; i < astring.length; ++i)
        {
            String s2 = astring[i];
            String s3 = StrUtils.removePrefixSuffix(s2, s, s1);
            int j = Config.parseInt(s3, Integer.MIN_VALUE);

            if (j == Integer.MIN_VALUE)
            {
                warn("Invalid dimension ID: " + s3 + ", path: " + s2);
            }
            else
            {
                map.put(j, s2);
            }
        }

        Set<Integer> set = map.keySet();
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        Arrays.sort((Object[])ainteger);

        if (ainteger.length <= 0)
        {
            return new ImmutablePair<>((CustomLoadingScreen[])null, 0);
        }
        else
        {
            // Убираем создание объектов CustomLoadingScreen
            int k = ainteger[0];
            int l = ainteger[ainteger.length - 1];
            int i1 = l - k + 1;
            CustomLoadingScreen[] acustomloadingscreen = new CustomLoadingScreen[i1];

            // Вместо загрузки экранов, можно вернуть пустые экраны или null
            for (int j1 = 0; j1 < ainteger.length; ++j1)
            {
                Integer integer = ainteger[j1];
                // acustomloadingscreen[integer - k] = CustomLoadingScreen.parseScreen(s4, integer, properties);
                acustomloadingscreen[integer - k] = null; // или new CustomLoadingScreen(); если нужно
            }

            return new ImmutablePair<>(acustomloadingscreen, k);
        }
    }

    public static void warn(String str)
    {
        Config.warn("CustomLoadingScreen: " + str);
    }

    public static void dbg(String str)
    {
        Config.dbg("CustomLoadingScreen: " + str);
    }
}