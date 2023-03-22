package com.hikers.sanneomeo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Table(name = "tbl_keep")
@NoArgsConstructor
public class Keep {
    @Builder
    public Keep(Long userSeq, Long trailSeq) {
        this.userSeq = userSeq;
        this.trailSeq = trailSeq;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keep_seq", nullable = false)
    private Long keepSeq;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "trail_seq", nullable = false)
    private Long trailSeq;

    @Column(name = "created_at")
    private Timestamp createdAt;


}
