package br.challenge.dbserver;

import br.challenge.dbserver.application.controller.CreateRuling;
import br.challenge.dbserver.application.controller.VoteOnRuling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RulingControllerTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private static final String REGEX_PATTERN = "[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}";

    @Autowired
    public RulingControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void listingRuling_onlyOpenStatus() throws Exception {
        this.mockMvc.perform(get("/ruling?status=OPEN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-Version", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listingRuling_onlyCloseStatus() throws Exception {
        this.mockMvc.perform(get("/ruling?status=CLOSE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-Version", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listingRuling_withinInvalidStatusParam() throws Exception {
        this.mockMvc.perform(get("/ruling?status=INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-Version", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listingRuling_withoutRequiredParam() throws Exception {
        this.mockMvc.perform(get("/ruling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-Version", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRuling() throws Exception {
        buildResultActionsForRuling()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN)));
    }

    @Test
    void createRuling_withinInvalidApiVersion() throws Exception {
        final var ruling1 = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        this.mockMvc.perform(post("/ruling")
                    .header("X-API-Version", "2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(ruling1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRuling_withinInvalidDescription() throws Exception {
        final var payloadWithoutDescription = "{\"title\":\"Title 1\", \"end_date\": \"2024-05-14\"}";
        this.mockMvc.perform(post("/ruling")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWithoutDescription))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("JSON parse error: Missing required creator property 'description' (index 2)"));
    }

    @Test
    void createRuling_withinInvalidEndDate() throws Exception {
        final var payloadWithoutEndDate = "{\"title\":\"Title 1\",\"description\":\"Description 1\"}";
        this.mockMvc.perform(post("/ruling")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWithoutEndDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("JSON parse error: Missing required creator property 'end_date' (index 3)"));
    }

    @Test
    void createRuling_withinInvalidTitle() throws Exception{
        final var payloadWithoutTitle = "{\"description\": \"John Doe\", \"end_date\": \"2024-05-14\"}";
        this.mockMvc.perform(post("/ruling")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWithoutTitle))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("JSON parse error: Missing required creator property 'title' (index 1)"));
    }

    @Test
    void voteOnRuling_withinInvalidCpf() throws Exception {
        final var payload = "{\"ruling_id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"cpf\": \"123.456.-00\", \"vote_in_favor\": \"true\"}";
        this.mockMvc.perform(post("/ruling/vote")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.cpf").value("CPF must be in the format XXX.XXX.XXX-XX"));
    }

    @Test
    void voteOnRuling_withinInvalidUuid() throws Exception {
        final var requestPayload = "{\"ruling_id\": \"3fa85f64-5717-4562-b3fc-\", \"cpf\": \"123.456.789-00\", \"vote_in_favor\": \"true\"}";
        this.mockMvc.perform(post("/ruling/vote")
                        .header("X-API-Version", "1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("JSON parse error: Cannot deserialize value of type `java.util.UUID` from String \"3fa85f64-5717-4562-b3fc-\": not a valid textual representation, problem: Illegal character '-' (code 0x2d) in base64 content"));
    }

    @Test
    void voteOnRuling_isOk() throws Exception {
        final var createdRuling = buildResultActionsForRuling()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN)))
                .andReturn();

        final var voteOnRuling = new VoteOnRuling(normalizeUUID(createdRuling.getResponse().getContentAsString()), "123.456.789-00", true);
        this.mockMvc.perform(post("/ruling/vote")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(voteOnRuling)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN)));
    }

    @Test
    void resultOfRuling_withinInvalidUuid() throws Exception {
        this.mockMvc.perform(get("/ruling/3fa85f64-5717-4562-b3fc-/result")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void resultOfRuling_rulingNotFound() throws Exception {
        this.mockMvc.perform(get(String.format("/ruling/%s/result", UUID.randomUUID()))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isNotEmpty())
                    .andExpect(jsonPath("$").value("Ruling not found or still open"));
    }

    @Test
    void resultOfRuling_isOk() throws Exception {
        MvcResult resultOfRuling = buildResultActionsForRuling()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN))).andReturn();

        final var rulingId = normalizeUUID(resultOfRuling.getResponse().getContentAsString());
        this.mockMvc.perform(get(String.format("/ruling/%s/result", rulingId))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.ruling_id").isString())
                .andExpect(jsonPath("$.ruling_id").value(rulingId.toString()));
    }

    @Test
    void openRuling_withinInvalidUuid() throws Exception {
        this.mockMvc.perform(post("/ruling/3fa85f64-5717-4562-b3fc-/open")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void openRuling_rulingNotFound() throws Exception {
        this.mockMvc.perform(get(String.format("/ruling/%s/open", UUID.randomUUID()))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("Ruling not found"));
    }

    @Test
    void openRuling_isOk() throws Exception {
        final var resultOfRuling = buildResultActionsForRuling()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN))).andReturn();

        final var rulingId = normalizeUUID(resultOfRuling.getResponse().getContentAsString());
        this.mockMvc.perform(get(String.format("/ruling/%s/open", rulingId))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void closeRuling_withinInvalidUuid() throws Exception {
        this.mockMvc.perform(get("/ruling/3fa85f64-5717-4562-b3fc-/close")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void closeRuling_rulingNotFound() throws Exception {
        this.mockMvc.perform(get(String.format("/ruling/%s/close", UUID.randomUUID()))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value("Ruling not found"));
    }

    @Test
    void closeRuling_isOk() throws Exception {
        final var resultOfRuling = buildResultActionsForRuling()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(MatchesPattern.matchesPattern(REGEX_PATTERN))).andReturn();

        final var rulingId = normalizeUUID(resultOfRuling.getResponse().getContentAsString());
        this.mockMvc.perform(get(String.format("/ruling/%s/close", rulingId))
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }


    private UUID normalizeUUID(String uuid) {
        return UUID.fromString(uuid.replace("\"", ""));
    }

    private ResultActions buildResultActionsForRuling() throws Exception {
        final var ruling1 = new CreateRuling("Title 1", "Description 1", LocalDate.now().plusDays(7));
        return this.mockMvc.perform(post("/ruling")
                        .header("X-API-Version", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ruling1)));
    }
}
