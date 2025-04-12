package wtf.sqwezz.ui.styles;


import java.awt.*;

public class StyleFactoryImpl implements StyleFactory {

    @Override
    public Style createStyle(String name, Color firstColor, Color secondColor) {
        return new Style(name, firstColor, secondColor);
    }
}
