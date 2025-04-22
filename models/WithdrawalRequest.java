package models;

import java.time.LocalDateTime;
import java.util.UUID;
import enumeration.BTOApplicationStatus;

public class WithdrawalRequest {
    private String requestId;
    private BTOApplication application;
    private LocalDateTime requestedAt;
    private boolean approved;
    private LocalDateTime processedAt;
    private String processedBy;

    public WithdrawalRequest(String requestId, BTOApplication application) {
        this.requestId = requestId;
        this.application = application;
        this.requestedAt = LocalDateTime.now();
        this.approved = false;
        this.processedAt = null;
        this.processedBy = null;
    }

    public WithdrawalRequest(BTOApplication application) {
        this(generateRequestId(), application);
    }

    private static String generateRequestId() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "WDR-" + date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getRequestId() {
        return requestId;
    }

    public BTOApplication getApplication() {
        return application;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public void approve(String processedBy) {
        if (this.processedAt != null) {
            throw new IllegalStateException("This withdrawal request has already been processed.");
        }
        this.approved = true;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
        this.application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
    }

    public void reject(String processedBy) {
        if (this.processedAt != null) {
            throw new IllegalStateException("This withdrawal request has already been processed.");
        }
        this.approved = false;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
    }

    public boolean isPending() {
        return this.processedAt == null;
    }

    public String getStatus() {
        if (this.processedAt == null) {
            return "Pending";
        }
        return this.approved ? "Approved" : "Rejected";
    }
}