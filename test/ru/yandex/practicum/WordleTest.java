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

public class WordleTest {

    @Test
    void testMainWithValidDictionary(@TempDir Path tempDir) throws IOException {
        Path dictFile = tempDir.resolve("words_ru.txt");
        Files.write(dictFile, List.of("агент", "арбуз", "бабка"), StandardCharsets.UTF_8);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        try {
            PrintWriter logger = new PrintWriter(new StringWriter());
            WordleDictionary dictionary = new WordleDictionary(
                    WordleDictionaryLoader.loadDictionary(dictFile.toString(), logger)
            );
            WordleGame game = new WordleGame(dictionary, logger);
            assertNotNull(game);
            assertTrue(dictionary.contains(game.getTargetWord()));
        } catch (DictionaryLoadException e) {
            fail("Словарь должен загружаться корректно: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testMainWithMissingDictionary() {
        PrintWriter logger = new PrintWriter(new StringWriter());
        assertThrows(DictionaryLoadException.class, () -> {
            WordleDictionaryLoader.loadDictionary("nonexistent_file.txt", logger);
        });
    }
}