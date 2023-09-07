package ru.job4j.auth.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.job4j.auth.validation.Operation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Min(value = 1, message = "Id must be not null", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class})
    private int id;
    @NotBlank(message = "Login must be not empty")
    private String login;
    @Length(min = 6, message = "Password must be more than 5 symbols")
    private String password;
}
