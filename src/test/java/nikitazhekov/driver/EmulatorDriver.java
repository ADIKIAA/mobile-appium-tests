package nikitazhekov.driver;

import io.appium.java_client.android.AndroidDriver;
import nikitazhekov.config.ConfigReader;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmulatorDriver {
    public static AndroidDriver driver;
    private static final String DEVICE_NAME = ConfigReader.emulatorConfig.deviceName();
    private static final String PLATFORM_NAME = ConfigReader.emulatorConfig.platformName();
    private static final String APK_PATH = ConfigReader.emulatorConfig.app();
    private static final String REMOTE_URL = ConfigReader.emulatorConfig.remoteURL();

    /**
     * Создаёт AndroidDriver для Appium
     */
    public static AndroidDriver createDriver(String appPackage, String appActivity) {

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("platformName", PLATFORM_NAME);
            capabilities.setCapability("deviceName", DEVICE_NAME);
            capabilities.setCapability("automationName", "UiAutomator2");

            capabilities.setCapability("appPackage", appPackage);
            capabilities.setCapability("appActivity", appActivity);

            // путь до APK
            File file = new File(APK_PATH);
            assertTrue(file.exists(), "APK not found: " + APK_PATH);
            capabilities.setCapability("app", file.getAbsolutePath());

            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("noReset", true);

            driver = new AndroidDriver(new URL(REMOTE_URL), capabilities);
            return driver;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании Appium driver", e);
        }
    }
}
