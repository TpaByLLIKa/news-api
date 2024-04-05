package com.cource3.newsapi.api;

import com.cource3.newsapi.model.News;
import com.cource3.newsapi.services.NewsParamsService;
import com.cource3.newsapi.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsParamsService newsParamsService;

    @GetMapping(value = "/news")
    public List<News> getRecentNews() {
        return newsParamsService.setParams(newsService.getRecentNews());
    }

    @GetMapping(value = "/allnews")
    public List<News> getAllNews() {
        return newsParamsService.setParams(newsService.getAllNews());
    }

    @PostMapping(value = "/updateNews")
    public News updateNews(@RequestBody News news) {
        return newsParamsService.updateParams(news);
    }
}
