package com.vgt.collections.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "user_diaries")
public class UserDiaries {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "diary_id", nullable = false)
    private String diaryId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "diary_name")
    private String diaryName;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "userDiaries", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserDiaryDetail> diaryDetails;

}
