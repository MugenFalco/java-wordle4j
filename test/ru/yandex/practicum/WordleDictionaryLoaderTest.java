package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.DictionaryLoadException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    @Test
    void testLoadDictionary(@TempDir Path tempDir) throws IOException, DictionaryLoadException {
        Path dictFile = tempDir.resolve("words_ru.txt");
        Files.write(dictFile, List.of("Агент", "Ёжики", "арбуз", "короткое", "длинноеслово", "12345"),
                StandardCharsets.UTF_8);

        PrintWriter logger = new PrintWriter(System.out);
        List<String> words = WordleDictionaryLoader.loadDictionary(dictFile.toString(), logger);

        assertEquals(3, words.size(), "Должно быть загружено 3 корректных слова");
        assertTrue(words.contains("агент"));
        assertTrue(words.contains("ежики"));
        assertTrue(words.contains("арбуз"));
        assertFalse(words.contains("короткое"));
    }

    @Test
    void testLoadDictionaryEmptyFile(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);
        PrintWriter logger = new PrintWriter(System.out);

        assertThrows(DictionaryLoadException.class, () -> {
            WordleDictionaryLoader.loadDictionary(emptyFile.toString(), logger);
        });
    }

    @Test
    void testLoadDictionaryFileNotFound() {
        PrintWriter logger = new PrintWriter(System.out);
        assertThrows(DictionaryLoadException.class, () -> {
            WordleDictionaryLoader.loadDictionary("nonexistent_file.txt", logger);
        });
    }
}