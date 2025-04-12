package wtf.sqwezz.functions.settings.impl;


import wtf.sqwezz.functions.settings.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {

    public float anim;

    public BooleanSetting(String name, Boolean defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> bool) {
        return (BooleanSetting) super.setVisible(bool);
    }

    @Override
    public Integer getValue() {
        return 0;
    }

    @Override
    public void setValue(Object value) {

    }

}