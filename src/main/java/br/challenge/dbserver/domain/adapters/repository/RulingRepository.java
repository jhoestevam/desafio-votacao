package br.challenge.dbserver.domain.adapters.repository;

import br.challenge.dbserver.domain.adapters.Ruling;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RulingRepository {

    List<Ruling> listAll(Boolean available);

    Optional<Ruling> findById(UUID uuid);

    UUID save(Ruling ruling);

}
