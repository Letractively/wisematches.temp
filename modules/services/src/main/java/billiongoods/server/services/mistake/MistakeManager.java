package billiongoods.server.services.mistake;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MistakeManager {
	void addMistakeListener(MistakeListener l);

	void removeMistakeListener(MistakeListener l);


	Mistake raiseMistake(Integer productId, String description, MistakeScope scope);

	Mistake resolveMistake(Integer mistakeId, MistakeResolution resolution);
}
