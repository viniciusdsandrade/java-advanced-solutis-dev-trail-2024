package br.com.agilizeware.payment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.agilizeware.dao.CrudAgilizeRepositoryIF;
import br.com.agilizeware.model.Payment;

public interface PaymentDaoRepository extends CrudAgilizeRepositoryIF<Payment, Long> {

	@Query(" select p from Payment p where p.paymentId = :paymentId ")
	Payment findByPaymentId(@Param("paymentId") String paymentId);

	@Query(" select p from Payment p where p.merchantOrderId = :merchantOrderId ")
	Payment findByMerchantOrderId(@Param("merchantOrderId") String merchantOrderId);

}
