package nikitazhekov.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItalyVpnPage {

    private final AndroidDriver driver;

    private final By statusText = By.id("com.helalik.italy.vpn:id/TextView4");
    private final By countryNameText = By.id("com.helalik.italy.vpn:id/tv_country_name");
    private final By flagImage = By.id("com.helalik.italy.vpn:id/iv_flag");
    private final By lottieAnimation = By.id("com.helalik.italy.vpn:id/lottie");

    public ItalyVpnPage(AndroidDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    /** Получает текущий статус подключения */
    public String getConnectionStatus() {
        try {
            return driver.findElement(statusText).getText().trim();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /** Получает название страны/пакета */
    public String getCountryName() {
        try {
            return driver.findElement(countryNameText).getText().trim();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /** Проверяет, подключен ли VPN */
    public boolean isConnected() {
        String status = getConnectionStatus().toLowerCase().trim();
        return status.equals("connected") ||
                status.equals("connected!") ||
                status.contains("protecting");
    }

    /** Проверяет, отключен ли VPN */
    public boolean isDisconnected() {
        String status = getConnectionStatus().toLowerCase().trim();
        return status.equals("disconnected") ||
                status.equals("disconnected!");
    }

    /** Проверяет, есть ли ошибка */
    public boolean isError() {
        String status = getConnectionStatus().toLowerCase().trim();
        return status.contains("try another") ||
                status.contains("error") ||
                status.contains("failed");
    }

    /** Подключается к VPN */
    public void connect() {
        System.out.println("=== ПОДКЛЮЧЕНИЕ ===");

        if (isConnected()) {
            System.out.println("Уже подключен");
            return;
        }

        if (isError()) {
            System.out.println("Ошибка обнаружена, меняем сервер...");
            changeServer();
        }

        System.out.println("Пробуем подключиться...");

        if (quickConnect()) {
            System.out.println("Подключение инициировано");
            checkPermissionDialog();
        } else {
            System.out.println("Не удалось подключиться");
        }
    }

    /** Быстрая попытка подключения */
    private boolean quickConnect() {
        if (tapAnimation()) return true;
        return false;
    }

    /** Тап по анимации */
    private boolean tapAnimation() {
        try {
            driver.findElement(lottieAnimation).click();
            System.out.println("Тап по анимации");
            sleep(1500);
            return !getConnectionStatus().toLowerCase().contains("disconnected");
        } catch (Exception e) {
            return false;
        }
    }

    /** Меняет сервер */
    private void changeServer() {
        try {
            System.out.println("Быстрая смена сервера...");

            // Открываем список серверов
            driver.findElement(flagImage).click();
            sleep(1500);

            // Ищем серверы
            List<WebElement> servers = driver.findElements(By.className("android.widget.RelativeLayout"));

            // Быстро выбираем другой сервер если есть
            if (servers.size() > 1) {
                // Пробуем второй сервер
                servers.get(1).click();
                System.out.println("Выбран второй сервер");
            } else if (!servers.isEmpty()) {
                // Или первый
                servers.get(0).click();
                System.out.println("Выбран первый сервер");
            }

            sleep(1500);

        } catch (Exception e) {
            System.out.println("Ошибка смены сервера: " + e.getMessage());

            // Быстрое возвращение
            try {
                driver.navigate().back();
                sleep(1000);
            } catch (Exception ex) {
                // Игнорируем
            }
        }
    }

    /** Проверяет диалог разрешения */
    private void checkPermissionDialog() {
        try {
            sleep(2000);

            // Ищем кнопку OK
            List<WebElement> buttons = driver.findElements(By.className("android.widget.Button"));
            for (WebElement button : buttons) {
                try {
                    String text = button.getText().toLowerCase();
                    if (text.contains("ok") || text.contains("allow") || text.contains("разрешить")) {
                        button.click();
                        System.out.println("Нажата кнопка разрешения");
                        sleep(1000);
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            // Игнорируем
        }
    }

    /** Отключается от VPN */
    public void disconnect() {
        System.out.println("=== ОТКЛЮЧЕНИЕ ===");

        if (isDisconnected()) {
            System.out.println("Уже отключен");
            return;
        }

        // Быстрая попытка отключения
        if (tapAnimation() ) {
            System.out.println("Отключение");
        }
    }

    /** Ждет подключения */
    public void waitForConnection(int timeoutSeconds) {
        System.out.println("Ожидание подключения (" + timeoutSeconds + " сек)...");

        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (isConnected()) {
                System.out.println("✓ ПОДКЛЮЧЕНО!");
                return;
            }

            if (isError()) {
                System.out.println("Ошибка: " + getConnectionStatus());
                changeServer();
            }

            sleep(2000);
        }

        System.out.println("Не подключилось за " + timeoutSeconds + " сек");
    }

    /** Ждет отключения */
    public void waitForDisconnection(int timeoutSeconds) {
        System.out.println("Ожидание отключения (" + timeoutSeconds + " сек)");

        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (isDisconnected() || isError()) {
                System.out.println("ОТКЛЮЧЕНО");
                return;
            }

            sleep(2000);
        }

        System.out.println("Не отключилось за " + timeoutSeconds + " сек");
    }

    /** Выводит состояние */
    public void printCurrentState() {
        System.out.println("=== СОСТОЯНИЕ ===");
        System.out.println("Статус: " + getConnectionStatus());
        System.out.println("Пакет: " + getCountryName());
        System.out.println("Подключен: " + isConnected());
        System.out.println("Отключен: " + isDisconnected());
        System.out.println("==================");
    }

    /** Короткий sleep */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}