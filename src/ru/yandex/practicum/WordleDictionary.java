package ru.yandex.practicum;

import java.util.*;
import java.util.stream.Collectors;

public class WordleDictionary {
    private final List<String> words;
    public static final int WORD_LENGTH = 5;
    private static final String RUSSIAN_LETTERS = "[а-я]+";

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public List<String> filter(List<GuessResult> history) {
        return words.stream()
                .filter(word -> isCompatible(word, history))
                .collect(Collectors.toList());
    }

    private boolean isCompatible(String candidate, List<GuessResult> history) {
        for (GuessResult gr : history) {
            String guess = gr.getGuess();
            String hint = gr.getHint();

            boolean[] candidateUsed = new boolean[WORD_LENGTH];

            for (int i = 0; i < WORD_LENGTH; i++) {
                if (hint.charAt(i) == '+') {
                    if (candidate.charAt(i) != guess.charAt(i)) return false;
                    candidateUsed[i] = true;
                }
            }

            for (int i = 0; i < WORD_LENGTH; i++) {
                if (hint.charAt(i) == '^') {
                    char gChar = guess.charAt(i);
                    boolean found = false;
                    for (int j = 0; j < WORD_LENGTH; j++) {
                        if (j != i && !candidateUsed[j] && candidate.charAt(j) == gChar) {
                            candidateUsed[j] = true;
                            found = true;
                            break;
                        }
                    }
                    if (!found) return false;
                }
            }

            for (int i = 0; i < WORD_LENGTH; i++) {
                if (hint.charAt(i) == '-') {
                    char gChar = guess.charAt(i);
                    for (int j = 0; j < WORD_LENGTH; j++) {
                        if (!candidateUsed[j] && candidate.charAt(j) == gChar) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static String getHint(String guess, String target) {
        char[] result = new char[WORD_LENGTH];
        Arrays.fill(result, '-');
        boolean[] matched = new boolean[WORD_LENGTH];
        Map<Character, Integer> remaining = new HashMap<>();

        // Точные совпадения
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = '+';
                matched[i] = true;
            }
        }

        // Считаем символы target, не участвующие в точных совпадениях
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (!matched[i]) {
                char ch = target.charAt(i);
                remaining.put(ch, remaining.getOrDefault(ch, 0) + 1);
            }
        }

        // Находим символы, которые есть в слове, но не на своём месте
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '-') {
                char gChar = guess.charAt(i);
                Integer count = remaining.get(gChar);
                if (count != null && count > 0) {
                    result[i] = '^';
                    remaining.put(gChar, count - 1);
                }
            }
        }
        return new String(result);
    }

    public static String normalizeWord(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replace('ё', 'е')
                .replaceAll("\\s+", "");
    }

    public static boolean isValidWord(String word) {
        return word != null && word.length() == WORD_LENGTH && word.matches(RUSSIAN_LETTERS);
    }
}