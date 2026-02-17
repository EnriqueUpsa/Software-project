package model;

import java.time.LocalDateTime;

/**
 * Audit entry for changes in an animal status.
 */
public class StatusChangeLog {
    private final String microchipId;
    private final Animal.Status oldStatus;
    private final Animal.Status newStatus;
    private final LocalDateTime timestamp;

    public StatusChangeLog(String microchipId,
                           Animal.Status oldStatus,
                           Animal.Status newStatus,
                           LocalDateTime timestamp) {
        if (microchipId == null || microchipId.isBlank()) {
            throw new IllegalArgumentException("Microchip ID is required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }

        this.microchipId = microchipId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    public StatusChangeLog(String microchipId,
                           Animal.Status oldStatus,
                           Animal.Status newStatus) {
        this(microchipId, oldStatus, newStatus, LocalDateTime.now());
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public Animal.Status getOldStatus() {
        return oldStatus;
    }

    public Animal.Status getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
