package ru.yandex.practicum;

import java.util.*;
import java.util.stream.Collectors;

public class WordleDictionary {
    private final List<String> words;

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

            boolean[] candidateUsed = new boolean[5];

            for (int i = 0; i < 5; i++) {
                if (hint.charAt(i) == '+') {
                    if (candidate.charAt(i) != guess.charAt(i)) return false;
                    candidateUsed[i] = true;
                }
            }

            for (int i = 0; i < 5; i++) {
                if (hint.charAt(i) == '^') {
                    char gChar = guess.charAt(i);
                    boolean found = false;
                    for (int j = 0; j < 5; j++) {
                        if (j != i && !candidateUsed[j] && candidate.charAt(j) == gChar) {
                            candidateUsed[j] = true;
                            found = true;
                            break;
                        }
                    }
                    if (!found) return false;
                }
            }

            for (int i = 0; i < 5; i++) {
                if (hint.charAt(i) == '-') {
                    char gChar = guess.charAt(i);
                    for (int j = 0; j < 5; j++) {
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
        char[] result = new char[5];
        Arrays.fill(result, '-');
        boolean[] matched = new boolean[5];
        int[] targetCharCounts = new int['я' - 'а' + 1];

        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = '+';
                matched[i] = true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (!matched[i]) {
                targetCharCounts[target.charAt(i) - 'а']++;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == '-') {
                char gChar = guess.charAt(i);
                int idx = gChar - 'а';
                if (targetCharCounts[idx] > 0) {
                    result[i] = '^';
                    targetCharCounts[idx]--;
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
        return word != null && word.length() == 5 && word.matches("[а-я]+");
    }
}