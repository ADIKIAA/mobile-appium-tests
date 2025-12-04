package nikitazhekov.config;

import org.aeonbits.owner.Config;

/**
 * Чтение ключей из emulator.properties
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "file:src/test/resources/configs/emulator.properties",
})
public interface EmulatorConfig extends Config {
    //достаем значения по ключу
    @Key("deviceName")
    String deviceName();

    @Key("platformName")
    String platformName();

    @Key("appPackage")
    String appPackage();

    @Key("appActivity")
    String appActivity();

    @Key("app")
    String app();

    @Key("remoteURL")
    String remoteURL();
}
