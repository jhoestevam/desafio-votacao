package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public record CreateRuling ( UUID uuid,
                             @JsonProperty(required = true) String title,
                             @JsonProperty(required = true) String description,
                             @JsonProperty(value = "end_date", required = true) LocalDate endDate,
                             @JsonProperty(defaultValue = "OPEN") RulingStatus status){

    public CreateRuling (String title, String description, LocalDate endDate){
        this(null, title, description, endDate, RulingStatus.OPEN);
    }

    //todo: adjust the constructor to receive the uuid
    public CreateRuling (UUID uuid, String title, String description, LocalDate endDate){
        this(uuid, title, description, endDate, RulingStatus.OPEN);
    }
}
