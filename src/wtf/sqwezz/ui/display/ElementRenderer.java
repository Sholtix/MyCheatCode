package wtf.sqwezz.ui.display;

import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.utils.client.IMinecraft;

public interface ElementRenderer extends IMinecraft {
    void render(EventDisplay eventDisplay);
}
