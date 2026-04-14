package ru.yandex.practicum.exception;

 //Исключение, выбрасываемое при невозможности загрузить словарь.
public class DictionaryLoadException extends WordleException {

    public DictionaryLoadException(String message) {
        super(message);
    }

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}