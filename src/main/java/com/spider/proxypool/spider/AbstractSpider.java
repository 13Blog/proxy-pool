package com.spider.proxypool.spider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13 on 2017/10/10.
 */
public abstract class AbstractSpider<T> {

    private static final String[][] HEADERS = new String[][]{
            {"Connection", "keep-alive"},
            {"Cache-Control", "no-cache"},
            {"Upgrade-Insecure-Requests", "1"},
            {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"},
            {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"},
            {"Accept-Encoding", "gzip, deflate"},
            {"Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"},
    };

    private static final Logger logger = LoggerFactory.getLogger(AbstractSpider.class);

    protected int pageIndex = 1;

    private int totalPage = 10;
    private long interval = 1000;

    public AbstractSpider(int totalPage, long interval) {
        this.totalPage = totalPage;
        this.interval = interval;
    }

    public boolean hasNextPage() {
        return pageIndex <= totalPage;
    }

    public String nextPage() {
        String html = "";
        String url = pageUrl();
        pageIndex++;
        logger.info("spider page: " + url);
        try {
            Connection connection = Jsoup.connect(url);
            for (String[] head : HEADERS) {
                connection.header(head[0], head[1]);
            }
            connection.timeout(4000).followRedirects(true);
            html = connection.execute().parse().html();//执行
        } catch (IOException e) {
            logger.info("spider page error: " + e.getMessage());
        }

        return html;
    }


    public List<T> fetchAll() {
        List<T> res = new ArrayList<>();
        while (hasNextPage()) {
            String html = nextPage();
            res.add(parseHtml(html));
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
            }
        }

        return res;
    }

    public void fetchAll(Consumer<T> comsumer) {
        while (hasNextPage()) {
            String html = nextPage();
            comsumer.consume(parseHtml(html));
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
            }
        }
    }


    public int getPageIndex() {
        return pageIndex;
    }


    public int getTotalPage() {
        return totalPage;
    }

    public long getInterval() {
        return interval;
    }

    protected abstract String pageUrl();

    protected abstract T parseHtml(String html);

    public interface Consumer<T> {
        void consume(T t);
    }
}
