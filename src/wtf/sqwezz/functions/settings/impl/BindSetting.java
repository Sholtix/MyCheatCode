package wtf.sqwezz.functions.settings.impl;

import wtf.sqwezz.functions.settings.Setting;

import java.util.function.Supplier;

public class BindSetting extends Setting<Integer> {
    public BindSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> bool) {
        return (BindSetting) super.setVisible(bool);
    }

    @Override
    public Integer getValue() {
        return 0;
    }

    @Override
    public void setValue(Object value) {

    }
}
