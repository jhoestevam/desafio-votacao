package br.challenge.dbserver.domain.adapters.repository;

import br.challenge.dbserver.infrastracture.Vote;

import java.util.UUID;

public interface VoteRepository {

    boolean existsVoteByCpfAndRulingUuid(String cpf, String rulingUuid);

    UUID save(Vote vote);

}
