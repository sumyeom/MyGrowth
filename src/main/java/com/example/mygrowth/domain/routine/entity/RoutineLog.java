package com.example.mygrowth.domain.routine.entity;

import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.common.BaseCreatedEntity;
import com.example.mygrowth.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "routine_log",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"routine_id", "user_id", "date"})
        }
)
@Getter
@NoArgsConstructor
public class RoutineLog extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private Routine routine;

    @Column(name="date", nullable = false)
    private LocalDate date;

    @Column(name="is_success", nullable = false)
    private boolean isSuccess;

    public RoutineLog(Routine routine, LocalDate date, boolean isSuccess) {
        this.routine = routine;
        this.date = date;
        this.isSuccess = isSuccess;
    }

}
