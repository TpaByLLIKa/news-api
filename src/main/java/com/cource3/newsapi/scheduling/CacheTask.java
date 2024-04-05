package com.cource3.newsapi.scheduling;

import com.cource3.newsapi.services.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheTask {

    private final NewsService newsService;

    @Scheduled(fixedDelay = 3600000L)
    @CacheEvict(cacheNames = "news", allEntries = true, beforeInvocation = true)
    public void reloadAllNewsToCache() {
        log.info("Start all news cache reloading");
        newsService.getAllNews();
        log.info("End all news cache reloading");
    }
    @Scheduled(fixedDelay = 180000L)
    @CacheEvict(cacheNames = "recentNews", allEntries = true, beforeInvocation = true)
    public void reloadRecentNewsToCache() {
        log.info("Start recent news cache reloading");
        newsService.getRecentNews();
        log.info("End recent news cache reloading");
    }
}
