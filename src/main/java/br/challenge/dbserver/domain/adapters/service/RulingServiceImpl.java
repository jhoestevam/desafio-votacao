package br.challenge.dbserver.domain.adapters.service;

import br.challenge.dbserver.application.controller.CreateRuling;
import br.challenge.dbserver.application.controller.ResultRuling;
import br.challenge.dbserver.application.controller.RulingStatus;
import br.challenge.dbserver.application.controller.VoteOnRuling;
import br.challenge.dbserver.domain.adapters.repository.VoteRepository;
import br.challenge.dbserver.infrastracture.Ruling;
import br.challenge.dbserver.domain.adapters.repository.RulingRepository;
import br.challenge.dbserver.infrastracture.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RulingServiceImpl implements RulingService {

    private final RulingRepository rulingRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public RulingServiceImpl(RulingRepository rulingRepository, VoteRepository voteRepository) {
        this.rulingRepository = rulingRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public UUID createRuling(final CreateRuling createRuling) {
        if (createRuling != null) {
            var ruling = new Ruling();
            ruling.setUuid(UUID.randomUUID().toString());
            ruling.setTitle(createRuling.title());
            ruling.setDescription(createRuling.description());
            ruling.setStartDate(LocalDate.now());
            ruling.setEndDate(createRuling.endDate());
            ruling.setVotesAgainst(0);
            ruling.setVotesInFavor(0);
            ruling.setAvailable(RulingStatus.OPEN.equals(createRuling.status()));
            return rulingRepository.save(ruling);
        }

        return null;
    }

    @Override
    public List<CreateRuling> listOfRuling(UUID uuid, Boolean available) {
        if (uuid != null) {
            return rulingRepository.findById(uuid)
                    .filter(ruling -> available.equals(ruling.isAvailable()))
                    .map(ruling -> List.of(new CreateRuling(UUID.fromString(ruling.getUuid()),
                            ruling.getTitle(),
                            ruling.getDescription(),
                            ruling.getEndDate())))
                    .orElse(List.of());
        }

        final var rulings = rulingRepository.listAll(available);
        if (!rulings.isEmpty()) {
            return rulings.stream()
                    .map(ruling -> new CreateRuling(UUID.fromString(ruling.getUuid()),
                            ruling.getTitle(),
                            ruling.getDescription(),
                            ruling.getEndDate()))
                    .toList();
        }

        return List.of();
    }

    @Override
    public ResultRuling resultOfRuling(UUID uuid) {
        return rulingRepository.findById(uuid)
                .map(ruling -> {
                    final var totalOfVotes = ruling.getVotesInFavor() + ruling.getVotesAgainst();

                    float percentageFor;
                    if (totalOfVotes == 0) {
                        percentageFor = 0f;
                    } else {
                        percentageFor = (float) ruling.getVotesInFavor() / totalOfVotes * 100;
                    }

                    final String result;
                    if (ruling.isAvailable()) {
                        result = "Still counting votes";
                    } else if (ruling.getVotesInFavor() > ruling.getVotesAgainst()) {
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
                }).orElseThrow(() -> new NotFoundRulingException("Ruling not found or still open"));
    }

    @Override
    public void openRuling(UUID uuid) {
        rulingRepository.findById(uuid)
                .map(ruling -> {
                    checkRulingClosedByDate(ruling);
                    ruling.setAvailable(true);
                    return rulingRepository.save(ruling);
                })
                .orElseThrow(() -> new NotFoundRulingException("Ruling not found"));
    }

    @Override
    public void closeRuling(UUID uuid) {
        rulingRepository.findById(uuid)
                .map(ruling -> {
                    ruling.setAvailable(false);
                    return rulingRepository.save(ruling);
                })
                .orElseThrow(() -> new NotFoundRulingException("Ruling not found"));
    }

    @Override
    @Transactional
    public UUID tallyVoteForRuling(VoteOnRuling voteOnRuling) {
        this.checkDuplicateVote(voteOnRuling);
        return rulingRepository.findById(voteOnRuling.rulingId())
                .map(ruling -> {
                    this.checkRulingClosedByDate(ruling);

                    if (ruling.isAvailable()) {

                        if (voteOnRuling.voteInFavor()) {
                            ruling.setVotesInFavor(ruling.getVotesInFavor() + 1);
                        } else {
                            ruling.setVotesAgainst(ruling.getVotesAgainst() + 1);
                        }

                        final var vote = new Vote();
                        vote.setUuid(UUID.randomUUID().toString());
                        vote.setCpf(voteOnRuling.cpf());
                        vote.setVoteInFavor(voteOnRuling.voteInFavor());
                        vote.setRuling(ruling);

                        return voteRepository.save(vote);
                    }
                    throw new ValidationRulingException("The ruling is closed. It is not possible to vote.");
                }).orElseThrow(() -> new NotFoundRulingException("Ruling not found"));
    }

    private void checkRulingClosedByDate(Ruling ruling) {
        if (ruling.getEndDate().isBefore(LocalDate.now())) {
            throw new ValidationRulingException("The end date of the ruling has already expired. It is not possible to vote.");
        }
    }

    private void checkDuplicateVote(VoteOnRuling voteOnRuling) {
        if (voteRepository.existsVoteByCpfAndRulingUuid(voteOnRuling.cpf(), voteOnRuling.rulingId().toString())) {
            throw new ValidationRulingException("The vote has already been registered.");
        }
    }
}
