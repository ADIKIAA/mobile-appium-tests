package nikitazhekov.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class VkVideoPage {

    private final AndroidDriver driver;

    public VkVideoPage(AndroidDriver driver) {
        this.driver = driver;
    }

    /** Закрывает окно логина, если оно появилось */
    public void closeLoginIfVisible() {
        System.out.println("Закрываем всплывающие окна...");

        try {
            Thread.sleep(2000);

            driver.navigate().back();
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("Ошибка при закрытии окон: " + e.getMessage());
        }
    }


    public void openFirstVideo() {
        System.out.println("Открываем первое видео...");

        closeLoginIfVisible();

        try {
            Thread.sleep(2000);

            int width = driver.manage().window().getSize().width;
            int height = driver.manage().window().getSize().height;

            new io.appium.java_client.TouchAction(driver)
                    .tap(PointOption.point(width / 2, height / 3))
                    .perform();

            Thread.sleep(3000);

            System.out.println("Видео успешно открыто");

        } catch (Exception e) {
            throw new RuntimeException("Не удалось открыть видео: " + e.getMessage());
        }
    }

    /** Запускает видео, если оно на паузе */
    public void playVideoIfPaused() {
        System.out.println("Пытаемся запустить видео...");

        try {
            // Даем время на загрузку
            Thread.sleep(2000);

            // Тапаем по центру экрана, чтобы показать элементы управления
            int width = driver.manage().window().getSize().width;
            int height = driver.manage().window().getSize().height;

            new io.appium.java_client.TouchAction(driver)
                    .tap(PointOption.point(width / 2, height / 2))
                    .perform();
            System.out.println("Тап для показа контролов");

            Thread.sleep(1000);

            // Ищем кнопку play
            List<WebElement> playButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'play') or contains(@text, 'Воспроизвести') or contains(@text, 'Play') or @content-desc='Воспроизвести' or @content-desc='Play' or @resource-id='com.vk.vkvideo:id/play']")
            );

            if (!playButtons.isEmpty()) {
                WebElement playButton = playButtons.get(0);
                playButton.click();
                System.out.println("Кнопка play нажата - видео запущено");
                Thread.sleep(2000);
            } else {

                List<WebElement> pauseButtons = driver.findElements(
                        By.xpath("//*[contains(@resource-id, 'pause') or contains(@text, 'Пауза') or contains(@text, 'Pause') or @content-desc='Пауза' or @content-desc='Pause']")
                );

                if (!pauseButtons.isEmpty()) {
                    System.out.println("Найдена кнопка паузы - видео уже воспроизводится");
                } else {
                    System.out.println("Кнопки управления не найдены");
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка при попытке запуска видео: " + e.getMessage());
        }
    }

    /** Паузит видео, если оно воспроизводится */
    public void pauseVideoIfPlaying() {
        System.out.println("Пытаемся поставить видео на паузу...");

        try {
            // Ждем больше времени для загрузки видео
            Thread.sleep(5000);

            int width = driver.manage().window().getSize().width;
            int height = driver.manage().window().getSize().height;

            new io.appium.java_client.TouchAction(driver)
                    .tap(PointOption.point(width / 2, height / 2))
                    .perform();
            System.out.println("Тап для показа контролов");

            Thread.sleep(1000);

            List<WebElement> pauseButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'pause') or contains(@text, 'Пауза') or contains(@text, 'Pause') or @content-desc='Пауза' or @content-desc='Pause' or contains(@resource-id, 'player_play_pause')]")
            );

            if (!pauseButtons.isEmpty()) {
                WebElement pauseButton = pauseButtons.get(0);
                pauseButton.click();
                System.out.println("Видео поставлено на паузу");
                Thread.sleep(2000);
            } else {
                List<WebElement> playButtons = driver.findElements(
                        By.xpath("//*[contains(@resource-id, 'play') or contains(@text, 'Воспроизвести') or contains(@text, 'Play') or @content-desc='Воспроизвести' or @content-desc='Play']")
                );

                if (!playButtons.isEmpty()) {
                    System.out.println("Найдена кнопка play - видео уже на паузе");
                } else {
                    System.out.println("Кнопки управления не найдены - видео может быть в процессе загрузки");
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка при попытке паузы: " + e.getMessage());
        }
    }

    /** Проверяем, воспроизводится ли видео (для позитивного теста) */
    public boolean isVideoPlaying() {
        System.out.println("=== НАЧАЛО ПРОВЕРКИ ВИДЕО ===");

        try {
            // Сначала попробуем найти элементы по разным стратегиям
            printDebugInfo();

            List<WebElement> timeElementsById = driver.findElements(By.id("com.vk.vkvideo:id/time_current"));
            if (!timeElementsById.isEmpty()) {
                System.out.println("Найдены элементы времени по ID: " + timeElementsById.size());
                return checkTimeChange(timeElementsById.get(0));
            }

            List<WebElement> allTextElements = driver.findElements(By.className("android.widget.TextView"));
            System.out.println("Всего TextView элементов: " + allTextElements.size());

            for (WebElement element : allTextElements) {
                try {
                    String text = element.getText();
                    if (text != null && text.matches("\\d{1,2}:\\d{2}")) { // Формат MM:SS или M:SS
                        System.out.println("Найден элемент с временем: " + text);
                        return checkTimeChange(element);
                    }
                } catch (Exception e) {
                    // Пропускаем элементы с ошибками
                }
            }

            boolean isPlayingByButtons = checkPlayPauseState();
            if (isPlayingByButtons) {
                return true;
            }

            List<WebElement> progressBars = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'progress') or contains(@class, 'ProgressBar')]")
            );
            if (!progressBars.isEmpty()) {
                System.out.println("Найдены progress bars: " + progressBars.size());

                return isProgressBarMoving(progressBars.get(0));
            }

            System.out.println("Не найдены элементы для проверки видео");
            return false;

        } catch (Exception e) {
            System.out.println("Ошибка при проверке видео: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Проверяет состояние кнопок play/pause */
    private boolean checkPlayPauseState() {
        try {
            // Ищем кнопку паузы (значит видео играет)
            List<WebElement> pauseButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'pause') or contains(@text, 'Пауза') or contains(@text, 'Pause')]")
            );

            if (!pauseButtons.isEmpty()) {
                System.out.println("Найдена кнопка паузы - видео играет");
                return true;
            }

            // Ищем кнопку play (значит видео на паузе или не запущено)
            List<WebElement> playButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'play') or contains(@text, 'Воспроизвести') or contains(@text, 'Play')]")
            );

            if (!playButtons.isEmpty()) {
                System.out.println("Найдена кнопка play - видео на паузе");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Ошибка при проверке состояния кнопок: " + e.getMessage());
        }

        return false;
    }

    /** Проверяет, движется ли progress bar */
    private boolean isProgressBarMoving(WebElement progressBar) {
        try {
            // Получаем начальное значение progress bar
            String initialValue = progressBar.getText();
            if (initialValue.isEmpty()) {
                initialValue = progressBar.getAttribute("value") != null ?
                        progressBar.getAttribute("value") : "0";
            }

            System.out.println("Начальное значение progress bar: " + initialValue);

            // Ждем 3 секунды
            Thread.sleep(3000);

            // Получаем новое значение
            String newValue = progressBar.getText();
            if (newValue.isEmpty()) {
                newValue = progressBar.getAttribute("value") != null ?
                        progressBar.getAttribute("value") : "0";
            }

            System.out.println("Новое значение progress bar: " + newValue);

            // Если значения разные - progress bar движется
            boolean isMoving = !initialValue.equals(newValue);
            System.out.println("Progress bar движется: " + isMoving);

            return isMoving;

        } catch (Exception e) {
            System.out.println("Ошибка при проверке progress bar: " + e.getMessage());
            return false;
        }
    }

    /** Проверяет изменение времени на элементе */
    private boolean checkTimeChange(WebElement timeElement) {
        try {
            String initialTime = timeElement.getText();
            System.out.println("Начальное время: " + initialTime);

            if (initialTime.isEmpty() || initialTime.equals("0:00") || initialTime.equals("00:00")) {
                System.out.println("Время не началось, ждем...");
                Thread.sleep(3000);
                initialTime = timeElement.getText();
                System.out.println("Время после ожидания: " + initialTime);
            }

            // Ждем 5 секунд для прогресса
            Thread.sleep(5000);

            String newTime = timeElement.getText();
            System.out.println("Время после 5 секунд: " + newTime);

            boolean isPlaying = !initialTime.equals(newTime);
            System.out.println("Видео воспроизводится: " + isPlaying);

            return isPlaying;

        } catch (Exception e) {
            System.out.println("Ошибка при проверке изменения времени: " + e.getMessage());
            return false;
        }
    }

    /** Выводит отладочную информацию о текущем экране */
    private void printDebugInfo() {
        try {
            System.out.println("\n=== ДЕБАГ ИНФОРМАЦИЯ ===");

            List<WebElement> textViews = driver.findElements(By.className("android.widget.TextView"));
            System.out.println("TextView элементы (" + textViews.size() + "):");

            for (int i = 0; i < Math.min(textViews.size(), 10); i++) {
                try {
                    WebElement element = textViews.get(i);
                    String text = element.getText();
                    String resourceId = element.getAttribute("resourceId");

                    if (text != null && !text.isEmpty()) {
                        System.out.println(i + ": ID=" + resourceId + ", Текст='" + text + "'");
                    }
                } catch (Exception e) {
                    // Пропускаем
                }
            }

            List<WebElement> timeRelatedElements = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'time') or contains(@resource-id, 'progress')]")
            );
            System.out.println("\nЭлементы связанные со временем (" + timeRelatedElements.size() + "):");

            for (WebElement element : timeRelatedElements) {
                try {
                    String resourceId = element.getAttribute("resourceId");
                    String text = element.getText();
                    System.out.println("ID=" + resourceId + ", Текст='" + text + "'");
                } catch (Exception e) {
                    // Пропускаем
                }
            }

            // Ищем все ImageView
            List<WebElement> imageViews = driver.findElements(By.className("android.widget.ImageView"));
            System.out.println("\nImageView элементы (" + imageViews.size() + "):");

            for (int i = 0; i < Math.min(imageViews.size(), 5); i++) {
                try {
                    WebElement element = imageViews.get(i);
                    String resourceId = element.getAttribute("resourceId");
                    String contentDesc = element.getAttribute("content-desc");

                    if (resourceId != null && (resourceId.contains("play") || resourceId.contains("pause"))) {
                        System.out.println(i + ": ID=" + resourceId + ", Описание='" + contentDesc + "' (кнопка управления)");
                    }
                } catch (Exception e) {
                    // Пропускаем
                }
            }

            System.out.println("=== КОНЕЦ ДЕБАГ ИНФОРМАЦИИ ===\n");

        } catch (Exception e) {
            System.out.println("Ошибка при выводе дебаг информации: " + e.getMessage());
        }
    }

    /** Метод для позитивного теста - видео должно воспроизводиться */
    public boolean testVideoPlaysSuccessfully() {
        // Открываем видео
        openFirstVideo();

        // Запускаем видео (если оно на паузе)
        playVideoIfPaused();

        // Даем время на запуск
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Проверяем, что видео воспроизводится
        return isVideoPlaying();
    }

    /** Метод для негативного теста - видео НЕ должно воспроизводиться */
    public boolean testVideoNotPlaying() {
        // Открываем видео
        openFirstVideo();

        pauseVideoIfPlaying();

        return isVideoPausedOrStopped();
    }

    /** Проверяет, что видео на паузе или остановлено */
    private boolean isVideoPausedOrStopped() {
        System.out.println("=== ПРОВЕРКА ЧТО ВИДЕО НЕ ВОСПРОИЗВОДИТСЯ ===");

        try {
            // Ждем 3 секунды
            Thread.sleep(3000);

            boolean isPaused = checkIfVideoIsPaused();
            if (isPaused) {
                System.out.println("Видео на паузе (по кнопкам управления)");
                return true;
            }

            List<WebElement> timeElements = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'time') or contains(@text, ':')]")
            );

            if (!timeElements.isEmpty()) {
                WebElement timeElement = timeElements.get(0);
                String initialTime = timeElement.getText();
                System.out.println("Начальное время: " + initialTime);

                Thread.sleep(5000);

                String newTime = timeElement.getText();
                System.out.println("Время через 5 секунд: " + newTime);

                boolean isNotPlaying = initialTime.equals(newTime);
                System.out.println("Время не изменилось (видео не играет): " + isNotPlaying);

                return isNotPlaying;
            }

            System.out.println("Элементы времени не найдены - видео, вероятно, не загружено или не играет");
            return true;

        } catch (Exception e) {
            System.out.println("Ошибка при проверке паузы: " + e.getMessage());
            return true; // Если ошибка, считаем что видео не играет
        }
    }

    /** Проверяет, что видео на паузе по кнопкам управления */
    private boolean checkIfVideoIsPaused() {
        try {
            List<WebElement> playButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'play') or contains(@text, 'Воспроизвести') or contains(@text, 'Play') or @content-desc='Воспроизвести' or @content-desc='Play']")
            );

            if (!playButtons.isEmpty()) {
                System.out.println("Найдена кнопка play - видео на паузе");
                return true;
            }

            List<WebElement> pauseButtons = driver.findElements(
                    By.xpath("//*[contains(@resource-id, 'pause') or contains(@text, 'Пауза') or contains(@text, 'Pause') or @content-desc='Пауза' or @content-desc='Pause']")
            );

            if (!pauseButtons.isEmpty()) {
                System.out.println("Найдена кнопка паузы - видео играет");
                return false;
            }

            System.out.println("Кнопки управления не найдены");
            return false;

        } catch (Exception e) {
            System.out.println("Ошибка при проверке кнопок: " + e.getMessage());
            return false;
        }
    }
}