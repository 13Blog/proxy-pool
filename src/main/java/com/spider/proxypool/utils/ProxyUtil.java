package com.spider.proxypool.utils;

import com.spider.proxypool.common.Constants;
import com.spider.proxypool.entity.ProxyEntity;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

/**
 * Created by 13 on 17/10/9.
 */
public class ProxyUtil {

    private static final Logger logger = LogManager.getLogger(ProxyUtil.class);


    public static boolean verifyProxy(ProxyEntity proxy) {
        if (proxy == null) return false;
        return verifyProxy(proxy.getIp(), proxy.getPort());
    }


    public static boolean verifyProxy(String proxy) {

        if (proxy == null || !proxy.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+"))
            return false;

        String[] ps = proxy.trim().split(":");
        return verifyProxy(ps[0], Integer.valueOf(ps[1]));
    }

    /**
     * 验证代理是否可用
     *
     * @param ip
     * @param port
     * @return
     */
    public static boolean verifyProxy(String ip, int port) {
        boolean useful;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(Constants.VERIFY_URL);
            InetSocketAddress addr = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(4 * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(6 * 1000);
            int rCode = connection.getResponseCode();
            useful = rCode == 200;
        } catch (IOException e1) {
            logger.warn(String.format("verify proxy %s:%d exception: " + e1.getMessage(), ip, port));
            useful = false;
        } finally {
            if (connection != null) connection.disconnect();
        }

        logger.info(String.format("verify proxy %s:%d useful: " + useful, ip, port));
        return useful;
    }
}
