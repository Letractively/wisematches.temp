package billiongoods.server.warehouse.impl;

import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.services.price.impl.loader.BanggoodPriceLoader;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.Supplier;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceLoaderTest {
	public BanggoodPriceLoaderTest() {
	}

	@Test
	public void qwe() throws ScriptException {
		String script = "var strbuf = new Array();strbuf[15]='9i8';strbuf[14]='dG';strbuf[13]='/j?';strbuf[12]='uT3';strbuf[11]='zO';strbuf[10]='pYF';strbuf[9]='C03';strbuf[8]='1I';strbuf[7]='y/nE';strbuf[6]='hoC';strbuf[5]='1s';strbuf[4]='MTg';strbuf[3]='tc';strbuf[2]='nDJ';strbuf[1]='C5o';strbuf[0]='6w';var arr=[13,7,2,8,0,12,10,6,15,3,9,4,11,1,14,5];var b='';for (q = 0;q<16;q++){b+=strbuf[arr[q]];};b";

		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		final Object eval = engine.eval(script);
		System.out.println(eval);
	}

	@Test
	public void asd() throws PriceLoadingException {
		BanggoodPriceLoader priceValidator = new BanggoodPriceLoader();
		final Price price = priceValidator.loadPrice(new HibernateSupplierInfo("71838", "SKU072712 ", Supplier.BANGGOOD, null));
		assertNotNull(price);
	}
}
