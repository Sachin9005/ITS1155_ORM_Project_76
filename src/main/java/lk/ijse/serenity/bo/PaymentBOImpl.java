package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.PaymentDAOImpl;
import lk.ijse.serenity.dto.PaymentDTO;
import lk.ijse.serenity.entity.Payment;

import java.math.BigDecimal;

public class PaymentBOImpl {

    PaymentDAOImpl paymentDAO = new PaymentDAOImpl();

    public boolean savePayment(PaymentDTO paymentDTO) {
        try {
            Payment payment = Payment.builder()
                    .invoiceNumber(paymentDAO.nextInvoiceNumber())
                    .patient(paymentDTO.getPatient())
                    .therapySession(paymentDTO.getTherapySession())
                    .amount(paymentDTO.getAmount())
                    .paymentDate(paymentDTO.getPaymentDate())
                    .status(paymentDTO.getStatus())
                    .paymentMethod(paymentDTO.getPaymentMethod())
                    .build();
            return paymentDAO.save(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal totalRevenue() {
        return paymentDAO.totalRevenue();
    }
}
