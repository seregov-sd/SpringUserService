package by.task.services;

import by.task.exceptions.dao.DaoException;
import by.task.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserMenuManager {
    private final Scanner scanner;
    private final UserService userService;
    private boolean running = true;

    public UserMenuManager(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    public void run() {
        while (running) {
            try {
                printMenu();
                int choice = getIntInput();

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> running = false;
                    default -> System.out.println("Неверный выбор, попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\nUSER_SERVICE");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private int getIntInput() {
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private long getLongInput() {
        long input = scanner.nextLong();
        scanner.nextLine();
        return input;
    }

    private void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = getIntInput();

        User user = new User(name, email, age);
        userService.saveUser(user);
        System.out.println("Пользователь создан: " + user);
    }

    private void findUserById() {
        System.out.print("Введите ID пользователя: ");
        Long id = getLongInput();

        Optional<User> user = userService.getUserById(id);
        user.ifPresentOrElse(
                u -> System.out.println("Найден пользователь: " + u),
                () -> System.out.println("Пользователь с ID " + id + " не найден")
        );
    }

    private void findAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст");
        } else {
            System.out.println("Список пользователей:");
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = getLongInput();

        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }

        User user = optionalUser.get();
        System.out.println("Текущие данные: " + user);

        System.out.print("Введите новое имя (оставьте пустым для сохранения текущего): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) user.setName(name);

        System.out.print("Введите новый email (оставьте пустым для сохранения текущего): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Введите новый возраст (0 для сохранения текущего): ");
        int age = getIntInput();
        if (age > 0) user.setAge(age);

        userService.updateUser(user);
        System.out.println("Данные пользователя обновлены: " + user);
    }

    private void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = getLongInput();

        Optional<User> user = userService.getUserById(id);
        user.ifPresentOrElse(
                u -> {
                    userService.deleteUser(u);
                    System.out.println("Пользователь удалён: " + u);
                },
                () -> System.out.println("Пользователь с ID " + id + " не найден")
        );
    }
}