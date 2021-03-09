package org.jaun.clubmanager.svt;

import org.awaitility.Awaitility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class SvtUtil {
    public static void waitForAssertionTrue(Runnable runnable) {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> {
            try {
                runnable.run();
                return true;
            } catch (AssertionError e) {
                System.err.println(e);
                return false;
            }
        });
    }

    public static String load(String resourcePath) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SvtUtil.class.getResourceAsStream(resourcePath).transferTo(bos);
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not load test resource " + resourcePath, e);
        }
    }
}
