package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.domain.adapters.repository.VoteRepository;
import br.challenge.dbserver.infrastracture.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostgresVoteRepository implements VoteRepository {

    private final SpringDataVoteRepository voteRepository;

    @Autowired
    public PostgresVoteRepository(SpringDataVoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public boolean existsVoteByCpfAndRulingUuid(String cpf, String rulingUuid) {
        return voteRepository.existsVoteByCpfAndRulingUuid(cpf, rulingUuid);
    }

    @Override
    public UUID save(Vote vote) {
        return UUID.fromString(voteRepository.save(vote).getUuid());
    }
}
