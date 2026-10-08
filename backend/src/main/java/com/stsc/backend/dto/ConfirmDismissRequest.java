package com.stsc.backend.dto;

import com.stsc.backend.model.ReportConfirmation;
import lombok.Data;

@Data
public class ConfirmDismissRequest {
    private Long userId;
    private ReportConfirmation.ActionType actionType; // CONFIRM or DISMISS
}
