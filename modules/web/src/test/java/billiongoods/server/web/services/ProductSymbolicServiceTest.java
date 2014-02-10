package billiongoods.server.web.services;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductSymbolicServiceTest {
	public ProductSymbolicServiceTest() {
	}

	@Test
	public void test() {
		final ProductSymbolicService pne = new ProductSymbolicService();

		final String s = pne.generateSymbolic("Это мой продукт: 10% бесплатно, абвгдеёжзийклмнопрстуфхцчшщъыьэюя + titanium");
		assertEquals("eto-moi-produkt:-10%-besplatno,-abvgdeyozhziiklmnoprstufhtschshshieyuya-+-titanium", s);
	}
}
