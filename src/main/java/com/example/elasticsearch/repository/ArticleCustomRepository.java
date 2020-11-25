package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.Article;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;

public interface ArticleCustomRepository {
    SearchResponse findByTitle(String title);

    IndexResponse indexArticles(Article article);
}
