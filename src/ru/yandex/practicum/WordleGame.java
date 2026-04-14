package ru.yandex.practicum;

import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;
import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final WordleDictionary dictionary;
    private final String targetWord;
    private int attemptsLeft;
    private static final int MAX_ATTEMPTS = 6;
    private final List<String> guesses;
    private final List<String> hints;
    private final Set<String> usedHintWords;
    private final Set<String> guessedWords;
    private final PrintWriter logger;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        List<String> words = dictionary.getWords();
        this.targetWord = words.get(new Random().nextInt(words.size()));
        this.attemptsLeft = MAX_ATTEMPTS;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.usedHintWords = new HashSet<>();
        this.guessedWords = new HashSet<>();
        logger.println("Игра начата. Загаданное слово: " + targetWord);
    }
    //конструктор для тестов
    public WordleGame(WordleDictionary dictionary, String targetWord, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.targetWord = targetWord;
        this.attemptsLeft = MAX_ATTEMPTS;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.usedHintWords = new HashSet<>();
        this.guessedWords = new HashSet<>();
        logger.println("Игра начата. Загаданное слово: " + targetWord);
    }

    public int getAttemptsLeft() { return attemptsLeft; }
    public String getTargetWord() { return targetWord; }
    public List<String> getGuesses() { return Collections.unmodifiableList(guesses); }
    public List<String> getHints() { return Collections.unmodifiableList(hints); }
    public boolean isGameOver() { return attemptsLeft == 0 || isWordGuessed(); }
    public boolean isWordGuessed() { return guessedWords.contains(targetWord); }

    public GuessResult makeGuess(String guess) throws InvalidWordException {
        if (isGameOver()) {
            throw new IllegalStateException("Игра уже завершена");
        }
        if (guessedWords.contains(guess)) {
            logger.println("Повторный ввод слова: " + guess);
            throw new InvalidWordException("Это слово уже было введено ранее");
        }
        String hint = WordleDictionary.getHint(guess, targetWord);
        guesses.add(guess);
        hints.add(hint);
        attemptsLeft--;
        guessedWords.add(guess);
        logger.println("Догадка: " + guess + " -> " + hint);
        return new GuessResult(guess, hint);
    }

    public String getHint() {
        List<GuessResult> history = buildHistory();
        List<String> candidates = dictionary.filter(history);
        Set<String> used = new HashSet<>(guesses);
        used.addAll(usedHintWords);
        candidates.removeAll(used);
        if (candidates.isEmpty()) {
            return null;
        }
        String hintWord = candidates.getFirst();
        usedHintWords.add(hintWord);
        logger.println("Выдана подсказка: " + hintWord);
        return hintWord;
    }

    List<GuessResult> buildHistory() {
        List<GuessResult> history = new ArrayList<>();
        for (int i = 0; i < guesses.size(); i++) {
            history.add(new GuessResult(guesses.get(i), hints.get(i)));
        }
        return history;
    }

    public String normalizeAndValidate(String input) throws InvalidWordException {
        if (input == null || input.isBlank()) {
            throw new InvalidWordException("Ввод не может быть пустым");
        }
        String normalized = WordleDictionary.normalizeWord(input);
        if (!WordleDictionary.isValidWord(normalized)) {
            throw new InvalidWordException("Слово должно состоять из 5 русских букв");
        }
        if (!dictionary.contains(normalized)) {
            throw new WordNotFoundInDictionaryException(normalized);
        }
        return normalized;
    }
}