package com.binoj.laptop_price_server.controller;

import com.binoj.laptop_price_server.dto.PredictionRequestDto;
import com.binoj.laptop_price_server.dto.PredictionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PredictionController {

    private final RestTemplate restTemplate;
    private final String FLASK_URL = "http://localhost:5001/predict";

    @PostMapping("/predict")
    public PredictionResponseDto predict(@RequestBody PredictionRequestDto req) {
        try {
            // Call Python Flask ML API
            PredictionResponseDto response =
                    restTemplate.postForObject(FLASK_URL, req, PredictionResponseDto.class);

            return response;

        } catch (Exception ex) {
            PredictionResponseDto err = new PredictionResponseDto();
            err.setSuccess(false);
            err.setError("Error calling Flask API: " + ex.getMessage());
            return err;
        }
    }


}
