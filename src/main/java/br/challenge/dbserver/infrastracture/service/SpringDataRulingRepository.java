package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.domain.adapters.Ruling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRulingRepository extends JpaRepository<Ruling, String>{
}
