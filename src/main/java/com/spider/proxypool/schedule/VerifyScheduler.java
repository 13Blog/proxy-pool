package com.spider.proxypool.schedule;

import com.spider.proxypool.db.repositor.ProxyRepository;
import com.spider.proxypool.entity.ProxyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/10/10.
 */
public class VerifyScheduler extends Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(VerifyScheduler.class);

    public VerifyScheduler(long defaultInterval, TimeUnit defaultUnit) {
        super(defaultInterval, defaultUnit);
    }

    @Override
    public void run() {
        List<ProxyEntity> proxys = ProxyRepository.getInstance().getAll();
        logger.info("verify scheduler running, proxys:"+proxys.size());
        ProxyVerifier.refreshAll(proxys);
    }
}
