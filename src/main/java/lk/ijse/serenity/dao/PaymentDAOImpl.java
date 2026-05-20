package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.Payment;
import org.hibernate.Session;

import java.math.BigDecimal;

public class PaymentDAOImpl extends CrudDAOImpl<Payment>{

    public String nextInvoiceNumber() {
        try(Session session = FactoryConfiguration.getInstance().getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(p) FROM Payment p", Long.class).uniqueResult();
            return String.format("INV-%05d", (count == null ? 0 : count) + 1);
        }
    }

    public BigDecimal totalRevenue() {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            BigDecimal result = session.createQuery(
                    "SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'",
                    BigDecimal.class).uniqueResult();
            return result != null ? result : BigDecimal.ZERO;
        }
    }
}
