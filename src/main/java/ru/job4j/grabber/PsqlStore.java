package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement("insert into post(name, text, link, created) values (?, ?, ?, ?)"
                        + "on conflict (link)"
                        + "do nothing",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
               while (resultSet.next()) {
                   post.setId(resultSet.getInt("id"));
               }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> allPosts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    allPosts.add(new Post(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allPosts;
    }

    @Override
    public Post findById(int id) {
        List<Post> result = getAll();
        for (Post post : result) {
            if (id == post.getId()) {
                return post;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        try (InputStream input = PsqlStore.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            config.load(input);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try (Store psql = new PsqlStore(config)) {
            String link = String.format("%s%s%s", HabrCareerParse.SOURCE_LINK, HabrCareerParse.PREFIX, HabrCareerParse.SUFFIX);
            List<Post> list = new HabrCareerParse(new HabrCareerDateTimeParser()).list(link);
            list.forEach(psql :: save);
            List<Post> posts = psql.getAll();
            for (Post p : posts) {
                System.out.println(p);
            }
            System.out.println(psql.getAll().size());
            System.out.println(psql.findById(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
