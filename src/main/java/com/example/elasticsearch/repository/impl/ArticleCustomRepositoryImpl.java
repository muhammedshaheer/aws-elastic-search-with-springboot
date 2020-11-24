package com.example.elasticsearch.repository.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    @Value("${es.article.index.name}")
    private String INDEX;

    @Value("${es.article.index.type}")
    private String TYPE;

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    public ArticleCustomRepositoryImpl(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public IndexResponse indexArticles(Article article) throws IOException {
        Map articleMap = objectMapper.convertValue(article, Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, article.getId())
                .source(articleMap);

        return restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public SearchResponse findByTitle(String title) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("title.keyword", title));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("title.keyword")
                .order(SortOrder.ASC);
        searchSourceBuilder.sort(fieldSortBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(INDEX);

        return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }
}
