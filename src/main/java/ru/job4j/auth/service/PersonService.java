package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
        try {
            personRepository.save(patch(person));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Impossible invoke set method from object : " + person + ", Check set and get pairs.");
        }
        return false;
    }

    public boolean delete(Person person) {
        return personRepository.deleteById(person.getId());
    }

    private Person patch(Person person) throws InvocationTargetException, IllegalAccessException {
        var currentOpt = personRepository.findById(person.getId());
        if (currentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var current = currentOpt.get();
        var methods = current.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method: methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Impossible invoke set method from object : " + current + ", Check set and get pairs.");
                }
                var newValue = getMethod.invoke(person);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return current;
    }


}
