package com.spider.proxypool.schedule;

import com.spider.proxypool.common.Constants;
import com.spider.proxypool.entity.ProxyEntity;
import com.spider.proxypool.spider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/10/10.
 */
public class SpiderScheduler extends Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(SpiderScheduler.class);

    public SpiderScheduler(long defaultInterval, TimeUnit defaultUnit) {
        super(defaultInterval, defaultUnit);
    }

    @Override
    public void run() {

        logger.info("spider scheduler running...");

        List<AbstractSpider<List<ProxyEntity>>> fetchers =
                Arrays.asList(

                new GoubanjiaSpider(Constants.totalPage)

                );


        for (AbstractSpider<List<ProxyEntity>> fetcher : fetchers) {
            fetcher.fetchAll((list) -> {
                ProxyVerifier.verifyAll(list);
            });
        }

        logger.info("finish spider scheduler");
    }
}
