package com.example.elasticsearch.repository.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {
    private static final Logger logger = LoggerFactory.getLogger(ArticleCustomRepositoryImpl.class);

    @Value("${es.article.index.name}")
    private String index;

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    public ArticleCustomRepositoryImpl(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public IndexResponse indexArticles(Article article) {
        Map<String, Object> articleMap = objectMapper.convertValue(article, new TypeReference<>() {
        });
        IndexRequest indexRequest = new IndexRequest(index)
                .id(article.getId())
                .source(articleMap);

        try {
            return restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in indexing articles", e);
            return null;
        }
    }

    @Override
    public SearchResponse findByTitle(String title) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("title.keyword", title));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("title.keyword")
                .order(SortOrder.ASC);
        searchSourceBuilder.sort(fieldSortBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(index);

        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in searching articles", e);
            return null;
        }
    }

    @Override
    public GetResponse findById(String id) {
        GetRequest getRequest = new GetRequest();
        getRequest.index(index).id(id);

        try {
            return restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in getting article by id", e);
            return null;
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                logger.error("Invalid index", e);
            }
            return null;
        }
    }
}
