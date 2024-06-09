package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table
public class BloodStockModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    private String bloodGroup;
    @NotNull
    private Long avaliableUnit= 0L;
    @NotNull
    private LocalDateTime lastUpdate;
    private int coinValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Long getAvaliableUnit() {
        return avaliableUnit;
    }

    public void setAvaliableUnit(Long avaliableUnit) {
        this.avaliableUnit = avaliableUnit;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {

        this.lastUpdate = lastUpdate;
    }

    public Integer getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(Integer coinValue) {
        this.coinValue = coinValue;
    }
}
