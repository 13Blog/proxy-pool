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
 * http://www.xicidaili.com 代理爬取
 */
public class XiciDailiSpider extends AbstractSpider<List<ProxyEntity>> {

    private static final String BASE_URL = "http://www.xicidaili.com/";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(XiciDailiSpider.class);


    public XiciDailiSpider() {
        this(10, 1000);
    }

    public XiciDailiSpider(int totalPage) {
        this(totalPage, 1000);
    }

    public XiciDailiSpider(int totalPage, long interval) {
        super(totalPage, interval);
    }

    @Override
    protected String pageUrl() {
        if (pageIndex <= getTotalPage() / 2) {
            return BASE_URL + "nn/" + pageIndex;
        } else {
            return BASE_URL + "nt/" + (pageIndex - (getTotalPage() / 2));
        }

    }

    @Override
    protected List<ProxyEntity> parseHtml(String html) {

        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.getElementById("ip_list").select("tbody");

        for (Element table : tables) {
            Elements trs = table.select("tr");

            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 10) {
                    continue;
                }
                ProxyEntity enity = new ProxyEntity();
                enity.setIp(tds.get(1).text().trim());
                enity.setPort(Integer.parseInt(tds.get(2).text().trim()));
                enity.setAgentType(tds.get(4).text().trim());
                enity.setLocation(tds.get(3).text().trim());

                Date date = new Date();
                try {
                    date = SDF.parse(tds.get(9).text().trim());
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
