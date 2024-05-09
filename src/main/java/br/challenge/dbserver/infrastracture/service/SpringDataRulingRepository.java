package br.challenge.dbserver.infrastracture.service;

import br.challenge.dbserver.infrastracture.Ruling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataRulingRepository extends JpaRepository<Ruling, String> {

    List<Ruling> findAllByAvailable(Boolean available);
}
