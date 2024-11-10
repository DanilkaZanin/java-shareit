package ru.practicum.shareit.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.NotNullCondition;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentMapperImpl;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@Configuration
public class Config {
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

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(new NotNullCondition());
        return modelMapper;
    }

    @Bean
    public CommentMapper commentMapper() {
        return new CommentMapperImpl();
    }

    @Bean
    public ItemMapper itemMapper(ModelMapper modelMapper, CommentMapper commentMapper) {
        return new ItemMapperImpl(modelMapper, commentMapper);
    }
}