package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.infrastracture.Ruling;
import br.challenge.dbserver.domain.adapters.repository.RulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresRulingRepository implements RulingRepository {

    private final SpringDataRulingRepository rulingRepository;

    @Autowired
    public PostgresRulingRepository(SpringDataRulingRepository rulingRepository) {
        this.rulingRepository = rulingRepository;
    }

    @Override
    public List<Ruling> listAll(final Boolean available) {
        return rulingRepository.findAllByAvailable(available);
    }

    @Override
    public Optional<Ruling> findById(UUID uuid) {
        return rulingRepository.findById(uuid.toString());
    }

    @Override
    public UUID save(Ruling ruling) {
        final var savedRuling = rulingRepository.save(ruling);
        return UUID.fromString(savedRuling.getUuid());
    }
}
