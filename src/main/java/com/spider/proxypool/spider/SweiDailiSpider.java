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
 * http://www.swei360.com 代理爬取
 */
public class SweiDailiSpider extends AbstractSpider<List<ProxyEntity>> {

    private static final String BASE_URL = "http://www.swei360.com/";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm");

    private static final Logger logger = LogManager.getLogger(SweiDailiSpider.class);


    public SweiDailiSpider() {
        this(30, 1000);
    }

    public SweiDailiSpider(int totalPage) {
        this(totalPage, 1000);
    }

    public SweiDailiSpider(int totalPage, long interval) {
        super(totalPage, interval);
    }

    @Override
    protected String pageUrl() {
        if (pageIndex % 3 == 0) {
            return BASE_URL + "?page=" + (pageIndex + 3) / 3;
        } else if (pageIndex % 3 == 1) {
            return BASE_URL + "free/?stype=1&page=" + (pageIndex + 3) / 3;
        } else if (pageIndex % 3 == 2) {
            return BASE_URL + "free/?stype=3&page=" + (pageIndex + 3) / 3;
        }
        return BASE_URL + "?page=" + (pageIndex + 3) / 3;
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
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 7) {
                    continue;
                }
                /**
                 <tr>
                 <td>110.73.43.59</td>
                 <td>8123</td>
                 <td>高匿代理IP</td>
                 <td>HTTPS</td>
                 <td>SSL高匿_广西南宁市联通</td>
                 <td>8秒</td>
                 <td>2017-10-11 13:08:13</td>
                 </tr>

                 */

                ProxyEntity enity = new ProxyEntity();
                enity.setIp(tds.get(0).text().trim());
                enity.setPort(Integer.parseInt(tds.get(1).text().trim()));
                enity.setAgentType(tds.get(2).text().trim());
                enity.setLocation(tds.get(4).text().trim());
                Date date = new Date();
                try {
                    date = SDF.parse(tds.get(6).text().trim());
                } catch (ParseException e) {
                }

                enity.setLastValidateTime(date);

                logger.info("got an agent: " + enity.toString());
                res.add(enity);
            }
        }
        return res;
    }
}
