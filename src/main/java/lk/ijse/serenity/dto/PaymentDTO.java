package lk.ijse.serenity.dto;

import lk.ijse.serenity.entity.Patient;
import lk.ijse.serenity.entity.Payment;
import lk.ijse.serenity.entity.TherapySession;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString

public class PaymentDTO {

    private Long id;
    private String invoiceNumber;
    private Patient patient;
    private TherapySession therapySession;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private Payment.Status status;
    private Payment.PaymentMethod paymentMethod;
}
