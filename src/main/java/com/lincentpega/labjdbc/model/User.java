package com.lincentpega.labjdbc.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private String name;
    @Min(1)
    private Long age;
}
