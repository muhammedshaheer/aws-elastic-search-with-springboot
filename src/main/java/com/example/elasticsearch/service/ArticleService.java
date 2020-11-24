package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Article;

import java.io.IOException;

public interface ArticleService {
    void indexArticles(Article article) throws IOException;

    void getArticleByTitle(String title) throws IOException;
}
