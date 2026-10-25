package org.jedi_bachelor.ioboxstarter.properties;

public enum RealisationType {

    JAVA("java"),

    KOTLIN("kotlin");

    private final String typeName;

    RealisationType(String typeName) {
        this.typeName = typeName;
    }
}
