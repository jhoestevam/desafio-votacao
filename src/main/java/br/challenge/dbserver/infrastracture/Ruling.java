package br.challenge.dbserver.infrastracture;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "ruling")
public class Ruling {

    @Id
    @Column(name = "uuid")
    public String uuid;

    @Column(name = "title")
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "end_date")
    public LocalDate endDate;

    @Column(name = "votes_in_favor")
    public int votesInFavor;

    @Column(name = "votes_against")
    public int votesAgainst;
    
    @Column(name = "available")
    public boolean available;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getVotesInFavor() {
        return votesInFavor;
    }

    public void setVotesInFavor(int votesInFavor) {
        this.votesInFavor = votesInFavor;
    }

    public int getVotesAgainst() {
        return votesAgainst;
    }

    public void setVotesAgainst(int votesAgainst) {
        this.votesAgainst = votesAgainst;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
