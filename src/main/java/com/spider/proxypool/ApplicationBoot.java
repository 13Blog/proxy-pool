package com.spider.proxypool;

import com.spider.proxypool.schedule.Scheduler;
import com.spider.proxypool.schedule.SpiderScheduler;
import com.spider.proxypool.schedule.VerifyScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/10/11.
 */
@SpringBootApplication(scanBasePackages = {"com.spider.proxypool.api"})
public class ApplicationBoot {
    private static final List<Scheduler> schedules = Arrays.asList(
            new SpiderScheduler(8, TimeUnit.MINUTES),
            new VerifyScheduler(5, TimeUnit.MINUTES)
    );

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);

        for (Scheduler schedule : schedules) {
            schedule.schedule();
        }
    }
}
