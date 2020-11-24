package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Article;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    void indexArticles(Article article) throws IOException;

    List<Article> getArticleByTitle(String title) throws IOException;
}
