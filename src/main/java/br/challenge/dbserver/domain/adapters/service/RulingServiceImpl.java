package br.challenge.dbserver.domain.adapters.service;

import br.challenge.dbserver.application.controller.CreateRuling;
import br.challenge.dbserver.application.controller.ResultRuling;
import br.challenge.dbserver.domain.adapters.Ruling;
import br.challenge.dbserver.domain.adapters.repository.RulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RulingServiceImpl implements RulingService {

    private final RulingRepository rulingRepository;

    @Autowired
    public RulingServiceImpl(RulingRepository rulingRepository) {
        this.rulingRepository = rulingRepository;
    }

    @Override
    public UUID createRuling(final CreateRuling createRuling) {
        if (createRuling != null) {
            var ruling = new Ruling();
            ruling.setTitle(createRuling.title());
            ruling.setDescription(createRuling.description());
            ruling.setStartDate(LocalDate.now());
            ruling.setEndDate(createRuling.endDate());
            ruling.setVotesAgainst(0);
            ruling.setVotesInFavor(0);
            return rulingRepository.save(ruling);
        }

        return null;
    }

    @Override
    public List<CreateRuling> listRuling(UUID uuid, Boolean available) {
        if (uuid != null) {
            return rulingRepository.findById(uuid)
                    .map(ruling -> List.of(new CreateRuling(ruling.getTitle(), ruling.getDescription(), ruling.getEndDate())))
                    .orElse(List.of());
        }

        List<Ruling> rulings = rulingRepository.listAll(available);
        if (!rulings.isEmpty()) {
            return rulings.stream().map(ruling -> new CreateRuling(ruling.getTitle(), ruling.getDescription(), ruling.getEndDate())).toList();
        }

        return List.of();
    }

    @Override
    public ResultRuling resultRuling(UUID uuid) {
        return rulingRepository.findById(uuid)
                .filter(Ruling::isAvailable)
                .map(ruling -> {
                    final var totalOfVotes = ruling.getVotesInFavor() + ruling.getVotesAgainst();
                    final float percentageFor = (float) ruling.getVotesInFavor() / totalOfVotes * 100;

                    final String result;
                    if (ruling.getVotesInFavor() > ruling.getVotesAgainst()) {
                        result = "Approved";
                    } else {
                        result = "Rejected";
                    }

                    return new ResultRuling(UUID.fromString(ruling.getUuid()),
                            totalOfVotes,
                            ruling.getVotesInFavor(),
                            ruling.getVotesAgainst(),
                            percentageFor,
                            result);
                }).orElseThrow(() -> new NotFoundRulingException("Ruling not found"));
    }
}
