package ru.yanin.practice.user_service.model.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("user_core")
public record UserCore(
        @Id
        Long id,

        @Column("first_name")
        String firstName,

        @Column("second_name")
        String secondName,

        @Column("birth_date")
        LocalDate birthDate
) {
}
