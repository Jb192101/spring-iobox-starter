package org.jedi_bachelor.ioboxstarter.mapper;

@FunctionalInterface
public interface Mapper<E, D> {
    D toDto(E entity);
}
