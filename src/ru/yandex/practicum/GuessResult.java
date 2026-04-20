package ru.yandex.practicum;

public class GuessResult {
    private final String guess;  // введённое слово (приведенное к общему формату)
    private final String hint;   // подсказка (5 символов)

     //guess введённое слово
     //hint  символьная подсказка

    public GuessResult(String guess, String hint) {
        this.guess = guess;
        this.hint = hint;
    }

    public String getGuess() { // введенное слово
        return guess;
    }

    public String getHint() { // строка подсказки
        return hint;
    }
}