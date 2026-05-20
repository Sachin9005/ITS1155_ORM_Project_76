package lk.ijse.serenity.dto;

import lk.ijse.serenity.entity.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@ToString
public class TherapySessionDTO {

    private Long id;
    private Patient patient;
    private Therapist therapist;
    private TherapyProgram therapyProgram;
    private LocalDateTime scheduledAt;
    private TherapySession.Status status;
}
