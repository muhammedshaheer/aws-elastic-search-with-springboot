package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.Article;
import com.example.elasticsearch.dto.BulkOperationDTO;
import com.example.elasticsearch.dto.ReIndexRequestDTO;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;

import java.util.List;

public interface ArticleCustomRepository {
    SearchResponse findByTitle(String title);

    IndexResponse indexArticles(Article article);

    GetResponse findById(String id);

    DeleteResponse deleteArticleById(String id);

    UpdateResponse updateArticleById(String id, Article article);

    BulkResponse bulkOperation(BulkOperationDTO bulkOperation);

    MultiGetResponse getMultipleArticles(List<String> articleIdList);

    BulkByScrollResponse reIndexArticles(ReIndexRequestDTO reIndexRequest);
}
