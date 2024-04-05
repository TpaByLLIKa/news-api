package com.cource3.newsapi.repositories;

import com.cource3.newsapi.model.NewsParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsParamsRepository extends JpaRepository<NewsParams, String> {
}
