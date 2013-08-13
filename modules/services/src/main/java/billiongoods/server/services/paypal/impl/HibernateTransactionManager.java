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
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.GetExpressCheckoutDetailsResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.PayerInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateTransactionManager implements PayPalTransactionManager {
	private SessionFactory sessionFactory;

	private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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

		try {
			transaction.setInvoicingTime(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}

		transaction.setToken(response.getToken());
		transaction.setPhase(TransactionPhase.INVOICING);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void checkoutValidated(PayPalTransaction tnx, GetExpressCheckoutDetailsResponseType response) {
		final Session session = sessionFactory.getCurrentSession();

		final Long tnxId = tnx.getId();
		final HibernateTransaction transaction = (HibernateTransaction) tnx;

		try {
			transaction.setVerificationTime(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}

		final GetExpressCheckoutDetailsResponseDetailsType details = response.getGetExpressCheckoutDetailsResponseDetails();

		if (details != null) {
			final PayerInfoType payerInfo = details.getPayerInfo();
			transaction.setCheckoutStatus(details.getCheckoutStatus());
			transaction.setPayerComment(details.getNote());

			if (payerInfo != null) {
				transaction.setPayer(payerInfo.getPayer());
				transaction.setPayerId(payerInfo.getPayerID());
				transaction.setPayerPhone(payerInfo.getContactPhone());
				if (payerInfo.getPayerName() != null) {
					transaction.setPayerLastName(payerInfo.getPayerName().getLastName());
				}

				if (payerInfo.getPayerName() != null) {
					transaction.setPayerFirstName(payerInfo.getPayerName().getFirstName());
				}

				if (payerInfo.getPayerCountry() != null) {
					transaction.setPayerCountry(payerInfo.getPayerCountry().getValue());
				}
			}
		}
		transaction.setPhase(TransactionPhase.VERIFICATION);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void checkoutConfirmed(PayPalTransaction tnx, DoExpressCheckoutPaymentResponseType response) {
		final Session session = sessionFactory.getCurrentSession();

		final Long tnxId = tnx.getId();
		final HibernateTransaction transaction = (HibernateTransaction) tnx;

		try {
			transaction.setConfirmationTime(FORMAT_THREAD_LOCAL.get().parse(response.getTimestamp()));
		} catch (ParseException ex) {
			log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
		}

		final DoExpressCheckoutPaymentResponseDetailsType details = response.getDoExpressCheckoutPaymentResponseDetails();
		if (details != null) {
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
					transaction.setLastQueryError(new PayPalQueryError(info.getPaymentError()));
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
		}
		transaction.setPhase(TransactionPhase.CONFIRMATION);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void commitTransaction(PayPalTransaction tnx, boolean approved) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateTransaction transaction = (HibernateTransaction) tnx;
		transaction.setPhase(TransactionPhase.FINISHED);
		transaction.setResolution(approved ? TransactionResolution.APPROVED : TransactionResolution.REJECTED);
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void rollbackTransaction(PayPalTransaction tnx, TransactionPhase phase, PayPalException exception) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateTransaction transaction = (HibernateTransaction) tnx;
		transaction.setPhase(phase);
		transaction.setResolution(TransactionResolution.FAILURE);
		if (exception instanceof PayPalQueryException) {
			transaction.setLastQueryError(((PayPalQueryException) exception).getQueryError());
		} else {
			transaction.setLastQueryError(new PayPalQueryError("error", "exception", "fatal", "PayPal system exception", exception.getMessage()));
		}
		session.update(transaction);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public HibernateIPNMessage registerMessage(Map<String, String> values) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateIPNMessage object = new HibernateIPNMessage(values);
		session.save(object);
		return object;
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
