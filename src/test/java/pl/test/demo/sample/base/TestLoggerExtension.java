package pl.test.demo.sample.base;

import io.qameta.allure.Attachment;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.io.IoBuilder;
import org.junit.jupiter.api.extension.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class TestLoggerExtension implements BeforeAllCallback, TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler,
        BeforeTestExecutionCallback, AfterTestExecutionCallback {

    static {
        System.setOut(IoBuilder.forLogger(LogManager.getLogger())
                .setLevel(Level.DEBUG).buildPrintStream()
        );
        System.setErr(IoBuilder.forLogger(LogManager.getLogger())
                .setLevel(Level.WARN).buildPrintStream()
        );
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            log.error(context.getExecutionException().get().getMessage());
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getRequiredTestMethod().getName();
        ThreadContext.put("logFileName", testName + "_" + System.nanoTime() + "_" + Thread.currentThread().getId());
        ThreadContext.put("logDirName", testName);
        log.info("--- TEST " + testName + " STARTED ---");
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        log.info("--- TEST " + context.getRequiredTestMethod().getName() + " ENDED ---");
        attachLogToReport();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        logThrowable(throwable);
        throw throwable;
    }

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        logThrowable(throwable);
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        logThrowable(throwable);
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        logThrowable(throwable);
        throw throwable;
    }

    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        logThrowable(throwable);
        throw throwable;
    }

    private void logThrowable(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString();
        log.error(throwable.getMessage() + System.lineSeparator() + stacktrace);
    }

    @Attachment(value = "log", type = "text/plain")
    private byte[] attachLogToReport() throws IOException {
        String fileName = ThreadContext.get("logFileName");
        String dir = ThreadContext.get("logDirName");
        File file = new File(System.getProperty("user.dir") + "/build/logs/" + dir + "/" + fileName + ".log");

        return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
    }

}
