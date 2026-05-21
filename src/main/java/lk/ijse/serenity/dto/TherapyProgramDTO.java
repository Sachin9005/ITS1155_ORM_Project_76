package lk.ijse.serenity.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class TherapyProgramDTO {
    private Long id;
    private String programId;
    private String name;
    private String duration;
    private BigDecimal fee;
    private String description;

    public TherapyProgramDTO(String programId, String name, String duration, BigDecimal fee, String description) {
        this.programId = programId;
        this.name = name;
        this.duration = duration;
        this.fee = fee;
        this.description = description;
    }
}
