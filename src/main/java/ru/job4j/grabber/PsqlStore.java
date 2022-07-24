package ru.job4j.grabber;

import ru.job4j.grabber.until.HabrCareerDateTimeParser;

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
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> pool = hcp.list("https://career.habr.com/vacancies/java_developer?page=");
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PsqlStore psqlStore = new PsqlStore(cfg)) {
            pool.forEach(psqlStore::save);
            psqlStore.getAll().forEach(System.out::println);
            System.out.println(psqlStore.findById(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(name, text, link, created)"
                        + "values  (?, ?, ?, ?)"
                        + "on conflict (link) do nothing")) {
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
                rsl.add(getPostFromResultSet(pool));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id=?"
        )) {
            statement.setInt(1, id);
            ResultSet pool = statement.executeQuery();
            if (pool.next()) {
                return getPostFromResultSet(pool);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Post getPostFromResultSet(ResultSet rs) {
        try {
            return new Post(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("text"),
                    rs.getString("link"),
                    rs.getTimestamp("created").toLocalDateTime()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
