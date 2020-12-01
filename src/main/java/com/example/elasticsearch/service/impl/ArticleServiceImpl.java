package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.dto.BulkOperationDTO;
import com.example.elasticsearch.dto.ReIndexRequestDTO;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.example.elasticsearch.service.ArticleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleCustomRepository articleCustomRepository;
    private final ObjectMapper objectMapper;

    public ArticleServiceImpl(ArticleCustomRepository articleCustomRepository, ObjectMapper objectMapper) {
        this.articleCustomRepository = articleCustomRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void indexArticles(Article article) {
        UUID uuid = UUID.randomUUID();
        String articleId = uuid.toString();
        article.setArticleId(articleId);
        IndexResponse indexResponse = articleCustomRepository.indexArticles(article);
        logger.info("Indexing completed");
    }

    @Override
    public List<Article> getArticleByTitle(String title) {
        SearchResponse searchResponse = articleCustomRepository.findByTitle(title);
        if (searchResponse != null) {
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            List<Article> articleList = Arrays.stream(searchHits)
                    .map(searchHit -> {
                        try {
                            return objectMapper.readValue(searchHit.getSourceAsString(), Article.class);
                        } catch (JsonProcessingException e) {
                            logger.error("Error while parsing article response", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            logger.info("Searching completed");
            return articleList;
        } else {
            logger.info("Error in indexing article");
            return null;
        }
    }

    @Override
    public Article getArticleById(String id) {
        GetResponse getResponse = articleCustomRepository.findById(id);
        if (getResponse != null && getResponse.isExists()) {
            logger.info("Article found");
            try {
                return objectMapper.readValue(getResponse.getSourceAsString(), Article.class);
            } catch (JsonProcessingException e) {
                logger.error("Error while parsing article response", e);
                return null;
            }
        } else {
            logger.info("Article not found");
            return null;
        }
    }

    @Override
    public void deleteArticleById(String id) {
        DeleteResponse deleteResponse = articleCustomRepository.deleteArticleById(id);
        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            logger.info("Article not found");
        } else {
            logger.info("Article deleted successfully");
        }
    }

    @Override
    public Article updateArticleById(String id, Article article) {
        UpdateResponse updateResponse = articleCustomRepository.updateArticleById(id, article);
        if (updateResponse != null) {
            if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
                logger.info("New Article created");
            } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                logger.info("Existing article updated");
            } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                logger.info("No changes to update the article");
            }
            GetResult getResult = updateResponse.getGetResult();
            if (getResult.isExists()) {
                try {
                    return objectMapper.readValue(getResult.sourceAsString(), Article.class);
                } catch (JsonProcessingException e) {
                    logger.error("Error while parsing article response", e);
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void bulkOperation(BulkOperationDTO bulkOperation) {
        BulkResponse bulkResponse = articleCustomRepository.bulkOperation(bulkOperation);
        BulkItemResponse[] bulkResponseItems = bulkResponse.getItems();
        logger.info("Bulk operation completed");
    }

    @Override
    public List<Article> getMultipleArticles(List<String> articleIdList) {
        MultiGetResponse multiGetResponse = articleCustomRepository.getMultipleArticles(articleIdList);
        logger.info("Completed getting multiple articles");
        MultiGetItemResponse[] multiGetResponseList = multiGetResponse.getResponses();
        return Arrays.stream(multiGetResponseList)
                .map(multiGetItemResponse -> {
                    GetResponse response = multiGetItemResponse.getResponse();
                    if (response.isExists()) {
                        try {
                            return objectMapper.readValue(response.getSourceAsString(), Article.class);
                        } catch (JsonProcessingException e) {
                            logger.error("Error while parsing article response", e);
                            return null;
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void reIndexArticles(ReIndexRequestDTO reIndexRequest) {
        BulkByScrollResponse bulkByScrollResponse = articleCustomRepository.reIndexArticles(reIndexRequest);
        if (bulkByScrollResponse != null) {
            long totalTimeTaken = bulkByScrollResponse.getTook().getMillis();
            logger.info("Total time taken for reindexing is {}ms", totalTimeTaken);
            logger.info(bulkByScrollResponse.toString());
        }
    }

    @Override
    public List<Article> searchArticles(String keyword) {
        SearchResponse searchResponse = articleCustomRepository.searchArticles(keyword);
        logger.info("Completed searching for articles");
        if (searchResponse != null) {
            SearchHits hits = searchResponse.getHits();
            logger.info("Total number of search result count: {}", hits.getTotalHits().value);
            SearchHit[] searchHits = hits.getHits();
            return Arrays.stream(searchHits)
                    .map(searchHit -> {
                        try {
                            return objectMapper.readValue(searchHit.getSourceAsString(), Article.class);
                        } catch (JsonProcessingException e) {
                            logger.error("Error while parsing article response", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            logger.info("Total number of search result count: {}", 0);
        }
        return null;
    }
}
