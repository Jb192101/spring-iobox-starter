package org.jedi_bachelor.ioboxstarter.mapper;

sealed public interface Mapper<E, D>
    permits DeadLetterMapper, InboxMapper, OutboxMapper {

    D toDto(E entity);
}
