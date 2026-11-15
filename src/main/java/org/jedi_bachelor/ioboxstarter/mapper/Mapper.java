package org.jedi_bachelor.ioboxstarter.mapper;

sealed interface Mapper<E, D>
    permits DeadLetterMapper, InboxMapper, OutboxMapper {

    D toDto(final E entity);
}
