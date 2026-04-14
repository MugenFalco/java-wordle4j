package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.exception.InvalidWordException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        String dictionaryPath = "words_ru.txt";
        if (args.length > 0) {
            dictionaryPath = args[0];
        }

        try (PrintWriter logger = new PrintWriter("wordle.log", StandardCharsets.UTF_8);
             Scanner scanner = new Scanner(System.in)) {

            logger.println("=== Запуск Wordle ===");

            // Загрузка словаря
            WordleDictionary dictionary = new WordleDictionary(
                    WordleDictionaryLoader.loadDictionary(dictionaryPath, logger)
            );

            // Создание игры
            WordleGame game = new WordleGame(dictionary, logger);

            // Приветствие
            System.out.println("Добро пожаловать в Wordle! Угадайте слово из 5 букв.");

            // Игровой цикл
            while (!game.isGameOver()) {
                printGameState(game);
                System.out.println("\nОсталось попыток: " + game.getAttemptsLeft());
                System.out.print("Введите слово (5 русских букв) или Enter для подсказки: ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    String hint = game.getHint();
                    if (hint == null) {
                        System.out.println("Нет подходящих слов для подсказки.");
                    } else {
                        System.out.println("Подсказка: " + hint);
                    }
                    continue;
                }

                try {
                    String normalized = game.normalizeAndValidate(input);
                    GuessResult result = game.makeGuess(normalized);
                    System.out.println("Результат: " + result.getHint());

                    if (game.isWordGuessed()) {
                        printGameState(game);
                        System.out.println("Поздравляем! Вы угадали слово \"" + game.getTargetWord() + "\"!");
                        logger.println("Игрок выиграл. Слово: " + game.getTargetWord());
                        return;
                    }
                } catch (InvalidWordException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    logger.println("Некорректный ввод: " + input + " - " + e.getMessage());
                }
            }

            // Если вышли из цикла по gameOver без победы
            printGameState(game);
            System.out.println("Вы проиграли. Загаданное слово: " + game.getTargetWord());
            logger.println("Игрок проиграл. Слово: " + game.getTargetWord());

        } catch (FileNotFoundException e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
        } catch (DictionaryLoadException e) {
            System.err.println("Ошибка загрузки словаря: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Выводит в консоль историю всех попыток с подсказками.
     */
    private static void printGameState(WordleGame game) {
        List<String> guesses = game.getGuesses();
        List<String> hints = game.getHints();
        if (guesses.isEmpty()) return;

        StringBuilder sb = new StringBuilder("\n=== История попыток ===\n");
        for (int i = 0; i < guesses.size(); i++) {
            sb.append(String.format("%d. %s -> %s%n", i + 1, guesses.get(i), hints.get(i)));
        }
        sb.append("=========================");
        System.out.println(sb);
    }
}