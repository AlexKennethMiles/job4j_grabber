package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.until.DateTimeParser;
import ru.job4j.grabber.until.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final String PAGE_NUMBER = "?page=";
    private static final int PAGE_COUNT = 5;
    private final DateTimeParser dateTimeParser;
    private final List<Post> posts = new ArrayList<>();

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
        for (int i = 1; i <= PAGE_COUNT; i++) {
            hcp.list(PAGE_LINK + PAGE_NUMBER + PAGE_COUNT);
        }
        hcp.getPosts().forEach(System.out::println);
    }

    public List<Post> getPosts() {
        return posts;
    }

    @Override
    public List<Post> list(String link) {
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(this::parsePost);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    private String retrieveDescription(String link) {
        Connection connectToVacancy = Jsoup.connect(link);
        try {
            Document vacancy = connectToVacancy.get();
            Elements rows = vacancy.select(".collapsible-description");
            return Objects.requireNonNull(rows.first()).text();
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parsePost(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        assert titleElement != null;
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        Element dateOfTheElement = row.select(".vacancy-card__date").first();
        assert dateOfTheElement != null;
        Element date = dateOfTheElement.child(0);
        LocalDateTime vacancyDate = dateTimeParser.parse(date.attr("datetime"));
        posts.add(new Post(
                vacancyName,
                retrieveDescription(vacancyLink),
                vacancyLink,
                vacancyDate)
        );
    }
}
