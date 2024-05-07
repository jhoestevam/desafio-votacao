package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.domain.adapters.Ruling;
import br.challenge.dbserver.domain.adapters.repository.RulingRepository;
import br.challenge.dbserver.domain.adapters.service.RulingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DbserverRulingRepository implements RulingRepository {
    @Override
    public List<Ruling> listAll(final Boolean available) {
        return List.of();
    }

    @Override
    public Optional<Ruling> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public UUID save(Ruling ruling) {
        return null;
    }
}
