package com.springtdd.tddspringlbrary.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Loan {

    private long id;
    private String customer;
    private Book book;
    private LocalDate loanDate;
    private Boolean returned;

}
