package com.healthplus.healthplus_api.profile.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class Admin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String names;

    @Column(nullable = false)
    @Size(max = 100)
    private String lastNames;

    @JsonIgnore
    @OneToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name="FK_admin_user"))
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
