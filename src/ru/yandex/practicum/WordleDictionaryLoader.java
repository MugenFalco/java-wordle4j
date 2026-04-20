package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    public static List<String> loadDictionary(String filePath, PrintWriter logger)
            throws DictionaryLoadException {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line.trim());
                if (WordleDictionary.isValidWord(normalized)) {
                    words.add(normalized);
                }
            }
        } catch (FileNotFoundException e) {
            logger.println("Ошибка: файл словаря не найден - " + filePath);
            throw new DictionaryLoadException("Файл словаря не найден", e);
        } catch (IOException e) {
            logger.println("Ошибка ввода-вывода при чтении словаря");
            throw new DictionaryLoadException("Ошибка чтения словаря", e);
        }

        if (words.isEmpty()) {
            logger.println("Словарь пуст или не содержит корректных 5-буквенных слов");
            throw new DictionaryLoadException("Словарь пуст");
        }

        logger.println("Словарь загружен. Слов: " + words.size());
        return words;
    }
}