package br.challenge.dbserver;

import br.challenge.dbserver.application.controller.CreateRuling;
import br.challenge.dbserver.application.controller.ResultRuling;
import br.challenge.dbserver.application.controller.VoteOnRuling;
import br.challenge.dbserver.domain.adapters.service.NotFoundRulingException;
import br.challenge.dbserver.domain.adapters.service.RulingService;
import br.challenge.dbserver.domain.adapters.service.ValidationRulingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
class RulingServiceTests {

    @Autowired
    private RulingService rulingService;


    @Test
    void testCreateRuling() throws Exception {
        final var ruling = new CreateRuling("Title", "Description", LocalDate.now().plusDays(7));
        Assert.notNull(rulingService.createRuling(ruling).toString(), "Ruling created successfully");

    }

    @Test
    void testCreateRuling_nullValue() throws Exception {
        Assert.isNull(rulingService.createRuling(null), "Ruling not created successfully");
    }

    @Test
    void testListOfResult_uuidNotPersisted() throws Exception {
        UUID uuid = UUID.randomUUID();
        List<CreateRuling> listOfRuling = rulingService.listOfRuling(uuid, true);
        Assert.isTrue(listOfRuling.isEmpty(), "List of ruling is empty");
    }

    @Test
    void testListOfRuling_withUuid() throws Exception {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        UUID rulingUuid = rulingService.createRuling(ruling);

        List<CreateRuling> listOfRuling = rulingService.listOfRuling(rulingUuid, true);
        Assert.notEmpty(listOfRuling, "List of ruling is not empty");
        Assert.isTrue(listOfRuling.size() == 1, "List of ruling has one element");
    }

    @Test
    void testResultOfRuling_notPersisted() {
        Assertions.assertThrows(NotFoundRulingException.class, () -> rulingService.resultOfRuling(UUID.randomUUID()));
    }

    @Test
    void testResultOfRuling_statusStillCountingVotes() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        VoteOnRuling voteOnRuling1 = new VoteOnRuling(rulingUuid, randomCpf(), true);
        VoteOnRuling voteOnRuling2 = new VoteOnRuling(rulingUuid, randomCpf(), true);
        VoteOnRuling voteOnRuling3 = new VoteOnRuling(rulingUuid, randomCpf(), false);

        rulingService.tallyVoteForRuling(voteOnRuling1);
        rulingService.tallyVoteForRuling(voteOnRuling2);
        rulingService.tallyVoteForRuling(voteOnRuling3);

        final var resultRuling = rulingService.resultOfRuling(rulingUuid);
        Assert.isTrue(resultRuling.rulingUuid().equals(rulingUuid), "Ruling UUID is the same");
        Assert.isTrue("Still counting votes".equals(resultRuling.result()), "Ruling is approved");
    }

    @Test
    void testResultOfRuling_statusRejected() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        VoteOnRuling voteOnRuling1 = new VoteOnRuling(rulingUuid, randomCpf(), false);
        VoteOnRuling voteOnRuling2 = new VoteOnRuling(rulingUuid, randomCpf(), false);
        VoteOnRuling voteOnRuling3 = new VoteOnRuling(rulingUuid, randomCpf(), true);

        rulingService.tallyVoteForRuling(voteOnRuling1);
        rulingService.tallyVoteForRuling(voteOnRuling2);
        rulingService.tallyVoteForRuling(voteOnRuling3);

        rulingService.closeRuling(rulingUuid);

        final var resultRuling = rulingService.resultOfRuling(rulingUuid);
        Assert.isTrue(resultRuling.rulingUuid().equals(rulingUuid), "Ruling UUID is the same");
        Assert.isTrue("Rejected".equals(resultRuling.result()), "Ruling is rejected");
    }

    @Test
    void testResultOfRuling_statusApproved() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        VoteOnRuling voteOnRuling1 = new VoteOnRuling(rulingUuid, randomCpf(), true);
        VoteOnRuling voteOnRuling2 = new VoteOnRuling(rulingUuid, randomCpf(), true);
        VoteOnRuling voteOnRuling3 = new VoteOnRuling(rulingUuid, randomCpf(), false);

        rulingService.tallyVoteForRuling(voteOnRuling1);
        rulingService.tallyVoteForRuling(voteOnRuling2);
        rulingService.tallyVoteForRuling(voteOnRuling3);

        rulingService.closeRuling(rulingUuid);

        final var resultRuling = rulingService.resultOfRuling(rulingUuid);
        Assert.isTrue(resultRuling.rulingUuid().equals(rulingUuid), "Ruling UUID is the same");
        Assert.isTrue("Approved".equals(resultRuling.result()), "Ruling is rejected");
    }

    @Test
    void testResultOfRuling_percentageZero() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);
        final var resultRuling = rulingService.resultOfRuling(rulingUuid);
        Assert.isTrue(resultRuling.rulingUuid().equals(rulingUuid), "Ruling UUID is the same");
        Assert.isTrue("Still counting votes".equals(resultRuling.result()), "Ruling is approved");
    }

    @Test
    void testOpenRuling_withinCloseRuling() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        VoteOnRuling voteOnRuling1 = new VoteOnRuling(rulingUuid, randomCpf(), false);
        rulingService.tallyVoteForRuling(voteOnRuling1);
        rulingService.closeRuling(rulingUuid);
        ResultRuling resultRuling = rulingService.resultOfRuling(rulingUuid);

        Assertions.assertEquals("Rejected", resultRuling.result());

        rulingService.openRuling(rulingUuid);
        ResultRuling newResultRuling = rulingService.resultOfRuling(rulingUuid);
        Assertions.assertEquals("Still counting votes", newResultRuling.result());
    }

    @Test
    void testVote_withinDuplicateCpf() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        final var cpf = randomCpf();
        final var voteOnRuling = new VoteOnRuling(rulingUuid, cpf, true);
        final var voteOnRuling2 = new VoteOnRuling(rulingUuid, cpf, false);

        rulingService.tallyVoteForRuling(voteOnRuling);
        Assertions.assertThrows(ValidationRulingException.class, () -> rulingService.tallyVoteForRuling(voteOnRuling2), () -> "The vote has already been registered.");
    }

    @Test
    void testVote_withinEndDateExpired() {
        final var ruling = new CreateRuling("Title 1", "Description 1", LocalDate.now().minusDays(7));
        final var rulingUuid = rulingService.createRuling(ruling);

        final var voteOnRuling = new VoteOnRuling(rulingUuid, randomCpf(), true);
        Assertions.assertThrows(ValidationRulingException.class, () -> rulingService.tallyVoteForRuling(voteOnRuling), () -> "The end date of the ruling has already expired. It is not possible to vote.");
    }

    private static final int CPF_LENGTH = 11;

    private String randomCpf() {
        Random random = new Random();
        StringBuilder cpf = new StringBuilder(CPF_LENGTH + 3); // Extra space for special characters

        for (int i = 0; i < CPF_LENGTH; i++) {
            if (i == 3 || i == 6) {
                cpf.append(".");
            }
            if (i == 9) {
                cpf.append("-");
            }
            int digit = random.nextInt(10); // generates a random number between 0 and 9
            cpf.append(digit);
        }

        return cpf.toString();
    }
}
