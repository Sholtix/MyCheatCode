package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.functions.api.Category;
import wtf.sqwezz.functions.api.Function;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.display.ElementUpdater;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.math.StopWatch;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.math.vector.Vector4f;
import ru.hogoshi.Animation;

import java.util.List;

@RequiredArgsConstructor
public class ArraylistNextgenRenderer implements ElementRenderer, ElementUpdater {

                private int lastIndex;

                List<Function> list;


                StopWatch stopWatch = new StopWatch();
                @Subscribe
                public void update(EventUpdate e) {
                    if (stopWatch.isReached(1000)) {
                        list = Vredux.getInstance().getFunctionRegistry().getSorted(Fonts.sfui, 9 - 1.5f)
                                .stream()
                    .filter(m -> m.getCategory() != Category.Render)
                    .toList();
            stopWatch.reset();
        }
    }

    @Subscribe
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();
        float rounding = 6;
        float padding = 1.2f;
        float posX = 2;
        float posY = 20;
        int index = 0;

        if (list == null) return;

        float maxWidth = 0;
        float totalHeight = 0;

        for (Function f : list) {
            float fontSize = 6;
            Animation anim = f.getAnimation();
            float value = (float) anim.getValue();
            String text = f.getName();
            float textWidth = Fonts.sfui.getWidth(text, fontSize);

            if (value != 0) {
                float localFontSize = fontSize * value;
                float localTextWidth = textWidth * value;
                posY += (fontSize + padding * 2) * value;

                maxWidth = Math.max(maxWidth, localTextWidth + padding * 2);
                totalHeight += (fontSize + padding * 2) * value;
                index++;
            }
        }

        index = 0;
        posY = 20;
        for (Function f : list) {
            float fontSize = 5.5f;
            Animation anim = f.getAnimation();
            anim.update();

            float value = (float) anim.getValue();
            String text = f.getName();
            float textWidth = Fonts.sfui.getWidth(text, fontSize);

            if (value != 0) {
                float localFontSize = fontSize * value;
                float localTextWidth = textWidth * value;

                boolean isFirst = index == 0;
                boolean isLast = index == lastIndex;

                float localRounding = rounding;
                for (Function f2 : list.subList(list.indexOf(f) + 1, list.size())) {
                    if (f2.getAnimation().getValue() != 0) {
                        localRounding = isLast ? rounding : Math.min(textWidth - Fonts.sfui.getWidth(f2.getName(), fontSize), rounding);
                        break;
                    }
                }
                Vector4f rectVec = new Vector4f(isFirst ? rounding : 0, isLast ? rounding : 0, isFirst ? rounding : 0, isLast ? rounding : localRounding);

                DisplayUtils.drawRectVerticalW(posX - 1, posY, 1, localFontSize + padding * 2, ColorUtils.setAlpha(HUD.getColor(index), (int) (255 * value)), ColorUtils.setAlpha(HUD.getColor(index), (int) (255 * value)));
                DisplayUtils.drawRoundedRect(posX, posY - 0.5f, localTextWidth + padding * 2, localFontSize + padding * 2 + 1.5f, 0, ColorUtils.rgba(13,13,13, (int) (255 * value)));
                Fonts.sfMedium.drawText(ms, f.getName(), posX + padding, posY + padding, ColorUtils.setAlpha(HUD.getColor(index), (int) (255 * value)), localFontSize, 0.05f);

                posY += (fontSize + padding * 2) * value;
                index++;
            }
        }

        lastIndex = index - 1;
    }

}