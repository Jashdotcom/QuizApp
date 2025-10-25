package com.example.quizzapp.dto;

import lombok.Data;

@Data
public class OptionDTO {
    private Long id;
    private String text;
    private Boolean isCorrect;
}