package wtf.sqwezz.utils.text;

import wtf.sqwezz.utils.render.ColorUtils;

import net.minecraft.util.text.*;

public class GradientUtil {

    public static StringTextComponent gradient(String message) {

        StringTextComponent text = new StringTextComponent("");
        for (int i = 0; i < message.length(); i++) {
            text.append(new StringTextComponent(String.valueOf(message.charAt(i))).setStyle(Style.EMPTY.setBold(true).setColor(new Color(ColorUtils.getColor(i)))));
        }

        return text;

    }
    public static ITextComponent white(String text) {
        return new StringTextComponent(text).setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.WHITE)));
    }
}