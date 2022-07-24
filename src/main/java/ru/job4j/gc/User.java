package ru.job4j.gc;

public class User {
    private int id;
    private String name;
    private int age;

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.printf("Removed %d %s %d%n", id, name, age);
    }
}
