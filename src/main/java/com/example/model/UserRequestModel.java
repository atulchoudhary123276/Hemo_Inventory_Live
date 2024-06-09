package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table
public class UserRequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    private String bloodGroup;
    @NotEmpty
    private String type;
    @NotNull
    private Long quantity;
//    @NotNull
    private LocalDateTime createdOn;
    @NotEmpty
    private String createdBy;
    @NotEmpty
    private String updatedBy;
    @NotNull
    private LocalDateTime updatedTime;
    @NotEmpty
    private String status;
    private int age;
    @NotEmpty
    private String remark;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private UserModel userId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
        public UserModel getUser() {
        return userId;
    }

    public void setUser(UserModel userId) {
        this.userId = userId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {

        this.remark = remark;
    }

    @Override
    public String toString() {
        return "UserRequestModel{" +
                "id=" + id +

                ", userId=" + userId +
                '}';
    }
}
