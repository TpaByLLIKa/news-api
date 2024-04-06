package com.cource3.newsapi.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class News implements Serializable {

    private String title;

    private String summary;

    private String imageSrc;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;

    private String link;

    private Long views;

    private Long likes;

    private Long dislikes;

    private String id;

    public void setParams(NewsParams newsParams) {
        views = newsParams.getViews();
        likes = newsParams.getLikes();
        dislikes = newsParams.getDislikes();
        id = newsParams.getId();
    }
}
