package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.until.HabrCareerDataParser;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final String PAGE_NUMBER = "?page=";

    public static void main(String[] args) throws IOException {
        for (int i = 1; i < 6; i++) {
            System.out.println("===== Page " + i + " =====");
            Connection connection = Jsoup.connect(PAGE_LINK + PAGE_NUMBER + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element dateOfTheElement = row.select(".vacancy-card__date").first();
                Element date = dateOfTheElement.child(0);
                String vacancyDate = date.attr("datetime");
                HabrCareerDataParser parseDate = new HabrCareerDataParser();
                System.out.printf("%s %s %s%n %s%n",
                        vacancyName,
                        link,
                        parseDate.parse(vacancyDate),
                        retrieveDescription(link));
            });
        }
    }

    private static String retrieveDescription(String link) {
        Connection connectToVacancy = Jsoup.connect(link);
        String description = null;
        try {
            Document vacancy = connectToVacancy.get();
            Elements rows = vacancy.select(".collapsible-description");
            return rows.first().text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return description;
    }
}
