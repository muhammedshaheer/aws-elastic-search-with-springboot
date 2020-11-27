package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.Article;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;

public interface ArticleCustomRepository {
    SearchResponse findByTitle(String title);

    IndexResponse indexArticles(Article article);

    GetResponse findById(String id);

    DeleteResponse deleteArticleById(String id);

    UpdateResponse updateArticleById(String id, Article article);
}
