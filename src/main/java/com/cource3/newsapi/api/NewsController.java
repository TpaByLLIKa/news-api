package com.cource3.newsapi.api;

import com.cource3.newsapi.model.News;
import com.cource3.newsapi.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping(value = "/news")
    public List<News> getRecentNews() {
        return newsService.getRecentNews();
    }

    @GetMapping(value = "/allnews")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }
}
