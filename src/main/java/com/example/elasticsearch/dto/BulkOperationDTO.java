package com.example.elasticsearch.dto;

import com.example.elasticsearch.document.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkOperationDTO {

    private List<Article> indexArticle;
    private List<Article> updateArticle;
    private List<String> deleteArticle;

}
