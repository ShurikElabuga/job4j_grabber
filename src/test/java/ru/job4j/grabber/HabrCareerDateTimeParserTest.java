package ru.job4j.grabber;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void parseTest() {
        HabrCareerDateTimeParser h = new HabrCareerDateTimeParser();
        String date = "2024-09-18T11:03:12+03:00";
        LocalDateTime result = h.parse(date);
        assertThat(result).isEqualTo("2024-09-18T11:03:12");
    }
}