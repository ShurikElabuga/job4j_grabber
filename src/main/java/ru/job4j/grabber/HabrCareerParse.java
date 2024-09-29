package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public static final int PAGENUMBER = 5;

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = document.select(".basic-section basic-section--appearance-vacancy-description");
        return row.text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= PAGENUMBER; i++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, i, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element linkDateElement = dateElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s%s", SOURCE_LINK, linkElement.attr("href"), linkDateElement.attr("datetime"));
                System.out.printf("%s %s%n", vacancyName, link);
            });
        }
    }
}
