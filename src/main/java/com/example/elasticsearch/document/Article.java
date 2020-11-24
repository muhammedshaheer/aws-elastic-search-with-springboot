package com.example.elasticsearch.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "article")
public class Article {

    @Id
    private String id;

    private String title;

    private List<Author> authors;
}
