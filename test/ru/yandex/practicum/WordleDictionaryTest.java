package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {
    private WordleDictionary dictionary;
    private PrintWriter testLogger;

    @BeforeAll
    static void init() {}

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList(
                "аббат", "агент", "арбуз", "аркан", "бабка", "заяц", "кошка", "мышка", "птица", "рыбка"
        );
        dictionary = new WordleDictionary(words);
        testLogger = new PrintWriter(System.out);
    }

    @Test
    void testNormalizeWord() {
        assertEquals("елка", WordleDictionary.normalizeWord("Ёлка"));
        assertEquals("еж", WordleDictionary.normalizeWord("Ёж"));
        assertEquals("пять", WordleDictionary.normalizeWord("ПЯТЬ"));
        assertEquals("съем", WordleDictionary.normalizeWord("съём"));
    }

    @Test
    void testIsValidWord() {
        assertTrue(WordleDictionary.isValidWord("аббат"));
        assertFalse(WordleDictionary.isValidWord("абба"));    // 4 буквы
        assertFalse(WordleDictionary.isValidWord("аббатт"));  // 6 букв
        assertFalse(WordleDictionary.isValidWord("abcde"));   // латиница
        assertTrue(WordleDictionary.isValidWord("абвгд"));
    }

    @Test
    void testGetHint() {
        assertEquals("+++++", WordleDictionary.getHint("агент", "агент"));
        assertEquals("++---", WordleDictionary.getHint("арбуз", "аркан"));
        assertEquals("^^+-^", WordleDictionary.getHint("бабка", "аббат"));
        assertEquals("-----", WordleDictionary.getHint("мышка", "белье"));
    }

    @Test
    void testFilter() {
        List<GuessResult> history = List.of(
                new GuessResult("агент", "+++++")
        );
        List<String> filtered = dictionary.filter(history);
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains("агент"));

        history = List.of(
                new GuessResult("арбуз", "++---")
        );
        filtered = dictionary.filter(history);
        assertTrue(filtered.stream().allMatch(w -> w.startsWith("ар")));
        assertFalse(filtered.contains("арбуз"));
    }
}