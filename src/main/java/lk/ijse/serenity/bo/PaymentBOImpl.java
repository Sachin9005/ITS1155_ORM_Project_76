package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.PaymentDAOImpl;
import lk.ijse.serenity.dto.PaymentDTO;
import lk.ijse.serenity.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

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

    public List<PaymentDTO> findAll() {
        List<Payment> payments =  paymentDAO.getAll();

        List<PaymentDTO> paymentDTOs = payments.stream().map(p -> PaymentDTO.builder()
                .invoiceNumber(p.getInvoiceNumber())
                .patient(p.getPatient())
                .therapySession(p.getTherapySession())
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .status(p.getStatus())
                .paymentMethod(p.getPaymentMethod())
                .build()).toList();
        return paymentDTOs;
    }

    public long countPending() {
        return paymentDAO.countPending();
    }
}
