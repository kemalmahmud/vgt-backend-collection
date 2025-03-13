package com.vgt.collections.Model;

import com.google.type.DateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "user_diary_detail")
public class UserDiaryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "diary_detail_id", nullable = false)
    private String diaryDetailId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "diary_id")
    private UserDiaries userDiaries;

    @Column(name = "minutes_played")
    private Integer minutesPlayed;

    @Column(name = "hours_played")
    private Integer hoursPlayed;

    @Column(name = "diary_text")
    private String diaryText;

    @Column(name = "recorded_time", nullable = false)
    private LocalDateTime recordedTime;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
