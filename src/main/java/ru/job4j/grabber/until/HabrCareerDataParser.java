package ru.job4j.grabber.until;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HabrCareerDataParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        return ZonedDateTime.parse(parse).toLocalDateTime();
    }
}
