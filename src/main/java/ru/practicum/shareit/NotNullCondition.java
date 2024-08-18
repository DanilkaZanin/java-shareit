package ru.practicum.shareit;

import org.modelmapper.Condition;
import org.modelmapper.spi.MappingContext;

public class NotNullCondition implements Condition<Object, Object> {
    @Override
    public boolean applies(MappingContext<Object, Object> mappingContext) {
        return mappingContext.getSource() != null;
    }
}