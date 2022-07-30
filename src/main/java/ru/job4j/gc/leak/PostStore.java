package ru.job4j.gc.leak;

import java.util.*;

public class PostStore {

    private static Map<Integer, Post> posts = new HashMap<>();

    public int atomicInteger = 1;

    public Post add(Post post) {
        int id = atomicInteger++;
        post.setId(id);
        posts.put(id, post);
        return post;
    }

    public void removeAll() {
        posts.clear();
    }

    public static Collection<Post> getPosts() {
        return posts.values();
    }
}
