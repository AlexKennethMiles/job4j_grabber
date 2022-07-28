package ru.job4j.gc.prof;

import java.util.Random;
import java.util.Scanner;

public class GCApplication {
    public static void main(String[] args) {
        RandomArray ra = new RandomArray(new Random());
        int choice = showMenu();
        loop:
        while (true) {
            switch (choice) {
                case 1:
                    System.out.println("=== Выполняется заполнение массива ===");
                    ra.insert(250000);
                    System.out.println("Величина массива: " + ra.array.length);
                    break;
                case 2:
                    if (ra.array != null) {
                        System.out.println("=== Выполняется сортировка пузырьком ===");
                        new BubbleSort().sort(ra.getClone());
                    } else {
                        System.out.println("!!! Для начала заполните массив !!!");
                    }
                    break;
                case 3:
                    if (ra.array != null) {
                        System.out.println("=== Выполняется сортировка вставками ===");
                        new InsertSort().sort(ra);
                    } else {
                        System.out.println("!!! Для начала заполните массив !!!");
                    }
                    break;
                case 4:
                    if (ra.array != null) {
                        System.out.println("=== Выполняется сортировка слиянием ===");
                        new MergeSort().sort(ra);
                    } else {
                        System.out.println("!!! Для начала заполните массив !!!");
                    }
                    break;
                case 5:
                    if (ra.array != null) {
                        System.out.println("=== Выход из программы ===");
                    } else {
                        System.out.println("!!! Для начала заполните массив !!!");
                    }
                    break loop;
                default:
                    System.out.println("!!! Проверьте правильность ввода !!!");
            }
            choice = showMenu();
        }
    }

    public static int showMenu() {
        System.out.println("1. Создание массива");
        System.out.println("2. Сортировка пузырьком");
        System.out.println("3. Сортировка вставками");
        System.out.println("4. Сортировка слиянием");
        System.out.println("5. Выход");
        System.out.print("Выберите действие:");
        Scanner in = new Scanner(System.in);
        return in.nextInt();
    }
}
