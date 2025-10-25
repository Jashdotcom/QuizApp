package com.example.quizzapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private String username;
    private Integer score;
    private Integer rank;
}