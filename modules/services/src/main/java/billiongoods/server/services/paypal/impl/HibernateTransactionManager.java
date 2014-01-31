package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.paypal.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

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
	@Transactional(propagation = Propagation.SUPPORTS)
	public PayPalTransaction getTransaction(String token) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("from billiongoods.server.services.paypal.impl.HibernateTransaction where token=:token");
		query.setParameter("token", token);
		try {
			return (HibernateTransaction) query.uniqueResult();
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public PayPalTransaction beginTransaction(Order order) {
		HibernateTransaction transaction = new HibernateTransaction(order.getId(), order.getAmount(), order.getShipment().getAmount());
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
			transaction.setCheckoutStatus(details.getCheckoutStatus());

			final PayerInfoType payerInfo = details.getPayerInfo();
			if (payerInfo != null) {
				transaction.setPayer(payerInfo.getPayer());
				transaction.setPayerId(payerInfo.getPayerID());
				transaction.setPayerPhone(payerInfo.getContactPhone());

				final PersonNameType payerName = payerInfo.getPayerName();
				if (payerName != null) {
					transaction.setPayerLastName(payerName.getLastName());
					transaction.setPayerFirstName(payerName.getFirstName());
				}

				if (payerInfo.getPayerCountry() != null) {
					transaction.setPayerCountry(payerInfo.getPayerCountry().getValue());
				}
			}

			final List<PaymentDetailsType> paymentDetails = details.getPaymentDetails();
			if (paymentDetails != null && paymentDetails.size() == 1) {
				final PaymentDetailsType paymentDetailsType = paymentDetails.get(0);
				transaction.setPayerNote(paymentDetailsType.getNoteText());
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

				final PaymentCodeType paymentType = info.getPaymentType();
				if (paymentType != null) {
					transaction.setPaymentType(paymentType.getValue());
				}
				final PaymentStatusCodeType paymentStatus = info.getPaymentStatus();
				if (paymentStatus != null) {
					transaction.setPaymentStatus(paymentStatus.getValue());
				}
				transaction.setPaymentRequestId(info.getPaymentRequestID());

				if (info.getPaymentError() != null) {
					transaction.setLastQueryError(new PayPalQueryError(info.getPaymentError()));
				}

				try {
					transaction.setPaymentDate(FORMAT_THREAD_LOCAL.get().parse(info.getPaymentDate()));
				} catch (ParseException ex) {
					log.error("PayPal data can't be parsed [" + tnxId + "]: " + response.getTimestamp());
				}

				final BasicAmountType feeAmount = info.getFeeAmount();
				if (feeAmount != null) {
					transaction.setFeeAmount(parseDouble(feeAmount.getValue()));
				}

				final BasicAmountType grossAmount = info.getGrossAmount();
				if (grossAmount != null) {
					transaction.setGrossAmount(parseDouble(grossAmount.getValue()));
				}

				final BasicAmountType settleAmount = info.getSettleAmount();
				if (settleAmount != null) {
					transaction.setSettleAmount(parseDouble(settleAmount.getValue()));
				}
				final BasicAmountType taxAmount = info.getTaxAmount();
				if (taxAmount != null) {
					transaction.setTaxAmount(parseDouble(taxAmount.getValue()));
				}
				transaction.setExchangeRate(info.getExchangeRate());

				final ReversalReasonCodeType reasonCode = info.getReasonCode();
				if (reasonCode != null) {
					transaction.setReasonCode(reasonCode.getValue());
				}
				final PendingStatusCodeType pendingReason = info.getPendingReason();
				if (pendingReason != null) {
					transaction.setPendingReason(pendingReason.getValue());
				}
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

	private double parseDouble(String value) {
		if (value == null || value.length() == 0) {
			return Double.NaN;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			log.error("Double value can't be parsed: " + value);
			return Double.NaN;
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
