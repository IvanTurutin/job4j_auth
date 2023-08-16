package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.dto.PersonDto;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Контроллер пользователей
 * @see ru.job4j.auth.domain.Person
 */
@RestController
@AllArgsConstructor
@RequestMapping("/persons")
@Slf4j
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> personList = persons.findAll();
        return new ResponseEntity<>(
                personList,
                personList.isEmpty() ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return ResponseEntity.of(persons.findById(id));
    }

    @PostMapping(value = "/sign-up", consumes = {"application/json"})
    public ResponseEntity<Person> create(@RequestBody Person person) {
        checkPersonData(person);
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(
                person,
                this.persons.save(person) ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody PersonDto person) {
        if (person.password() == null) {
            throw new NullPointerException();
        }
        if (person.password().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }
        return this.persons.update(new PersonDto(person.id(), encoder.encode(person.password())))
                ? ResponseEntity.ok().build()
                : ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return this.persons.delete(person) ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }

    private void checkPersonData(Person person) {
        if (person.getPassword() == null || person.getLogin() == null) {
            throw new NullPointerException();
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }
        if (person.getLogin().isEmpty()) {
            throw new IllegalArgumentException("Invalid login. Login can't by empty.");
        }
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        log.error(e.getLocalizedMessage());
    }
}