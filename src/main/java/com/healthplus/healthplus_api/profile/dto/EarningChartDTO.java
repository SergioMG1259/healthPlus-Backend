package com.healthplus.healthplus_api.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class EarningChartDTO {
    List<String> labels;
    List<BigDecimal> counts;
}
