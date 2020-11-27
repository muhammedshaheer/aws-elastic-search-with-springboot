package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Article;

import java.util.List;

public interface ArticleService {
    void indexArticles(Article article);

    List<Article> getArticleByTitle(String title);

    Article getArticleById(String id);

    void deleteArticleById(String id);

    Article updateArticleById(String id, Article article);
}
