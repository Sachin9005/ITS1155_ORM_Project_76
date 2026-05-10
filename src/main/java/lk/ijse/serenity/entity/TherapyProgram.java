package lk.ijse.serenity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

@Entity
@Table(name = "therapy_programs")
public class TherapyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_id", unique = true, nullable = false, length = 20)
    private String programId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "duration", length = 50)
    private String duration;

    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(mappedBy = "programs", fetch = FetchType.LAZY)
    private List<Therapist> therapists = new ArrayList<>();

    @OneToMany(mappedBy = "therapyProgram", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TherapySession> sessions = new ArrayList<>();
}
