package pl.test.demo.sample.base;

import io.qameta.allure.Attachment;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.io.IoBuilder;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class MyTestWatcher extends TestWatcher {

    static {
        System.setOut(IoBuilder.forLogger(LogManager.getLogger())
                .setLevel(Level.DEBUG).buildPrintStream()
        );
        System.setErr(IoBuilder.forLogger(LogManager.getLogger())
                .setLevel(Level.WARN).buildPrintStream()
        );
    }

    @Override
    protected void starting(Description description) {
        ThreadContext.put("logFileName", description.getMethodName() + "_" + System.nanoTime() + "_" + Thread.currentThread().getId());
        ThreadContext.put("logDirName", description.getMethodName());
        log.info("--- TEST " + description.getMethodName() + " STARTED ---");
    }
    @Override
    protected void failed(Throwable e, Description description) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString();
        log.error(e.getMessage() + System.lineSeparator() + stacktrace);
    }
    @Override
    protected void finished(Description description) {
        log.info("--- TEST " + description.getMethodName() + " ENDED ---");
        attachLogToReport();
    }

    @Attachment(value = "log", type = "text/plain")
    private byte[] attachLogToReport(){
        String fileName = ThreadContext.get("logFileName");
        String dir = ThreadContext.get("logDirName");
        File file = new File(System.getProperty("user.dir") + "/build/logs/" + dir + "/" + fileName + ".log");
        try {
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

}
