package com.eilatkin.ch_plus.steps;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.reporter.IReporterManager;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.testit.models.LinkType;
import ru.testit.services.Adapter;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.comparison.PointsMarkupPolicy;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

import static ru.yandex.qatools.ashot.shooting.ShootingStrategies.scaling;
import static ru.yandex.qatools.ashot.shooting.ShootingStrategies.viewportPasting;
import static org.junit.jupiter.api.Assertions.fail;


@Component
@Slf4j
public class Screenshotter extends AbstractSteps {

    private final String screenshotsExpectedFolder= "./src/test/resources/screenshots/";
    private final String screenshotsActualFolder="./target/screenshots/actual/";
    private final String screenshotsDiffFolder="./target/screenshots/diff/";
    private final String testRunId = System.getProperty("testRunId","");
    private final String screenshots4TMS = System.getProperty("screenshots4TMS","true");
    private final String reporterUrl4TMS = System.getProperty("reporterUrl4TMS","");
    private final String date = LocalDate.now().toString();
    private String testName;
    private String testPath;
    private final String imgFormat = "png";

    public void setupScreenshotter() {
        testName = System.getProperty("testName");
        testPath = URLDecoder
                .decode(System.getProperty("testPath"), StandardCharsets.UTF_8)
                .split("/features/")[1]
                .replaceAll("\\.feature","")
                .concat("/");
        String driverType = System.getProperty("driverType");
        String W = System.getProperty("windowSizeW");
        String H = System.getProperty("windowSizeH");
        log.debug("Создаю каталоги скриншотов: {}", testPath);
        createScreenshotsFolders(testPath);
        String fullPath = testPath + testName
                + " [" + driverType + " "
                + W + "x" + H + "]" + "." + imgFormat;
        actualFile = new File(screenshotsActualFolder + fullPath);
        expectedFile = new File(screenshotsExpectedFolder + fullPath);
        diffFile = new File(screenshotsDiffFolder + fullPath);
    }

    private float getWindowDpr(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object output = js.executeScript("return window.devicePixelRatio");
        return Float.parseFloat(String.valueOf(output));
    }

    private void createScreenshotsFolders(String testPath) {
        createFolder(Path.of(screenshotsExpectedFolder + testPath));
        createFolder(Path.of(screenshotsActualFolder + testPath));
        createFolder(Path.of(screenshotsDiffFolder + testPath));
    }

    private void createFolder(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File actualFile;
    private File expectedFile;
    private File diffFile;
    private Screenshot actualScreenshot;

    /**
     * Скриншот страницы целиком со скролом;
     * скриншот видимой области страницы;
     * скриншот элемента со скролом;
     * скриншот элемента без скрола.
     */
    public void makePageScreenshotWithScroll(WebDriver driver) {
        actualScreenshot = new AShot()
                .shootingStrategy(viewportPasting(scaling(getWindowDpr(driver)), 200))
                .takeScreenshot(driver);
        saveExpectedScreenshotToFile();
        saveScreenshotToFile(actualScreenshot.getImage(), actualFile);
    }

    public void makePageScreenshotWithoutScroll(WebDriver driver) {
        actualScreenshot = new AShot()
                .shootingStrategy(scaling(getWindowDpr(driver)))
                .takeScreenshot(driver);
        saveExpectedScreenshotToFile();
        saveScreenshotToFile(actualScreenshot.getImage(), actualFile);
    }

    public void makeElementScreenshotWithScroll(WebDriver driver, WebElement element) {
        actualScreenshot = new AShot()
                .shootingStrategy(viewportPasting(scaling(getWindowDpr(driver)), 200))
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver, element);
        saveExpectedScreenshotToFile();
        saveScreenshotToFile(actualScreenshot.getImage(), actualFile);
    }

    public void makeElementScreenshotWithoutScroll(WebDriver driver, WebElement element) {
        actualScreenshot = new AShot()
                .shootingStrategy(scaling(getWindowDpr(driver)))
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver, element);
        saveExpectedScreenshotToFile();
        saveScreenshotToFile(actualScreenshot.getImage(), actualFile);
    }

    private Screenshot expectedScreenshot;
    /**
     *    Сохраняет эталонный скриншот, если его ещё нет в проекте, либо забирает его из файла, если он есть
     */
    private void saveExpectedScreenshotToFile() {
        setupScreenshotter();
        if (!expectedFile.exists()) {
            expectedScreenshot = actualScreenshot;
            saveScreenshotToFile(actualScreenshot.getImage(), expectedFile);
            fail(String.format("Создан эталонный скриншот по пути: %s%s. \n Необходимо его сверить и добавить в git index.",
                    testPath, testName));
        } else {
            try { expectedScreenshot = new Screenshot(ImageIO.read(expectedFile)); }
            catch (IOException e) {  e.printStackTrace(); }
        }
    }

    private void saveScreenshotToFile(RenderedImage screenshot, File targetFile) {
        try {
            ImageIO.write(screenshot, imgFormat, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private IReporterManager reporterManager;

    /**
     *  Сравнивает эталонный и актуальный скриншот.
     *  Создает изображение разницы эталонного и актуального скриншота и проверяет на допустимую разницу в пикселях.
     */
    @SneakyThrows
    public void compareScreenshots(int allowableDiff) {
        ImageDiffer imageDiffer = new ImageDiffer()
                .withIgnoredColor(Color.MAGENTA)
                .withDiffMarkupPolicy(
                        new PointsMarkupPolicy().withDiffColor(Color.RED));
        ImageDiff diffImage = imageDiffer.makeDiff(expectedScreenshot, actualScreenshot);
        int diffSize = diffImage.getDiffSize();
        saveScreenshotToFile(diffImage.getMarkedImage(), diffFile);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(diffFile), imgFormat, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        reporterManager.createAttachment("Скриншот отклонений от эталона:", is, "image/"+imgFormat, imgFormat);
        if (Boolean.parseBoolean(screenshots4TMS)) screenshots4TMS();
        if (!Objects.equals(reporterUrl4TMS, "")) reporterUrl4TMS();
        if (diffSize > allowableDiff) {
            fail(String.format("Скриншоты не совпадают. \nДопустимое значение разницы: %spx\n Фактическое значение разницы: %spx",
                    allowableDiff, diffSize));
        }
    }

    @SneakyThrows
    private void screenshots4TMS() {
        // добавить скриншот диффа в Test It reporter:
        String newDiffPath = screenshotsDiffFolder + "diff."+imgFormat;
        if (!diffFile.renameTo(new File(newDiffPath))) log.error("Не удалось создать временный файл diff");
        Adapter.addAttachments(newDiffPath);
        Path fileToDeletePath = Paths.get(newDiffPath);
        Files.delete(fileToDeletePath);
    }

    private void reporterUrl4TMS() {
        // добавить ссылку диффа в Test It reporter:
        // FIXME возможно косяк адаптера, не добавляется ссылка с ошибкой вида
//        ERROR ru.testit.services.AdapterManager - Could not update test case: test case with uuid CFEFE5056E1DF949C09AB4346D4C8F269F916C3CA94F37D99DCA0CB8EF347FB4 not found
        Adapter.addLinks(reporterUrl4TMS+date+"/"+testRunId+"/", "Отчёт Allure", "", LinkType.DEFECT);
    }


}
