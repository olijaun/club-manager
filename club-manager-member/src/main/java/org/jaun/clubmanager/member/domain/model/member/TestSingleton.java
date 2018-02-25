package org.jaun.clubmanager.member.domain.model.member;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;

/**
 * Just some test with standalone and timer stuff.
 */
@Singleton
@Startup
public class TestSingleton {

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        System.out.println("startup done");
        //timerService.createIntervalTimer(0, 10000, new TimerConfig());
    }

    @Timeout
    @Schedule(minute = "*", hour = "*")
    public void cleanup() {
        System.out.println("cleaning up...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("done.");
    }
}
