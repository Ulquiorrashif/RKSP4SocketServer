package com.example.rksp4socketserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@AllArgsConstructor
public class MatchStats {
    @Id
    private Long id;
    private Integer countTeam1;
    private Integer countTeam2;
    private Integer bestADR;
    private Integer bestKD;

}
