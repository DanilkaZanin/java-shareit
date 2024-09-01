package ru.practicum.shareit;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryConfig {
    @Bean
    public Map<Long, Item> items() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, User> users() {
        return new HashMap<>();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(new NotNullCondition());
        return modelMapper;
    }
}