package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Model to create a new ruling")
public record CreateRuling (@Schema(description = "Unique identifier of the ruling", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", hidden = true)
                            UUID uuid,

                            @Schema(description = "Title of the ruling", example = "Ruling Title")
                            @JsonProperty(required = true)
                            String title,

                            @Schema(description = "Description of the ruling", example = "Ruling Description")
                            @JsonProperty(required = true) String description,

                            @Schema(description = "End date of the ruling", example = "2021-12-31")
                            @JsonProperty(value = "end_date", required = true) LocalDate endDate,

                            @Schema(description = "Status of the ruling", example = "OPEN", defaultValue = "OPEN", allowableValues = {"OPEN", "CLOSED"})
                            @JsonProperty(defaultValue = "OPEN") RulingStatus status){

    public CreateRuling (String title, String description, LocalDate endDate){
        this(null, title, description, endDate, RulingStatus.OPEN);
    }

    public CreateRuling (UUID uuid, String title, String description, LocalDate endDate){
        this(uuid, title, description, endDate, RulingStatus.OPEN);
    }
}
