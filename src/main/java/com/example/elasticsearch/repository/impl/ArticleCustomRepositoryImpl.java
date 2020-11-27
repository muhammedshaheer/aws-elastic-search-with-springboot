package com.example.elasticsearch.repository.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.dto.BulkOperationDTO;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                .id(article.getArticleId())
                .source(articleMap);

        try {
            return restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in indexing articles", e);
            return null;
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                logger.error("Invalid index", e);
            }
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
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                logger.error("Invalid index", e);
            }
            return null;
        }
    }

    @Override
    public GetResponse findById(String id) {
        GetRequest getRequest = new GetRequest();
        getRequest.index(index)
                .id(id);

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

    @Override
    public DeleteResponse deleteArticleById(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);

        try {
            return restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in deleting article by id", e);
            return null;
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                logger.error("Invalid index", e);
            }
            return null;
        }
    }

    @Override
    public UpdateResponse updateArticleById(String id, Article article) {
        Map<String, Object> articleMap = objectMapper.convertValue(article, new TypeReference<>() {
        });

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).id(id)
                .doc(articleMap)
                .docAsUpsert(true)
                .fetchSource(true);

        try {
            return restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in updating article", e);
            return null;
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                logger.error("Invalid index", e);
            }
            return null;
        }
    }

    @Override
    public BulkResponse bulkOperation(BulkOperationDTO bulkOperation) {
        BulkRequest bulkRequest = new BulkRequest();

        List<Article> indexArticleList = bulkOperation.getIndexArticle();
        indexArticleList.forEach(article -> {
            UUID uuid = UUID.randomUUID();
            String articleId = uuid.toString();
            article.setArticleId(articleId);
            Map<String, Object> articleMap = objectMapper.convertValue(article, new TypeReference<>() {
            });
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(index)
                    .id(article.getArticleId())
                    .source(articleMap);
            bulkRequest.add(indexRequest);
        });

        List<Article> updateArticleList = bulkOperation.getUpdateArticle();
        updateArticleList.forEach(article -> {
            Map<String, Object> articleMap = objectMapper.convertValue(article, new TypeReference<>() {
            });
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index)
                    .id(article.getArticleId())
                    .doc(articleMap)
                    .docAsUpsert(true);
            bulkRequest.add(updateRequest);
        });

        List<String> deleteArticleList = bulkOperation.getDeleteArticle();
        deleteArticleList.forEach(articleId -> {
            DeleteRequest deleteRequest = new DeleteRequest(index, articleId);
            bulkRequest.add(deleteRequest);
        });

        try {
            return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error in applying bulk operation");
        }
        return null;
    }
}
