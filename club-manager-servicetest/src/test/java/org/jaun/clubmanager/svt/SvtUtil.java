package org.jaun.clubmanager.svt;

import org.awaitility.Awaitility;

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
}
