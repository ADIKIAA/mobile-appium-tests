package nikitazhekov.tests;

import io.appium.java_client.android.AndroidDriver;
import nikitazhekov.driver.EmulatorDriver;
import nikitazhekov.pages.VkVideoPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class VkVideoTest {

    private AndroidDriver driver;
    private VkVideoPage page;

    @BeforeEach
    void setup() {
        driver = EmulatorDriver.createDriver(
                "com.vk.vkvideo",
                "com.vk.video.screens.main.MainActivity"
        );
        page = new VkVideoPage(driver);
    }

    @Test
    @DisplayName("Позитивный тест: видео должно воспроизводиться")
    void videoShouldPlay() {
        // Проверяем позитивный сценарий
        boolean playing = page.testVideoPlaysSuccessfully();

        assertTrue(playing, "Видео должно воспроизводиться");

        System.out.println("✓ Позитивный тест пройден: видео воспроизводится");
    }

    @Test
    @DisplayName("Негативный тест: видео не должно воспроизводиться без запуска")
    void videoShouldNotPlay() {
        // Проверяем негативный сценарий
        boolean notPlaying = page.testVideoNotPlaying();

        assertTrue(notPlaying, "Видео не должно воспроизводиться без запуска");

        System.out.println("✓ Негативный тест пройден: видео не воспроизводится");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}