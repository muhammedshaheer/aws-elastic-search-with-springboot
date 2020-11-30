package com.example.elasticsearch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReIndexRequestDTO {
    private List<String> sourceIndexes;
    private String destinationIndex;
}
