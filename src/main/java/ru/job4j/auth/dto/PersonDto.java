package ru.job4j.auth.dto;

import org.hibernate.validator.constraints.Length;
import ru.job4j.auth.validation.Operation;

import javax.validation.constraints.Min;

public record PersonDto(
        @Min(value = 1, message = "Id must be not null", groups = {
                Operation.OnUpdate.class, Operation.OnDelete.class})
        int id,
        @Length(min = 6, message = "Password must be more than 5 symbols")
        String password
) {
}
