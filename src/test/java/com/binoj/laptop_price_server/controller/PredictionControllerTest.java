package com.binoj.laptop_price_server.controller;

import com.binoj.laptop_price_server.dto.PredictionRequestDto;
import com.binoj.laptop_price_server.dto.PredictionResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PredictionControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PredictionController controller;

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

    @Test
    @DisplayName("predict returns Flask response when downstream call succeeds")
    void predict_success() {
        PredictionRequestDto req = buildRequest();
        PredictionResponseDto expected = buildSuccessResponse();

        Mockito.when(restTemplate.postForObject(eq("http://localhost:5001/predict"), eq(req), eq(PredictionResponseDto.class)))
                .thenReturn(expected);

        PredictionResponseDto actual = controller.predict(req);

        assertThat(actual).isEqualTo(expected);
        verify(restTemplate).postForObject("http://localhost:5001/predict", req, PredictionResponseDto.class);
    }

    @Test
    @DisplayName("predict returns error payload when Flask call throws exception")
    void predict_whenFlaskErrors_returnsErrorResponse() {
        PredictionRequestDto req = buildRequest();

        Mockito.when(restTemplate.postForObject(eq("http://localhost:5001/predict"), eq(req), eq(PredictionResponseDto.class)))
                .thenThrow(new RuntimeException("Flask down"));

        PredictionResponseDto actual = controller.predict(req);

        assertThat(actual.isSuccess()).isFalse();
        assertThat(actual.getError()).contains("Flask down");
        verify(restTemplate).postForObject("http://localhost:5001/predict", req, PredictionResponseDto.class);
    }

    @Test
    @DisplayName("predict propagates null response gracefully")
    void predict_whenFlaskReturnsNull() {
        PredictionRequestDto req = buildRequest();

        Mockito.when(restTemplate.postForObject(eq("http://localhost:5001/predict"), eq(req), eq(PredictionResponseDto.class)))
                .thenReturn(null);

        PredictionResponseDto actual = controller.predict(req);

        assertThat(actual).isNull();
        verify(restTemplate).postForObject("http://localhost:5001/predict", req, PredictionResponseDto.class);
    }
}
