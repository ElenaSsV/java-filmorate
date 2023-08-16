package ru.yandex.practicum.filmorate.service.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

@Service
@Slf4j
public class GenreService extends AbstractService<Genre, GenreStorage> {

    @Autowired
    public GenreService(@Qualifier("GenreDbStorage") GenreStorage storage) {
        super(storage);
    }

}
