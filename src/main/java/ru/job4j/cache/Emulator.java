package ru.job4j.cache;

import java.nio.file.Paths;
import java.util.Scanner;

public class Emulator {
    private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Emulator e = new Emulator();
        e.startMenu();
    }

    public String initDir() {
        String dir = null;
        while (true) {
            System.out.println("Введите директорию:");
            dir = sc.nextLine();
            if (Paths.get(dir).toFile().isDirectory()) {
                break;
            } else {
                System.out.println("!!! Введена некорректная директория !!!");
            }
        }
        return dir;
    }

    public void startMenu() {
        String dir = initDir();
        AbstractCache<String, String> cache = new DirFileCache(dir);
        loop:
        while (true) {
            System.out.println("""
                    Варианты действий:
                    1 - Загрузить содержимое файла в кэш
                    2 - Получить содержимое кэша
                    Любая другая цифра - Выход из приложения
                    Ваш выбор:""");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Введите имя файла для загрузки в кэш");
                    String file = sc.next();
                    cache.put(file, cache.get(file));
                    break;
                case 2:
                    System.out.println("Введите имя файла в кэше");
                    System.out.println(cache.get(sc.next()));
                    break;
                default:
                    break loop;
            }
        }
    }
}
