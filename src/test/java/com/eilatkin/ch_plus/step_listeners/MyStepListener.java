package com.eilatkin.ch_plus.step_listeners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.properties.AllureProperties;
import ru.ibsqa.chameleon.steps.aspect.AbstractStepListener;
import ru.ibsqa.chameleon.steps.aspect.StepType;
import ru.testit.services.Adapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Листенер позволяет скопировать скриншот последнего заваленного шага из Allure в Test It reporter
 */

@Component
@Slf4j
public class MyStepListener extends AbstractStepListener {

    private final String screenshots4TMS = System.getProperty("screenshots4TMS","true");
    private static final String imgFormat = "png";

    @Autowired
    private AllureProperties allureProperties;

    @Override
    public void stepAfterThrowing(JoinPoint joinPoint, Throwable throwable, StepType stepType) {
        if (Boolean.parseBoolean(screenshots4TMS) && stepType.isUiStep()) screenshots4TMS();
    }

    @SneakyThrows
    private void screenshots4TMS() {
        // :
        Path failedScrnPath = findScreenshotUsingNIOApi(allureProperties.getResultsDirectory());
        log.debug("allureProperties.getResultsDirectory() = {}", allureProperties.getResultsDirectory());
        log.debug("failedScrnPath = {}", failedScrnPath);
        Adapter.addAttachments(String.valueOf(failedScrnPath));
    }

    private static Path findScreenshotUsingNIOApi(String sdir) throws IOException {
        Path dir = Paths.get(sdir);
        log.debug("Paths.get(sdir) = {}", dir);
        if (Files.isDirectory(dir)) {
            Optional<Path> opPath = Files.list(dir)
                    .filter(p -> (!Files.isDirectory(p)
                            && p.getFileName().toString().endsWith(imgFormat)))
                    .sorted((p1, p2)-> Long.valueOf(p2.toFile().lastModified())
                            .compareTo(p1.toFile().lastModified()))
                    .findFirst();

            if (opPath.isPresent()){
                return opPath.get();
            }
        }
        return null;
    }

}
