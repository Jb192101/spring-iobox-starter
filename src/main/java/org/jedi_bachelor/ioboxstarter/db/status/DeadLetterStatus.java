package org.jedi_bachelor.ioboxstarter.db.status;

import java.util.Set;

public enum DeadLetterStatus {

    NEW,

    PENDING,

    PUBLISHED;

    private Set<DeadLetterStatus> references;

    static {
        NEW.references = Set.of(PENDING);
        PENDING.references = Set.of(PUBLISHED);
        PUBLISHED.references = Set.of();
    }

    public static boolean hasNextStep(DeadLetterStatus from, DeadLetterStatus to) {
        return from.references.contains(to);
    }
}
