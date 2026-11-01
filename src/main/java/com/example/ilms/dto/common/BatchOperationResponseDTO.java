package com.example.ilms.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperationResponseDTO {
    private int totalRequested;
    private int successCount;
    private int failedCount;
    private List<UUID> successIds;
    private List<FailedItem> failedItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedItem {
        private UUID id;
        private String reason;
    }
}