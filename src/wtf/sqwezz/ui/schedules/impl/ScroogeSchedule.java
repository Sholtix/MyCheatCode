package wtf.sqwezz.ui.schedules.impl;

import wtf.sqwezz.ui.schedules.Schedule;
import wtf.sqwezz.ui.schedules.TimeType;

public class ScroogeSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Скрудж";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.FIFTEEN_HALF};
    }
}
