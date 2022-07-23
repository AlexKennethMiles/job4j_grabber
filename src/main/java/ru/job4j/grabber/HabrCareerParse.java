package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.until.DateTimeParser;
import ru.job4j.grabber.until.HabrCareerDataParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final String PAGE_NUMBER = "?page=";
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDataParser());
        hcp.list(PAGE_LINK).forEach(System.out::println);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            try {
                Connection connection = Jsoup.connect(link + PAGE_NUMBER + i);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    Element dateOfTheElement = row.select(".vacancy-card__date").first();
                    Element date = dateOfTheElement.child(0);
                    LocalDateTime vacancyDate = dateTimeParser.parse(date.attr("datetime"));
                    HabrCareerDataParser parseDate = new HabrCareerDataParser();
                    rsl.add(new Post(
                            vacancyName,
                            vacancyLink,
                            retrieveDescription(vacancyLink),
                            vacancyDate)
                    );
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsl;
    }

    private static String retrieveDescription(String link) {
        Connection connectToVacancy = Jsoup.connect(link);
        try {
            Document vacancy = connectToVacancy.get();
            Elements rows = vacancy.select(".collapsible-description");
            return rows.first().text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
