package wtf.sqwezz.functions.settings;

import java.util.HashMap;
import java.util.Map;

public class SettingManager {
    private final Map<String, Setting> settingsMap;

    public SettingManager() {
        settingsMap = new HashMap<>();
    }


    public void addSetting(Setting setting) {
        if (setting != null) {
            settingsMap.put(setting.getName(), setting);
        }
    }


    public Setting getSetting(String name) {
        return settingsMap.get(name);
    }


    public void removeSetting(String name) {
        settingsMap.remove(name);
    }


    public Map<String, Setting> getAllSettings() {
        return settingsMap;
    }
}