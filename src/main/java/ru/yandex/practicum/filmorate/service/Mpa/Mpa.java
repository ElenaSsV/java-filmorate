package ru.yandex.practicum.filmorate.service.Mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Slf4j
@Service
public class Mpa extends AbstractService<ru.yandex.practicum.filmorate.model.Mpa, MpaStorage> {

    @Autowired
    public Mpa(@Qualifier("MpaDbStorage") MpaStorage storage) {
        super(storage);
    }
}
