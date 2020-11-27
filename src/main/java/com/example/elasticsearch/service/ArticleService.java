package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Article;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    void indexArticles(Article article);

    List<Article> getArticleByTitle(String title);

    Article getArticleById(String id);
}
