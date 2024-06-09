package com.example.dto;
import java.time.LocalDateTime;
public class BloodStockDto {
    private Long id;
    private String bloodGroup;
    private Long avaliableUnit= 0L;
    private LocalDateTime lastUpdate;
    private int coinValue;

    public Long getAvaliableUnit() {
        return avaliableUnit;
    }

    public void setAvaliableUnit(Long avaliableUnit) {
        this.avaliableUnit = avaliableUnit;
    }

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
