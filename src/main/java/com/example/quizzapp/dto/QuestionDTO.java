// dto/QuestionDTO.java
package com.example.quizzapp.dto;

import com.example.quizzapp.model.Question.QuestionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private QuestionType type;
    private Integer points;
    private Integer timeLimit;
    private List<OptionDTO> options = new ArrayList<>();
}