package billiongoods.server.warehouse;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Category {
	Integer getId();

	String getName();

	String getDescription();


	int getPosition();


	boolean isFinal();

	boolean isActive();


	Category getParent();

	Genealogy getGenealogy();


	List<Category> getChildren();

	Set<StoreAttribute> getAttributes();
}
