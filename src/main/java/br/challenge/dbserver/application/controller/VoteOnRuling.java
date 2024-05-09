package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VoteOnRuling(@JsonProperty(value = "ruling_id", required = true) UUID rulingId,
                           @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF must be in the format XXX.XXX.XXX-XX") @JsonProperty(required = true) String cpf,
                           @JsonProperty(value = "vote_in_favor", required = true) boolean voteInFavor) {
}
