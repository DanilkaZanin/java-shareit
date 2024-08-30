package ru.practicum.shareit;

import java.util.Random;

public class UserEmailConfiguration {
    public static String email() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            sb.append(randomChar);
        }
        sb.append("@yandex.ru");
        return sb.toString();
    }
}
