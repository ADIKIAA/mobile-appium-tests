package nikitazhekov.tests;

import io.appium.java_client.android.AndroidDriver;
import nikitazhekov.pages.ItalyVpnPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ItalyVpnTest {

    private AndroidDriver driver;
    private ItalyVpnPage page;

    @BeforeEach
    void setup() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("platformName", "android");
            capabilities.setCapability("deviceName", "Pixel 5");
            capabilities.setCapability("automationName", "UiAutomator2");
            capabilities.setCapability("platformVersion", "11.0");

            capabilities.setCapability("appPackage", "com.helalik.italy.vpn");
            capabilities.setCapability("appActivity", "com.vpn.MainActivity2");

            String appPath = "src/test/resources/apk/italy_vpn.apk";
            File file = new File(appPath);
            assertTrue(file.exists(), "APK не найден: " + appPath);
            capabilities.setCapability("app", file.getAbsolutePath());

            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("noReset", true); // Не перезагружать приложение

            driver = new AndroidDriver(new URL("http://127.0.0.1:4808/wd/hub"), capabilities);
            page = new ItalyVpnPage(driver);

            Thread.sleep(5000);
            page.printCurrentState();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            throw new RuntimeException("Не удалось создать драйвер", e);
        }
    }

    @Test
    @DisplayName("Тест подключения")
    void testQuickConnection() {
        System.out.println("\n=== БЫСТРЫЙ ТЕСТ ===");

        // Начальное состояние
        page.printCurrentState();

        // Если подключен - отключаем
        if (page.isConnected()) {
            page.disconnect();
            page.waitForDisconnection(15); // Уменьшил до 15 сек
            page.printCurrentState();
        }

        // Подключаемся
        page.connect();
        page.waitForConnection(30); // Уменьшил до 30 сек

        // Проверяем
        page.printCurrentState();

        if (page.isConnected()) {
            System.out.println("✓ VPN подключен!");

            page.disconnect();
            page.waitForDisconnection(15);
        } else if (page.isError()) {
            System.out.println("⚠ Ошибка подключения");
        } else {
            System.out.println("⚠ Не удалось подключиться");
        }

        System.out.println("Финальное состояние:");
        page.printCurrentState();
    }

    @Test
    @DisplayName("Тест повторного подключения")
    void testReconnection() {
        System.out.println("\n=== ТЕСТ ПОВТОРНОГО ПОДКЛЮЧЕНИЯ ===");

        if (page.isConnected()) {
            page.disconnect();
            page.waitForDisconnection(10);
        }

        System.out.println("Попытка 1:");
        page.connect();
        page.waitForConnection(20);

        boolean firstSuccess = page.isConnected();
        System.out.println("Попытка 1: " + (firstSuccess ? "успешно" : "неудача"));

        if (firstSuccess) {
            page.disconnect();
            page.waitForDisconnection(10);

            System.out.println("Попытка 2:");
            page.connect();
            page.waitForConnection(20);

            boolean secondSuccess = page.isConnected();
            System.out.println("Попытка 2: " + (secondSuccess ? "успешно" : "неудача"));

            if (secondSuccess) {
                System.out.println("✓ Повторное подключение работает!");
                page.disconnect();
            }
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (page != null && page.isConnected()) {
                page.disconnect();
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            // Игнорируем
        }

        if (driver != null) {
            driver.quit();
        }
    }
}