package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.InvalidWordException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleGameTest {
    private WordleDictionary dictionary;
    private PrintWriter testLogger;

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("агент", "арбуз", "бабка", "зайка", "кошка", "мышка", "птица", "рыбка");
        dictionary = new WordleDictionary(words);
        testLogger = new PrintWriter(new StringWriter());
    }

    @Test
    void testGameInitialization() {
        WordleGame game = new WordleGame(dictionary, testLogger);
        assertTrue(dictionary.contains(game.getTargetWord()));
        assertEquals(6, game.getAttemptsLeft());
    }

    @Test
    void testHintWordSelection() throws InvalidWordException {
        WordleDictionary dict = new WordleDictionary(List.of("арбуз", "арена", "армия", "берег", "евнух", "рыбак"));
        WordleGame game = new WordleGame(dict, "арена", testLogger);
        game.makeGuess("арбуз");  // теперь исключение объявлено в сигнатуре
        List<GuessResult> history = game.buildHistory();
        List<String> candidates = dict.filter(history);
        assertTrue(candidates.stream().allMatch(w -> w.startsWith("ар")));
    }
}