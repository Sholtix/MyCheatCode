package wtf.sqwezz.ui.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.display.ElementUpdater;
import wtf.sqwezz.ui.schedules.Schedule;
import wtf.sqwezz.ui.schedules.SchedulesManager;
import wtf.sqwezz.ui.schedules.TimeType;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.Scissor;
import wtf.sqwezz.utils.render.font.Fonts;
import wtf.sqwezz.utils.text.GradientUtil;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.math.Vector4i;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SchedulesNextgenRenderer implements ElementRenderer, ElementUpdater {
    final Dragging dragging;
    float width;
    float height;
    final SchedulesManager schedulesManager = new SchedulesManager();
    final TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
    List<Schedule> activeSchedules = new ArrayList<>();
    private static final int MINUTES_IN_DAY = 1440;
    boolean sorted = false;

    @Override
    public void update(EventUpdate e) {
        activeSchedules = schedulesManager.getSchedules();
        if (!sorted) {
            this.activeSchedules.sort(Comparator.comparingInt(schedule -> (int) -Fonts.sfMedium.getWidth(schedule.getName(), 6.5f)));
            sorted = false;
        }
    }

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 6.0f;
        float padding = 3;
        ITextComponent name = GradientUtil.gradient("Schedules");
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();

        drawStyledRect(posX, posY, width, height, 4);
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        wtf.sqwezz.utils.font.Fonts.msSemiBold[16].drawCenteredString(ms, name, posX + width / 2, posY - 1f + padding + 4f, -1);

        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;

        posY += 5f;

        for (Schedule schedule : activeSchedules) {
            String nameText = schedule.getName();
            String timeString = getTimeString(schedule);

            float nameWidth = Fonts.sfMedium.getWidth(nameText, fontSize);
            float bindWidth = Fonts.sfMedium.getWidth(timeString, fontSize);

            float localWidth = nameWidth + bindWidth + padding * 3;

            Fonts.sfMedium.drawText(ms, nameText, posX + padding, posY + 0.5f, ColorUtils.rgba(210, 210, 210, 255), fontSize, 0.05f);
            Fonts.sfMedium.drawText(ms, timeString, posX + width - padding - bindWidth, posY + 0.5f, ColorUtils.rgba(210, 210, 210, 255), fontSize, 0.05f);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (fontSize + padding);
            localHeight += (fontSize + padding);
        }
        Scissor.unset();
        Scissor.pop();
        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }

    private String formatTime(Calendar calendar, int minutes) {
        String times;
        int hours = minutes / 60;
        int secondsLeft = 59 - calendar.get(Calendar.SECOND);

        if ((minutes %= 60) > 0) {
            --minutes;
        }

        if (hours > 1) {
            times = hours + "ч " + minutes + "м";
        } else {
            times = minutes + "м " + secondsLeft + "с";
        }
        return times;
    }

    private int calculateTimeDifference(int[] times, int minutes) {
        int index = Arrays.binarySearch(times, minutes);

        if (index < 0) {
            index = -index - 1;
        }

        if (index >= times.length) {
            return times[0] + MINUTES_IN_DAY - minutes;
        }

        return times[index] - minutes;
    }

    private String getTimeString(Schedule schedule, Calendar calendar) {
        int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int[] timeArray = Arrays.stream(schedule.getTimes()).mapToInt(TimeType::getMinutesSinceMidnight).toArray();
        int timeDifference = calculateTimeDifference(timeArray, minutes);
        return formatTime(calendar, timeDifference);
    }

    public String getTimeString(Schedule schedule) {
        return getTimeString(schedule, Calendar.getInstance(timeZone));
    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(x, y, width, height + 5f, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(x + 0.5f, y + 0.5f, width - 1f, height + 4, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height + 5f, radius, ColorUtils.rgba(13,13,13, 230));
    }

}
