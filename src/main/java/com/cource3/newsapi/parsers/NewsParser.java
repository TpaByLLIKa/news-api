package com.cource3.newsapi.parsers;

import com.cource3.newsapi.model.News;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class NewsParser {

    @Value("${parsing.url}")
    private String url;
    @Value("${parsing.totalPagesNum}")
    private long totalPagesNum;
    @Value("${parsing.totalThreadsNum}")
    private int threadsNum;


    public List<News> getNews(long pageCount) {
        log.info("Start loading news from " + (pageCount == Long.MAX_VALUE ? "all" : pageCount) + " pages");

        if (pageCount < totalPagesNum) {
            totalPagesNum = pageCount;
            while (totalPagesNum % threadsNum != 0)
                threadsNum--;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
        long k = totalPagesNum / threadsNum;
        List<Future<List<News>>> futures = new LinkedList<>();

        for (int j = 0; j < threadsNum; j++) {
            long threadNum = j;
            futures.add(
                    CompletableFuture.supplyAsync(() -> {
                        log.info("Start loading in " + (threadNum + 1) + "'th parser thread");

                        Connection connection = Jsoup.connect(url + threadNum + 1)
                                .timeout(15000);

                        Map<Element, Element> elMap = new LinkedHashMap<>();

                        for (long i = threadNum * k + 1; i <= k * (threadNum + 1); i++) {
                            try {
                                addNewsElFromPage(elMap, connection, url + i);
                            } catch (IOException e) {
                                if (e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() == 404)
                                    break;
                                else
                                    log.error(e.getMessage(), e);
                            }
                        }
                        log.info("End loading and start parsing in " + (threadNum + 1) + "'th parser thread");

                        return parse(elMap);
                    }, executorService));
        }

        List<News> resList = new LinkedList<>();
        for (Future<List<News>> future : futures) {
            try {
                resList.addAll(future.get());
            } catch (ExecutionException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info("End parsing: " + resList.size() + " news has been parsed");
        return resList;
    }

    protected void addNewsElFromPage(Map<Element, Element> elMap, Connection connection, String url) throws IOException {
        Elements pageNews = connection.url(url)
                .get()
                .getElementsByClass("news-front");

//        log.info(pageNews.size() + " news has been loaded");
        for (Element el : pageNews) {
            String link = el.getElementsByClass("media-heading").get(0).child(0).attr("abs:href");

            try {
                Element pageEl = connection.url(link).get().getElementsByClass("content-single").get(0);
                elMap.put(el, pageEl);
            } catch (IOException e) {
                elMap.put(el, null);
                log.warn("Failed to open this news: " + e.getMessage(), e);
            }
        }
    }

    protected List<News> parse(Map<Element, Element> elMap) {
        List<News> newsList = new LinkedList<>();

        for (Map.Entry<Element, Element> entry : elMap.entrySet()) {
            Element newsEl = entry.getKey();
            Element newsPageElement = entry.getValue();

            News news = new News();

            Element head = newsEl.getElementsByClass("media-heading").get(0).child(0);
            news.setTitle(head.ownText());
            news.setLink(head.attr("abs:href"));

            if (entry.getValue() != null) {
                StringBuilder summary = new StringBuilder();
                newsPageElement.child(1).getAllElements()
                        .stream()
                        .filter(Element::hasText)
                        .forEach(element -> summary.append(element.ownText()));
                news.setSummary(summary.toString());

                String strDate = newsPageElement.getElementsByClass("media-body").get(0).getElementsByTag("p").get(0).ownText();
                try {
                    DateFormatter formatter = new DateFormatter("dd.MM.yyyy");
                    news.setDate(formatter.parse(strDate, Locale.getDefault()));
                } catch (ParseException e) {
                    log.warn(e.getMessage(), e);
                }
            }

            Element img = newsEl.getElementsByClass("wp-post-image").get(0).child(0);
            String[] srcset = img.attr("srcset").split(", ");
            String srcFromSet = srcset[srcset.length - 1].split(" ")[0];
            String imgSrc = StringUtils.hasText(srcFromSet) && srcFromSet.startsWith("http") ? srcFromSet : img.attr("abs:src");
            news.setImageSrc(imgSrc);

            newsList.add(news);
        }

        log.info(newsList.size() + " news has been parsed");

        return newsList;
    }
}
