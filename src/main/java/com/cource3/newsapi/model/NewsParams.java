package com.cource3.newsapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "news_params")
@Data
@NoArgsConstructor
public class NewsParams {

    public NewsParams(News news) {
        id = StringUtils.hasText(news.getLink()) ? news.getLink() + news.getDate() :
                StringUtils.hasText(news.getTitle()) ? news.getTitle() + news.getDate() :
                        StringUtils.hasText(news.getSummary()) ? news.getSummary() + news.getDate() :
                                null;
        if (id == null)
            throw new NullPointerException("Unable to set id for news: " + news);

        views = news.getViews() != null ? news.getViews() : 0L;
        likes = news.getLikes() != null ? news.getLikes() : 0L;
        dislikes = news.getDislikes() != null ? news.getDislikes() : 0L;
    }

    @Id
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "views")
    private Long views;

    @Column(name = "likes")
    private Long likes;

    @Column(name = "dislikes")
    private Long dislikes;
}
