package wtf.sqwezz.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventRotate extends CancelEvent {
    public double yaw, pitch;
}
