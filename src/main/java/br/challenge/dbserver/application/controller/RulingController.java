package br.challenge.dbserver.application.controller;

import br.challenge.dbserver.domain.adapters.service.RulingService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ruling")
public class RulingController {

    private final RulingService rulingService;

    @Autowired
    public RulingController(RulingService rulingService) {
        this.rulingService = rulingService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createRuling(@RequestBody final CreateRuling createRuling) {
        final var id = rulingService.createRuling(createRuling);
        return ResponseEntity.ok(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CreateRuling>> listRuling(@RequestParam(required = false) final UUID uuid,
                                                         @RequestParam final RulingStatus status) {

        final var listOfAllRuling = rulingService.listRuling(uuid, RulingStatus.OPEN.equals(status));
        return ResponseEntity.ok(listOfAllRuling);
    }

    @PostMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String voteRuling() {
        throw new NotImplementedException("Not implemented yet");
    }

    @RequestMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/result")
        public ResponseEntity<ResultRuling> resultRuling(@RequestParam UUID uuid) {
        final var resultRuling = rulingService.resultRuling(uuid);
        return ResponseEntity.ok(resultRuling);
    }

    @RequestMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/open")
    public String openRuling() {
        throw new NotImplementedException("Not implemented yet");
    }

    @RequestMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/close")
    public String closeRuling() {
        throw new NotImplementedException("Not implemented yet");
    }

}
