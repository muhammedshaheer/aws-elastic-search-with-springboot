package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.repository.ArticleCustomRepository;
import com.example.elasticsearch.repository.ArticleRepository;
import com.example.elasticsearch.service.ArticleService;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;
    private final ArticleCustomRepository articleCustomRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleCustomRepository articleCustomRepository) {
        this.articleRepository = articleRepository;
        this.articleCustomRepository = articleCustomRepository;
    }

    @Override
    public void indexArticles(Article article) throws IOException {
        IndexResponse indexResponse = articleCustomRepository.indexArticles(article);
        logger.info("Indexing completed");
    }

    @Override
    public void getArticleByTitle(String title) throws IOException {
        SearchResponse searchResponse = articleCustomRepository.findByTitle(title);
        logger.info("Searching completed");
    }
}
