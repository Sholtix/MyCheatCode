package wtf.sqwezz.ui.schedules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wtf.sqwezz.ui.schedules.impl.*;
import wtf.sqwezz.utils.client.IMinecraft;

public class SchedulesManager
        implements IMinecraft {
    private final List<Schedule> schedules = new ArrayList<>();

    public SchedulesManager() {
        this.schedules.addAll(Arrays.asList(new AirDropSchedule(), new ScroogeSchedule(), new SecretMerchantSchedule(), new MascotSchedule(), new CompetitionSchedule()));
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }
}