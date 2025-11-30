package com.binoj.laptop_price_server.controller;

import com.binoj.laptop_price_server.dto.PredictionRequestDto;
import com.binoj.laptop_price_server.dto.PredictionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PredictionController.class)
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    private PredictionRequestDto buildRequest() {
        PredictionRequestDto dto = new PredictionRequestDto();
        dto.setRam(16);
        dto.setWeight(1.5);
        dto.setTouchscreen(1);
        dto.setIps(1);
        dto.setCompany("Dell");
        dto.setTypename("Ultrabook");
        dto.setOpsys("Windows");
        dto.setCpu("Intel i7");
        dto.setGpu("NVIDIA");
        return dto;
    }

    private PredictionResponseDto buildSuccessResponse() {
        PredictionResponseDto response = new PredictionResponseDto();
        response.setSuccess(true);
        response.setPrediction(1234.56);
        return response;
    }

    @Nested
    class PredictEndpoint {

        @Test
        @DisplayName("Returns downstream prediction when Flask call succeeds")
        void predict_success() throws Exception {
            PredictionRequestDto req = buildRequest();
            PredictionResponseDto response = buildSuccessResponse();

            Mockito.when(restTemplate.postForObject(eq("http://localhost:5001/predict"), eq(req), eq(PredictionResponseDto.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/predict")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.prediction").value(1234.56));
        }

        @Test
        @DisplayName("Returns error payload when Flask call throws exception")
        void predict_flaskError() throws Exception {
            PredictionRequestDto req = buildRequest();

            Mockito.when(restTemplate.postForObject(eq("http://localhost:5001/predict"), eq(req), eq(PredictionResponseDto.class)))
                    .thenThrow(new RuntimeException("Flask down"));

            mockMvc.perform(post("/api/predict")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Flask down")));
        }

        @Test
        @DisplayName("Returns 400 when request body missing required fields")
        void predict_invalidRequest() throws Exception {
            mockMvc.perform(post("/api/predict")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}

