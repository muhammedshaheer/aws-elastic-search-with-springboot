package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.Article;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

public interface ArticleCustomRepository {
    SearchResponse findByTitle(String title);

    IndexResponse indexArticles(Article article);

    GetResponse findById(String id);
}
