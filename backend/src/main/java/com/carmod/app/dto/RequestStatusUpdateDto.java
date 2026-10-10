package com.carmod.app.dto;

import com.carmod.app.entity.ModificationRequest.RequestStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RequestStatusUpdateDto {

    private Long requestId;
    private RequestStatus status;
    private String adminNotes;
    private String rejectionReason;
    private BigDecimal finalCost;
}
