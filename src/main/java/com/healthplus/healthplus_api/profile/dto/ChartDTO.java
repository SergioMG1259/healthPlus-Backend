package com.healthplus.healthplus_api.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChartDTO {
    List<String> labels;
    List<Integer> counts;
}