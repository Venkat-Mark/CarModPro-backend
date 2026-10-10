package com.carmod.app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModificationRequestDto {

    @NotNull(message = "Please select a vehicle")
    private Long vehicleId;

    private String customerNotes;

    @NotEmpty(message = "Please select at least one service")
    private List<Long> serviceIds;
}
