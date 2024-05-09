package br.challenge.dbserver.application.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "Model to return the result of a ruling")
public record ResultRuling (@Schema(description = "Unique identifier of the ruling", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                            @JsonProperty("ruling_id") UUID rulingUuid,

                            @Schema(description = "Total votes", example = "100")
                            @JsonProperty("total_votes") int totalVotes,

                            @Schema(description = "Votes for", example = "60")
                            @JsonProperty("votes_for") int votesFor,

                            @Schema(description = "Votes against", example = "40")
                            @JsonProperty("votes_against") int votesAgainst,

                            @Schema(description = "Percentage for", example = "60.0")
                            @JsonProperty("percentage_for") float percentageFor,

                            @Schema(description = "Result", example = "Approved", allowableValues = {"Approved", "Rejected, Still counting votes"})
                            String result) {

}