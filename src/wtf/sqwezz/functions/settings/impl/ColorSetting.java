package wtf.sqwezz.functions.settings.impl;


import wtf.sqwezz.functions.settings.Setting;

import java.util.function.Supplier;

public class ColorSetting extends Setting<Integer> {

    public ColorSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }
    @Override
    public ColorSetting setVisible(Supplier<Boolean> bool) {
        return (ColorSetting) super.setVisible(bool);
    }

    @Override
    public Integer getValue() {
        return 0;
    }

    @Override
    public void setValue(Object value) {

    }
}