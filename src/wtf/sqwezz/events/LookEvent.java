package wtf.sqwezz.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LookEvent extends CancelEvent {
    public double yaw,pitch;
}
