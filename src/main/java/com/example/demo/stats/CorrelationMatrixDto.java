package com.example.demo.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CorrelationMatrixDto {
    private List<String> labels;
    private double[][] matrix;
}
