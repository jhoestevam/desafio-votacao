package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ResultRuling (@JsonProperty("ruling_id") UUID rulingUuid,
                            @JsonProperty("total_votes") int totalVotes,
                            @JsonProperty("votes_for") int votesFor,
                            @JsonProperty("votes_against") int votesAgainst,
                            @JsonProperty("percentage_for") float percentageFor,
                            String result) {

}