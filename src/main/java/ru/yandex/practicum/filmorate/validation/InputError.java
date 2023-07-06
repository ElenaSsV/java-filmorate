package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class InputError {

    private final String fieldName;
    private final String message;

}
