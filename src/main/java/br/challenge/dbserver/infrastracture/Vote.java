package br.challenge.dbserver.infrastracture;

import jakarta.persistence.*;

@Entity
@Table(name = "vote")
public class Vote {

    @Id
    @Column(name = "uuid")
    public String uuid;

    @Column(name = "cpf")
    public String cpf;

    @Column(name = "vote_in_favor")
    public boolean voteInFavor;

    @OneToOne
    @JoinColumn(name = "ruling_id")
    public Ruling ruling;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public boolean isVoteInFavor() {
        return voteInFavor;
    }

    public void setVoteInFavor(boolean voteInFavor) {
        this.voteInFavor = voteInFavor;
    }

    public Ruling getRuling() {
        return ruling;
    }

    public void setRuling(Ruling ruling) {
        this.ruling = ruling;
    }
}
