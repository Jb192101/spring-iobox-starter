package org.jedi_bachelor.ioboxstarter.db.status;

import java.util.Set;

public enum OutboxMessageStatus {

    COMPLETELY_FAILED,

    FAILED,

    PROCESSED,

    NEW,

    PENDING;

    private Set<OutboxMessageStatus> references = Set.of();

    static {
        COMPLETELY_FAILED.references = Set.of();
        FAILED.references = Set.of(PENDING, COMPLETELY_FAILED);
        PROCESSED.references = Set.of();
        NEW.references = Set.of(PENDING);
        PENDING.references = Set.of(FAILED, PROCESSED);
    }

    public static boolean hasNextStep(OutboxMessageStatus from, OutboxMessageStatus to) {
        return from.references.contains(to);
    }
}
