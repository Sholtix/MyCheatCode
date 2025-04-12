package wtf.sqwezz.ui.display;

import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.utils.client.IMinecraft;

public interface ElementUpdater extends IMinecraft {

    void update(EventUpdate e);
}
