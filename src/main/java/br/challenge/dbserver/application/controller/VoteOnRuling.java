package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema(name = "Model to vote on a ruling")
public record VoteOnRuling(@Schema(description = "Unique identifier of the ruling", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                           @JsonProperty(value = "ruling_id", required = true) UUID rulingId,

                           @Schema(description = "CPF of the voter", example = "123.456.789-00")
                           @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF must be in the format XXX.XXX.XXX-XX") @JsonProperty(required = true) String cpf,

                           @Schema(description = "Vote in favor of the ruling", example = "true")
                           @JsonProperty(value = "vote_in_favor", required = true) boolean voteInFavor) {
}
