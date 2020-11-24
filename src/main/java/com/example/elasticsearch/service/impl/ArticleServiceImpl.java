package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.example.elasticsearch.service.ArticleService;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Value("${es.article.index.name}")
    private String index;

    private final ArticleCustomRepository articleCustomRepository;

    public ArticleServiceImpl(ArticleCustomRepository articleCustomRepository) {
        this.articleCustomRepository = articleCustomRepository;
    }

    @Override
    public void indexArticles(Article article) throws IOException {
        UUID uuid = UUID.randomUUID();
        article.setArticleId(uuid.toString());
        IndexResponse indexResponse = articleCustomRepository.indexArticles(article);
        logger.info("Indexing completed");
    }

    @Override
    public void getArticleByTitle(String title) throws IOException {
        SearchResponse searchResponse = articleCustomRepository.findByTitle(title);
        logger.info("Searching completed");
    }
}
