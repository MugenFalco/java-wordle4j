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
    private final Set<Character> absentLetters;
    private final Set<Character> presentLetters;
    private final List<Character> correctPositions;

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
        this.absentLetters = new HashSet<>();
        this.presentLetters = new HashSet<>();
        this.correctPositions = new ArrayList<>();
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            correctPositions.add(null);
        }
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
        this.absentLetters = new HashSet<>();
        this.presentLetters = new HashSet<>();
        this.correctPositions = new ArrayList<>();
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            correctPositions.add(null);
        }
        logger.println("Игра начата. Загаданное слово: " + targetWord);
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public List<String> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public List<String> getHints() {
        return Collections.unmodifiableList(hints);
    }

    public boolean isGameOver() {
        return attemptsLeft == 0 || isWordGuessed();
    }

    public boolean isWordGuessed() {
        return guessedWords.contains(targetWord);
    }

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
        updateCharState(guess, hint);

        logger.println("Догадка: " + guess + " -> " + hint);
        return new GuessResult(guess, hint);
    }

    private void updateCharState(String guess, String hint) {
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            char c = guess.charAt(i);
            char hintChar = hint.charAt(i);
            if (hintChar == '+') {
                correctPositions.set(i, c);
                presentLetters.add(c);
            } else if (hintChar == '^') {
                presentLetters.add(c);
            } else if (hintChar == '-') {
                if (!presentLetters.contains(c) && !correctPositions.contains(c)) {
                    absentLetters.add(c);
                }
            }
        }
    }

    public String getHint() {
        Set<String> used = new HashSet<>(guessedWords);
        used.addAll(usedHintWords);

        for (String word : dictionary.getWords()) {
            if (used.contains(word)) {
                continue;
            }

            boolean positionsOk = true;
            for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
                Character correctChar = correctPositions.get(i);
                if (correctChar != null && word.charAt(i) != correctChar) {
                    positionsOk = false;
                    break;
                }
            }
            if (!positionsOk) continue;

            boolean noAbsent = true;
            for (char ch : absentLetters) {
                if (word.indexOf(ch) >= 0) {
                    noAbsent = false;
                    break;
                }
            }
            if (!noAbsent) continue;

            boolean allPresent = true;
            for (char ch : presentLetters) {
                if (word.indexOf(ch) < 0) {
                    allPresent = false;
                    break;
                }
            }
            if (!allPresent) continue;

            usedHintWords.add(word);
            logger.println("Выдана подсказка: " + word);
            return word;
        }

        return null;
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