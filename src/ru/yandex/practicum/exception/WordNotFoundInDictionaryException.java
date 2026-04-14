package ru.yandex.practicum.exception;

// Исключение, сигнализирующее о том, что введённое слово отсутствует в загруженном словаре.

public class WordNotFoundInDictionaryException extends InvalidWordException {

    public WordNotFoundInDictionaryException(String word) {
        super("Слово '" + word + "' отсутствует в словаре");
    }
}