package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.example.elasticsearch.service.ArticleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Value("${es.article.index.name}")
    private String index;

    private final ArticleCustomRepository articleCustomRepository;
    private final ObjectMapper objectMapper;

    public ArticleServiceImpl(ArticleCustomRepository articleCustomRepository, ObjectMapper objectMapper) {
        this.articleCustomRepository = articleCustomRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void indexArticles(Article article) throws IOException {
        UUID uuid = UUID.randomUUID();
        String articleId = uuid.toString();
        article.setId(articleId);
        article.setArticleId(articleId);
        IndexResponse indexResponse = articleCustomRepository.indexArticles(article);
        logger.info("Indexing completed");
    }

    @Override
    public List<Article> getArticleByTitle(String title) throws IOException {
        SearchResponse searchResponse = articleCustomRepository.findByTitle(title);
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
    }
}
