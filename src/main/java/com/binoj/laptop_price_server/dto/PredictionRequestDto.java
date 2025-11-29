package com.binoj.laptop_price_server.dto;

import lombok.Data;

@Data
public class PredictionRequestDto {

    private int ram;
    private double weight;
    private int touchscreen;
    private int ips;
    private String company;
    private String typename;
    private String opsys;
    private String cpu;
    private String gpu;


}
