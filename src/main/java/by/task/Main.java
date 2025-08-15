package by.task;

import by.task.services.UserMenuManager;
import by.task.services.UserService;
import by.task.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final UserMenuManager menuManager = new UserMenuManager(scanner, userService);

    public static void main(String[] args) {
        menuManager.run();
        HibernateUtil.shutdown();
    }
}