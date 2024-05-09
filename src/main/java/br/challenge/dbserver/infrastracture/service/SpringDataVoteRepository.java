package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.infrastracture.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataVoteRepository extends JpaRepository<Vote, String>{
    boolean existsVoteByCpfAndRulingUuid(String cpf, String rulingUuid);
}
