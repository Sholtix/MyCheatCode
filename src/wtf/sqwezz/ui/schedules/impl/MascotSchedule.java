package wtf.sqwezz.ui.schedules.impl;

import wtf.sqwezz.ui.schedules.Schedule;
import wtf.sqwezz.ui.schedules.TimeType;

public class MascotSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Талисман";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.NINETEEN_HALF};
    }
}
