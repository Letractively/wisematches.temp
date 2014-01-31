package billiongoods.server.services.mistake;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MistakeListener {
	void mistakeRaised(Mistake mistake);

	void mistakeResolved(Mistake mistake);
}
