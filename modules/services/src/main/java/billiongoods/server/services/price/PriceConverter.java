package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PriceConverter {
	private float course = 33.33f;

	public PriceConverter() {
	}

	public float convertPrice(float price) {
		return price * course;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public static float roundPrice(float price) {
		return Math.round(price * 100f) / 100f;
	}
}
