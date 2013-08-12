package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.paypal.PayPalTransaction;
import billiongoods.server.services.paypal.TransactionPhase;
import billiongoods.server.services.paypal.TransactionResolution;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

import java.util.Collections;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateTransactionManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateTransactionManagerTest() {
	}

	@Test
	public void test() throws Exception {
		final HibernateTransactionManager manager = new HibernateTransactionManager();
		manager.setSessionFactory(sessionFactory);

		final Order order = createMock(Order.class);
		expect(order.getId()).andReturn(12L);
		expect(order.getAmount()).andReturn(123.45f);
		expect(order.getShipment()).andReturn(1.70f);
		replay(order);

		final PayPalTransaction tnx = manager.beginTransaction(order);
		assertNotNull(tnx);
		assertEquals(TransactionPhase.NEW, tnx.getPhase());

		final SetExpressCheckoutResponseType rset = new SetExpressCheckoutResponseType();
		rset.setToken("12345678900987654321"); // 20 single-byte characters
		rset.setAck(AckCodeType.SUCCESS);
		rset.setTimestamp("2013.01.01T11:45:12Z");
		manager.checkoutInitiated(tnx, rset);
		assertEquals(TransactionPhase.INITIATED, tnx.getPhase());

		final PersonNameType payerName = new PersonNameType();
		payerName.setFirstName("Mock");
		payerName.setLastName("mocK");

		final PayerInfoType payerInfo = new PayerInfoType();
		payerInfo.setPayer("mock@mock.mock");
		payerInfo.setPayerID("MOCKID");
		payerInfo.setContactPhone("+mock");
		payerInfo.setPayerName(payerName);
		payerInfo.setPayerStatus(PayPalUserStatusCodeType.VERIFIED);
		payerInfo.setPayerCountry(CountryCodeType.AF);

		final GetExpressCheckoutDetailsResponseDetailsType rgetd = new GetExpressCheckoutDetailsResponseDetailsType();
		rgetd.setToken("12345678900987654321");
		rgetd.setInvoiceID("12L"); // orderId
		rgetd.setPayerInfo(payerInfo);
		rgetd.setCheckoutStatus("MockCheckout");

		final GetExpressCheckoutDetailsResponseType rget = new GetExpressCheckoutDetailsResponseType();
		rget.setAck(AckCodeType.SUCCESS);
		rget.setTimestamp("2013.01.01T11:45:12Z");
		rget.setGetExpressCheckoutDetailsResponseDetails(rgetd);

		manager.checkoutValidated(tnx, rget);
		assertEquals(TransactionPhase.VALIDATED, tnx.getPhase());

		final ErrorType o = new ErrorType();
		o.setErrorCode("code");
		o.setSeverityCode(SeverityCodeType.ERROR);
		o.setShortMessage("short error msg");
		o.setLongMessage("This is long error message");

		final PaymentInfoType infoType = new PaymentInfoType();
		infoType.setExchangeRate("12.3232312");
		infoType.setFeeAmount(new BasicAmountType(CurrencyCodeType.USD, "12.3"));
		infoType.setGrossAmount(new BasicAmountType(CurrencyCodeType.USD, "21.3"));
		infoType.setTransactionID("TID");
		infoType.setTransactionType(PaymentTransactionCodeType.CART);
		infoType.setParentTransactionID("PTID");
		infoType.setPaymentType(PaymentCodeType.INSTANT);
		infoType.setPaymentStatus(PaymentStatusCodeType.EXPIRED);
		infoType.setPaymentRequestID("PRID");
		infoType.setSettleAmount(new BasicAmountType(CurrencyCodeType.USD, "34.3"));
		infoType.setTaxAmount(new BasicAmountType(CurrencyCodeType.USD, "345.3"));
		infoType.setReasonCode(ReversalReasonCodeType.REFUND);
		infoType.setPendingReason(PendingStatusCodeType.PAYMENTREVIEW);
		infoType.setHoldDecision("wes");
		infoType.setInsuranceAmount("we2");
		infoType.setPaymentDate("2013.01.01T11:45:12Z");

		final DoExpressCheckoutPaymentResponseDetailsType details = new DoExpressCheckoutPaymentResponseDetailsType();
		details.setToken("12345678900987654321");
		details.setPaymentInfo(Collections.singletonList(infoType));

		final DoExpressCheckoutPaymentResponseType rdo = new DoExpressCheckoutPaymentResponseType();
		rdo.setAck(AckCodeType.FAILURE);
		rdo.setTimestamp("2013.01.01T11:45:12Z");
		rdo.setErrors(Collections.singletonList(o));
		rdo.setDoExpressCheckoutPaymentResponseDetails(details);

		manager.checkoutConfirmed(tnx, rdo);
		assertEquals(TransactionPhase.CONFIRMED, tnx.getPhase());

		manager.commitTransaction(tnx, TransactionResolution.FAILED);
		assertEquals(TransactionPhase.DONE, tnx.getPhase());

		final HibernateTransaction transaction = manager.getTransaction(tnx.getId());
		assertEquals(tnx.getId(), transaction.getId());
		assertEquals(12L, transaction.getOrderId().longValue());
		assertEquals("12345678900987654321", transaction.getToken());
		assertEquals(123.45f, transaction.getAmount(), 0.000000001f);
		assertEquals(1.70f, transaction.getShipment(), 0.000000001f);
		assertNotNull(transaction.getTimestamp());
		assertEquals(TransactionPhase.DONE, transaction.getPhase());
		assertEquals(TransactionResolution.FAILED, transaction.getResolution());
		assertEquals(AckCodeType.SUCCESS.getValue(), transaction.getAckSet());
		assertNotNull(transaction.getTimestampSet());
		assertEquals(AckCodeType.SUCCESS.getValue(), transaction.getAckGet());
		assertNotNull(transaction.getTimestampGet());
		assertEquals("mock@mock.mock", transaction.getPayer());
		assertEquals("MOCKID", transaction.getPayerId());
		assertEquals("+mock", transaction.getPayerPhone());
		assertEquals("mocK", transaction.getPayerLastName());
		assertEquals("Mock", transaction.getPayerFirstName());
		assertEquals("AF", transaction.getPayerCountry());
		assertEquals("MockCheckout", transaction.getCheckoutStatus());
		assertEquals(AckCodeType.FAILURE.getValue(), transaction.getAckDo());
		assertNotNull(transaction.getTimestampDo());
		assertEquals("TID", transaction.getTransactionId());
		assertEquals(PaymentTransactionCodeType.CART.getValue(), transaction.getTransactionType());
		assertEquals("PTID", transaction.getParentTransactionId());
		assertEquals(PaymentCodeType.INSTANT.getValue(), transaction.getPaymentType());
		assertEquals(PaymentStatusCodeType.EXPIRED.getValue(), transaction.getPaymentStatus());
		assertEquals("PRID", transaction.getPaymentRequestId());
		assertNotNull(transaction.getPaymentDate());
		assertEquals(12.3f, transaction.getFeeAmount(), 0.000001f);
		assertEquals(21.3f, transaction.getGrossAmount(), 0.000001f);
		assertEquals(34.3f, transaction.getSettleAmount(), 0.0000001f);
		assertEquals(345.3f, transaction.getTaxAmount(), 0.0000001f);
		assertEquals("12.3232312", transaction.getExchangeRate());
		assertEquals(ReversalReasonCodeType.REFUND.getValue(), transaction.getReasonCode());
		assertEquals(PendingStatusCodeType.PAYMENTREVIEW.getValue(), transaction.getPendingReason());
		assertEquals("wes", transaction.getHoldDecision());
		assertEquals("we2", transaction.getInsuranceAmount());

		assertEquals(1, transaction.getTransactionErrors().size());

		final HibernateTransactionError error = transaction.getTransactionErrors().get(0);
		assertEquals("code", error.getCode());
		assertEquals(SeverityCodeType.ERROR.getValue(), error.getSeverity());
		assertEquals(TransactionPhase.CONFIRMED, error.getPhase());
		assertEquals("short error msg", error.getShortMessage());
		assertEquals("This is long error message", error.getLongMessage());

		verify(order);
	}
}
