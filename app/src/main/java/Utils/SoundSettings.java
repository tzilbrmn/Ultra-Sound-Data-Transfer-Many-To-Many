package Utils;

/**
 * This class holds settings for data transmission over sound
 */
public class SoundSettings {
    // Current Values

    public static final String CurrStartFrequency = "18000";
    public static final String CurrEndFrequency = "19000";
    public static final String CurrBitsPerTone = "1";

    /**
     *
     * @return an Integer Array containing the transmission settings
     */
    public static Integer[] getSettings() {
        Integer[] SettingValues = new Integer[3];
        SettingValues[0] = Integer.valueOf(CurrStartFrequency);
        SettingValues[1] = Integer.valueOf(CurrEndFrequency);
        SettingValues[2] = Integer.valueOf(CurrBitsPerTone);
        return SettingValues;
    }
}
