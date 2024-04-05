package com.cource3.newsapi.services;

import com.cource3.newsapi.model.News;
import com.cource3.newsapi.model.NewsParams;
import com.cource3.newsapi.repositories.NewsParamsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsParamsService {

    private final NewsParamsRepository newsParamsRepository;

    public List<News> setParams(List<News> newsList) {
        for (News news : newsList) {
            NewsParams params = new NewsParams(news);
            Optional<NewsParams> optDbParams = newsParamsRepository.findById(params.getId());
            NewsParams dbParams = optDbParams.orElseGet(() -> newsParamsRepository.save(params));
            news.setParams(dbParams);
        }
        return newsList;
    }

    public News updateParams(News news) {
        newsParamsRepository.save(new NewsParams(news));
        return news;
    }
}
