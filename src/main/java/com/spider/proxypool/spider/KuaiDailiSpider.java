package com.spider.proxypool.spider;

import com.spider.proxypool.entity.ProxyEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 13 on 2017/10/10.
 * http://www.kuaidaili.com 代理爬取
 */
public class KuaiDailiSpider extends AbstractSpider<List<ProxyEntity>> {

    private static final Logger logger = LogManager.getLogger(KuaiDailiSpider.class);

    private static final String BASE_URL = "http://www.kuaidaili.com/free/";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public KuaiDailiSpider() {
        this(10);
    }

    public KuaiDailiSpider(int totalPage) {
        this(totalPage, 1000);
    }

    public KuaiDailiSpider(int totalPage, long interval) {
        super(totalPage, interval);
    }

    @Override
    protected String pageUrl() {
        if (pageIndex == 1) {
            return BASE_URL;
        } else {
            return BASE_URL + "inha/" + pageIndex + "/";
        }
    }

    @Override
    protected List<ProxyEntity> parseHtml(String html) {
        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("tbody");
        if (tables == null || tables.isEmpty()) {
            return res;
        }
        for (Element table : tables) {
            Elements trs = table.select("tr");

            for (int i = 0; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 7) {
                    continue;
                }
                ProxyEntity enity = new ProxyEntity();
                enity.setIp(tds.get(0).text().trim());
                enity.setPort(Integer.parseInt(tds.get(1).text()));
                enity.setAgentType(tds.get(2).text().trim());
                enity.setLocation(tds.get(4).text().trim());

                Date date = new Date();
                try {
                    date = SDF.parse(tds.get(4).text().trim());
                } catch (ParseException e) {
                }

                enity.setLastValidateTime(date);

                logger.info("got an agent: " + enity);
                res.add(enity);
            }
        }
        return res;
    }
}
