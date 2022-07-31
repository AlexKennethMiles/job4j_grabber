package ru.job4j.cache;

import java.nio.file.Paths;
import java.util.Scanner;

public class Emulator {
    private static final String MENU = """
            Варианты действий:
            1 - Загрузить содержимое файла в кэш
            2 - Получить содержимое кэша
            Любая другая цифра - Выход из приложения
            Ваш выбор:""";
    private static final String ERR_MSG = "!!! Введена некорректная директория !!!";
    private static final String DIR_MSG = "Введите директорию:";
    private static final String FILE_NAME = "Введите имя файла для загрузки в кэш:";
    private static final String CACHE_NAME = "Введите имя файла в кэше:";
    private static final int UPLOAD_TO_CACHE = 1;
    private static final int GET_FROM_CACHE = 2;

    private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Emulator e = new Emulator();
        e.startMenu();
    }

    public String initDir() {
        String dir = null;
        while (true) {
            System.out.println(DIR_MSG);
            dir = sc.nextLine();
            if (Paths.get(dir).toFile().isDirectory()) {
                break;
            } else {
                System.out.println(ERR_MSG);
            }
        }
        return dir;
    }

    public void startMenu() {
        String dir = initDir();
        AbstractCache<String, String> cache = new DirFileCache(dir);
        loop:
        while (true) {
            System.out.println(MENU);
            int choice = sc.nextInt();
            switch (choice) {
                case UPLOAD_TO_CACHE:
                    System.out.println(FILE_NAME);
                    String file = sc.next();
                    cache.put(file, cache.get(file));
                    break;
                case GET_FROM_CACHE:
                    System.out.println(CACHE_NAME);
                    System.out.println(cache.get(sc.next()));
                    break;
                default:
                    break loop;
            }
        }
    }
}
