package org.jaun.clubmanager.svt;

import org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

public class SvtUtil {
    public static void waitForNoException(Runnable runnable) {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> {
            try {
                runnable.run();
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }
}
