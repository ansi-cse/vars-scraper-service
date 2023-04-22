package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagingDTO<T> {
    Integer total;
    List<T> items;
}
