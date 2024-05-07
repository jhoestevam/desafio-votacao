package br.challenge.dbserver.domain.adapters.service;

import br.challenge.dbserver.application.controller.CreateRuling;
import br.challenge.dbserver.application.controller.ResultRuling;

import java.util.List;
import java.util.UUID;

public interface RulingService {

    UUID createRuling(final CreateRuling createRuling);

    List<CreateRuling> listRuling(UUID uuid, Boolean available);

    ResultRuling resultRuling(UUID uuid);
}
