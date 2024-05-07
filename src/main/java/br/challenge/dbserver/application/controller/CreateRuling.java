package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CreateRuling (String title,
                            String description,
                            @JsonProperty("end_date") LocalDate endDate) {
}
