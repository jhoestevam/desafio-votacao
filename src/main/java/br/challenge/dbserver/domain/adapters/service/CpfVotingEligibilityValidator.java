package br.challenge.dbserver.domain.adapters.service;

import br.challenge.dbserver.application.controller.VoteOnRuling;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CpfVotingEligibilityValidator {

    private static final Logger logger = Logger.getLogger(CpfVotingEligibilityValidator.class.getName());

    public static Optional<UUID> validate(final VoteOnRuling voteOnRuling, Consumer<VoteOnRuling> checkDuplicateVote) {
        checkDuplicateVote.accept(voteOnRuling);

        String baseUrl = System.getenv("CPF_VALIDATOR_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            return Optional.of(voteOnRuling.rulingId());
        }


        return Optional.of(voteOnRuling)
                .map(VoteOnRuling::cpf)
                .map(cpf -> String.format("%s/users/%s", baseUrl, cpf))
                .map(url -> {
                    try {
                        final var restTemplate = new RestTemplate();
                        return restTemplate.exchange(url, HttpMethod.GET, null, VoterInfoResponse.class);
                    } catch (HttpClientErrorException exception) {
                        logger.log(Level.WARNING, exception.getMessage());
                        throw exception;
                    }
                })
                .filter(response -> response != null && response.getStatusCode().is2xxSuccessful())
                .map(response -> {
                    return switch (response.getBody().status()) {
                        case "ABLE_TO_VOTE" -> Optional.of(voteOnRuling.rulingId());
                        case "UNABLE_TO_VOTE" -> throw new ValidationRulingException("CPF is not able to vote.");
                        default -> throw new ValidationRulingException("CPF validation status not found.");
                    };
                })
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Problem on the API CPF validation. "));
    }

    private record VoterInfoResponse (String status) {
    }
}
