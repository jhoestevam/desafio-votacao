package br.challenge.dbserver.application.controller;

import br.challenge.dbserver.domain.adapters.service.RulingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "/ruling",
        headers = "X-API-Version=1",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class RulingController {

    private final RulingService rulingService;

    @Autowired
    public RulingController(RulingService rulingService) {
        this.rulingService = rulingService;
    }

    @PostMapping
    public ResponseEntity<UUID> createRuling(@Valid @RequestBody final CreateRuling createRuling) {
        return ResponseEntity.ok(rulingService.createRuling(createRuling));
    }

    @PostMapping(value = "/vote")
    public ResponseEntity<UUID> createVote(@RequestBody @Valid VoteOnRuling voteOnRuling) {
        return ResponseEntity.ok(rulingService.tallyVoteForRuling(voteOnRuling));
    }

    @GetMapping
    public ResponseEntity<List<CreateRuling>> listing(@RequestParam(required = false) final UUID uuid, @RequestParam final RulingStatus status) {
        return ResponseEntity.ok(rulingService.listOfRuling(uuid, RulingStatus.OPEN.equals(status)));
    }

    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/result")
    public ResponseEntity<ResultRuling> result(@PathVariable UUID uuid) {
        return ResponseEntity.ok(rulingService.resultOfRuling(uuid));
    }

    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/open")
    public void openRuling(@PathVariable UUID uuid) {
        rulingService.openRuling(uuid);
    }

    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/close")
    public void closeRuling(@PathVariable UUID uuid) {
        rulingService.closeRuling(uuid);
    }

}
