package com.benhunterlearn.springcrudreredux.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CountDto {
    private Long count;
}
