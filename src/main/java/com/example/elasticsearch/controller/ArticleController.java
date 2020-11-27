package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.dto.BulkOperationDTO;
import com.example.elasticsearch.service.ArticleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/article")
    public void indexArticles(@RequestBody Article article) {
        articleService.indexArticles(article);
    }

    @GetMapping("/article")
    public List<Article> getArticleByTitle(@RequestParam(name = "title") String title) {
        return articleService.getArticleByTitle(title);
    }

    @GetMapping("/article/{id}")
    public Article getArticleById(@PathVariable("id") String id) {
        return articleService.getArticleById(id);
    }

    @DeleteMapping("/article/{id}")
    public void deleteArticleById(@PathVariable("id") String id) {
        articleService.deleteArticleById(id);
    }

    @PutMapping("/article/{id}")
    public Article updateArticleById(@PathVariable("id") String id, @RequestBody Article article) {
        return articleService.updateArticleById(id, article);
    }

    @PostMapping("/article/bulk")
    public void bulkOperation(@RequestBody BulkOperationDTO bulkOperation) {
        articleService.bulkOperation(bulkOperation);
    }
}
