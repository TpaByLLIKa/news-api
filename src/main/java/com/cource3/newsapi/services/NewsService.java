package com.cource3.newsapi.services;

import com.cource3.newsapi.model.News;
import com.cource3.newsapi.parsers.NewsParser;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsParser newsParser;

    @Cacheable("news")
    public List<News> getAllNews() {
        return newsParser.getNews(Long.MAX_VALUE);
    }

    @Cacheable("recentNews")
    public List<News> getRecentNews() {
        List<News> newsList = newsParser.getNews(3L);

        while (newsList.size() > 20)
            newsList.remove(newsList.size() - 1);

        return newsList;
    }
}
