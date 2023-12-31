package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис пользователей
 * @see ru.job4j.auth.domain.Person
 */
@ThreadSafe
@Service
@AllArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public boolean save(Person person) {
        try {
            return personRepository.save(person).getId() != 0;
        } catch (Exception e) {
            log.error("Exception at PersonRepository.save()", e);
        }
        return false;
    }

    public boolean update(Person person) {
        if (findById(person.getId()).isPresent()) {
            personRepository.save(person);
            return true;
        }
        return false;
    }

    public boolean update(PersonDto person) {
        Optional<Person> personDb = findById(person.id());
        if (personDb.isEmpty()) {
            return false;
        }
        personDb.get().setPassword(person.password());
        personRepository.save(personDb.get());
        return true;
    }

    public boolean delete(Person person) {
        return personRepository.deleteById(person.getId());
    }
}
