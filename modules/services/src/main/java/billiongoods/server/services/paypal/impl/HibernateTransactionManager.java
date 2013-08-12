package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.paypal.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.eBLBaseComponents.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateTransactionManager implements PayPalTransactionManager {
	private SessionFactory sessionFactory;

	private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss'Z'");
		}
	};

	private static final Logger log = LoggerFactory.getLogger("billiongoods.paypal.TransactionManager");

	public HibernateTransactionManager() {
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public HibernateTransaction getTransaction(Long id) {
		return (HibernateTransaction) sessionFactory.getCurrentSession().get(HibernateTransaction.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public PayPalTransaction beginTransaction(Order order) {
		HibernateTransaction transaction = new HibernateTransaction(order.getId(), order.getAmount(), order.getShipment());
		sessionFactory.getCurrentSession().save(transaction);
		return transaction;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void checkoutInitiated(PayPalTransaction tnx, SetExpressCheckoutResponseType response) {
		final Session session = sessionFactory.getCurrentSession();

		final Long tnxId = tnx.getId();
		final HibernateTransaction transaction = (HibernateTransaction) tnx;

		transaction.setAckSet(response.getAck().getValue());
		try {
			transaction.setTimestampSet(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}
		transaction.addErrors(convertErrors(TransactionPhase.INITIATED, response.getErrors()));

		transaction.setToken(response.getToken());
		transaction.setPhase(TransactionPhase.INITIATED);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void checkoutValidated(PayPalTransaction tnx, GetExpressCheckoutDetailsResponseType response) {
		final Session session = sessionFactory.getCurrentSession();

		final Long tnxId = tnx.getId();
		final HibernateTransaction transaction = (HibernateTransaction) tnx;

		transaction.setAckGet(response.getAck().getValue());
		try {
			transaction.setTimestampGet(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}
		transaction.addErrors(convertErrors(TransactionPhase.VALIDATED, response.getErrors()));

		final GetExpressCheckoutDetailsResponseDetailsType details = response.getGetExpressCheckoutDetailsResponseDetails();

		final PayerInfoType payerInfo = details.getPayerInfo();

		transaction.setPayer(payerInfo.getPayer());
		transaction.setPayerId(payerInfo.getPayerID());
		transaction.setPayerPhone(payerInfo.getContactPhone());
		transaction.setPayerLastName(payerInfo.getPayerName().getLastName());
		transaction.setPayerFirstName(payerInfo.getPayerName().getFirstName());
		transaction.setPayerCountry(payerInfo.getPayerCountry().getValue());

		transaction.setCheckoutStatus(details.getCheckoutStatus());

		transaction.setPhase(TransactionPhase.VALIDATED);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void checkoutConfirmed(PayPalTransaction tnx, DoExpressCheckoutPaymentResponseType response) {
		final Session session = sessionFactory.getCurrentSession();

		final Long tnxId = tnx.getId();
		final HibernateTransaction transaction = (HibernateTransaction) tnx;

		transaction.setAckDo(response.getAck().getValue());
		try {
			transaction.setTimestampDo(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}
		transaction.addErrors(convertErrors(TransactionPhase.CONFIRMED, response.getErrors()));

		final DoExpressCheckoutPaymentResponseDetailsType details = response.getDoExpressCheckoutPaymentResponseDetails();
		final List<PaymentInfoType> paymentInfo = details.getPaymentInfo();
		if (paymentInfo.size() != 1) {
			log.info("Incorrect payments count: " + paymentInfo.size());
		} else {
			final PaymentInfoType info = paymentInfo.get(0);

			transaction.setTransactionId(info.getTransactionID());
			transaction.setTransactionType(info.getTransactionType().getValue());
			transaction.setParentTransactionId(info.getParentTransactionID());

			transaction.setPaymentType(info.getPaymentType().getValue());
			transaction.setPaymentStatus(info.getPaymentStatus().getValue());
			transaction.setPaymentRequestId(info.getPaymentRequestID());

			if (info.getPaymentError() != null) {
				transaction.addErrors(convertErrors(TransactionPhase.CONFIRMED, Collections.singletonList(info.getPaymentError())));
			}

			try {
				transaction.setPaymentDate(FORMAT_THREAD_LOCAL.get().parse(info.getPaymentDate()));
			} catch (ParseException ex) {
				log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
			}

			transaction.setFeeAmount(parseFloat(info.getFeeAmount().getValue()));
			transaction.setGrossAmount(parseFloat(info.getGrossAmount().getValue()));
			transaction.setSettleAmount(parseFloat(info.getSettleAmount().getValue()));
			transaction.setTaxAmount(parseFloat(info.getTaxAmount().getValue()));
			transaction.setExchangeRate(info.getExchangeRate());

			transaction.setReasonCode(info.getReasonCode().getValue());
			transaction.setPendingReason(info.getPendingReason().getValue());
			transaction.setHoldDecision(info.getHoldDecision());

			transaction.setInsuranceAmount(info.getInsuranceAmount());
		}
		transaction.setPhase(TransactionPhase.CONFIRMED);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void commitTransaction(PayPalTransaction tnx, TransactionResolution resolution) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateTransaction transaction = (HibernateTransaction) tnx;
		transaction.setPhase(TransactionPhase.DONE);
		transaction.setResolution(resolution);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Long registerMessage(PayPalMessage message) {
		final Session session = sessionFactory.getCurrentSession();
		return ((Number) session.save(new HibernateIPNMessage(message))).longValue();
	}

	private List<HibernateTransactionError> convertErrors(TransactionPhase phase, List<ErrorType> errors) {
		if (errors == null) {
			return null;
		}

		final List<HibernateTransactionError> res = new ArrayList<>();
		for (ErrorType error : errors) {
			res.add(new HibernateTransactionError(error.getErrorCode(), error.getSeverityCode().getValue(), phase, error.getShortMessage(), error.getLongMessage()));
		}
		return res;
	}

	private float parseFloat(String value) {
		if (value == null || value.length() == 0) {
			return Float.NaN;
		}
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException ex) {
			log.error("Float value can't be parsed: " + value);
			return Float.NaN;
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
