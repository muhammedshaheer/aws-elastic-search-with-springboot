package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/article")
    public void indexArticles(@RequestBody Article article) throws IOException {
        articleService.indexArticles(article);
    }

    @GetMapping("/article")
    public List<Article> getArticleByTitle(@RequestParam(name = "title") String title) throws IOException {
        return articleService.getArticleByTitle(title);
    }
}
