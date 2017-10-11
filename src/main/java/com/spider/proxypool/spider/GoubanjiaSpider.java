package com.spider.proxypool.spider;

import com.spider.proxypool.entity.ProxyEntity;
import com.google.common.base.Strings;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/10/10.
 * http://www.goubanjia.com/ 代理爬取
 */
public class GoubanjiaSpider extends AbstractSpider<List<ProxyEntity>> {

    private static final String BASE_URL = "http://www.goubanjia.com/free/";

    private static final Logger logger = LoggerFactory.getLogger(GoubanjiaSpider.class);

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GoubanjiaSpider() {
        this(10, 1000);
    }

    public GoubanjiaSpider(int totalPage) {
        this(totalPage, 1000);
    }

    public GoubanjiaSpider(int totalPage, long interval) {
        super(totalPage, interval);
    }

    @Override
    protected String pageUrl() {
        if (pageIndex == 1) {
            return BASE_URL + "index.shtml";
        } else {
            return BASE_URL + "index" + pageIndex + ".shtml";
        }
    }

    @Override
    protected List<ProxyEntity> parseHtml(String html) {
        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("tbody");

        for (Element table : tables) {
            Elements trs = table.select("tr");

            for (int i = 0; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 8) {
                    continue;
                }

                ProxyEntity enity = new ProxyEntity();

                Element ipPort = tds.get(0);
                for (Element child : ipPort.children()) {
                    if (!child.hasAttr("style")) continue;
                    if (child.attr("style").contains("none")) {
                        child.remove();
                    }
                }
                String str = ipPort.text().replaceAll("\\s+", "");
                String[] ippppp = str.split(":");
                enity.setIp(ippppp[0]);
                enity.setPort(Integer.parseInt(ippppp[1]));
                enity.setAgentType(tds.get(1).select("a").text().trim());
                enity.setLocation(tds.get(3).select("a").text().trim());

                String validate = tds.get(6).text().trim();
                if (Strings.isNullOrEmpty(validate))
                    validate = "10天前";

                Date date = tryParse(validate);
                if (date == null) {
                    String t = validate.replaceFirst("[^-0-9.]+$", "");
                    String u = validate.replaceFirst("^[-0-9.]+", "");

                    int tu = u.startsWith("秒") ? 1 : u.startsWith("分") ?
                            60 : u.startsWith("小时") ? 3600 : u.startsWith("天") ?
                            24 * 3600 : 24 * 3600 * 100;
                    Float tt = Float.parseFloat(t);
                    long millsBefore = (long) (tt * tu * 1000);
                    if (millsBefore < 0) millsBefore = TimeUnit.DAYS.toMillis(10);
                    date = new Date();
                    date.setTime(date.getTime() - millsBefore);
                }
                enity.setLastValidateTime(date);

                logger.info("got an agent: " + enity);
                res.add(enity);
            }
        }
        return res;
    }

    private Date tryParse(String s) {
        try {
            return SDF.parse(s);
        } catch (ParseException e) {
        }

        return null;
    }
}
