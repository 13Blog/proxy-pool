package com.spider.proxypool.spider;


import com.spider.proxypool.entity.ProxyEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 13 on 2017/10/10.
 * http://www.66ip.cn 代理爬取
 */
public class Www66IPSpider extends AbstractSpider<List<ProxyEntity>> {

    private static final Logger logger = LoggerFactory.getLogger(Www66IPSpider.class);

    private static final String BASE_URL = "http://www.66ip.cn";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日HH时 验证");

    public Www66IPSpider() {
        this(100);
    }

    public Www66IPSpider(int totalPage) {
        this(totalPage, 1000);
    }

    public Www66IPSpider(int totalPage, long interval) {
        super(totalPage, interval);
    }

    protected String pageUrl() {
        String url;
        if (pageIndex == 1) {
            url = BASE_URL + "/index.html";
        } else {
            url = BASE_URL + "/" + pageIndex + ".html";
        }
        return url;
    }

    protected List<ProxyEntity> parseHtml(String html) {

        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("tbody");

        for (Element table : tables) {
            Elements trs = table.select("tr");

            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 5) {
                    continue;
                }
                ProxyEntity enity = new ProxyEntity();
                enity.setIp(tds.get(0).text().trim());
                enity.setPort(Integer.parseInt(tds.get(1).text()));
                enity.setLocation(tds.get(2).text().trim());
                enity.setAgentType(tds.get(3).text().trim());

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
