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
    private String programId;
    private String name;
    private String duration;
    private BigDecimal fee;
    private String description;
}
