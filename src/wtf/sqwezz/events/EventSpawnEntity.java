package wtf.sqwezz.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.entity.Entity;

@Data
@AllArgsConstructor
public class EventSpawnEntity {
    private Entity entity;
}
