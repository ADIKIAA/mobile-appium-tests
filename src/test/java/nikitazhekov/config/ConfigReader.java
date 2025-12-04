package nikitazhekov.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Класс для читалки файлов .properties
 */
public class ConfigReader {

    /**
     * Читалка для emulator.properties
     */
    public static final EmulatorConfig emulatorConfig = ConfigFactory
            .create(EmulatorConfig.class, System.getProperties());

}
