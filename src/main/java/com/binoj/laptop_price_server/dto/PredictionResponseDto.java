package com.binoj.laptop_price_server.dto;

import lombok.Data;

@Data
public class PredictionResponseDto {

    private boolean success;
    private double prediction;
    private String error;

}
