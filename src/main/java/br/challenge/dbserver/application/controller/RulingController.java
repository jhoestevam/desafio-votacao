package br.challenge.dbserver.application.controller;

import br.challenge.dbserver.domain.adapters.service.RulingService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@OpenAPIDefinition(info = @Info(title = "Ruling API", version = "1", description = "API to manage rulings and votes"))
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

    @Operation(summary = "Create a new ruling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruling created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Ruling not found"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Ruling data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreateRuling.class)
            )
    )
    @PostMapping
    public ResponseEntity<UUID> createRuling(@Valid @RequestBody final CreateRuling createRuling) {
        return ResponseEntity.ok(rulingService.createRuling(createRuling));
    }

    @Operation(summary = "Vote on a ruling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request; Ruling is closed; Duplicated vote"),
            @ApiResponse(responseCode = "404", description = "Ruling not found")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Vote data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = VoteOnRuling.class)
            )
    )
    @PostMapping(value = "/vote")
    public ResponseEntity<UUID> createVote(@RequestBody @Valid VoteOnRuling voteOnRuling) {
        return ResponseEntity.ok(rulingService.tallyVoteForRuling(voteOnRuling));
    }

    @Operation(summary = "List all rulings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rulings listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @Parameters({
            @Parameter(
                    name = "uuid",
                    description = "Ruling UUID",
                    schema = @Schema(type = "string", format = "uuid")
            ),
            @Parameter(
                    name = "status",
                    description = "Ruling status",
                    schema = @Schema(type = "string", allowableValues = {"OPEN", "CLOSED"})
            )
    })
    @GetMapping
    public ResponseEntity<List<CreateRuling>> listing(@RequestParam(required = false) final UUID uuid, @RequestParam final RulingStatus status) {
        return ResponseEntity.ok(rulingService.listOfRuling(uuid, RulingStatus.OPEN.equals(status)));
    }

    @Operation(summary = "Get the result of a ruling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruling result retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Ruling not found")
    })
    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/result")
    public ResponseEntity<ResultRuling> result(@PathVariable UUID uuid) {
        return ResponseEntity.ok(rulingService.resultOfRuling(uuid));
    }

    @Operation(summary = "Open a ruling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruling opened successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Ruling not found")
    })
    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/open")
    public void openRuling(@PathVariable UUID uuid) {
        rulingService.openRuling(uuid);
    }

    @Operation(summary = "Close a ruling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruling closed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Ruling not found")
    })
    @GetMapping("/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}}/close")
    public void closeRuling(@PathVariable UUID uuid) {
        rulingService.closeRuling(uuid);
    }

}
