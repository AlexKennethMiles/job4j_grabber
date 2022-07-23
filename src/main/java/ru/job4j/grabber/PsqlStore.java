package ru.job4j.grabber;

import ru.job4j.grabber.until.HabrCareerDataParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDataParser());
        List<Post> pool = hcp.list("https://career.habr.com/vacancies/java_developer");
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(cfg);
        pool.forEach(psqlStore::save);
        psqlStore.getAll().forEach(System.out::println);
        System.out.println(psqlStore.findById(1));
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(name, text, link, created) values  (?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (Statement statement = cnn.createStatement()) {
            ResultSet pool = statement.executeQuery("select * from post");
            while (pool.next()) {
                rsl.add(new Post(
                        pool.getInt("id"),
                        pool.getString("name"),
                        pool.getString("text"),
                        pool.getString("link"),
                        pool.getTimestamp("created").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id=?"
        )) {
            statement.setInt(1, id);
            ResultSet pool = statement.executeQuery();
            if (pool.next()) {
                rsl = new Post(
                        pool.getInt("id"),
                        pool.getString("name"),
                        pool.getString("text"),
                        pool.getString("link"),
                        pool.getTimestamp("created").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
