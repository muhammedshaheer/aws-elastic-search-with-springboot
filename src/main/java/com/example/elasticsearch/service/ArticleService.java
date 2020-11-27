package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.dto.BulkOperationDTO;

import java.util.List;

public interface ArticleService {
    void indexArticles(Article article);

    List<Article> getArticleByTitle(String title);

    Article getArticleById(String id);

    void deleteArticleById(String id);

    Article updateArticleById(String id, Article article);

    void bulkOperation(BulkOperationDTO bulkOperation);

    List<Article> getMultipleArticles(List<String> articleIdList);
}
