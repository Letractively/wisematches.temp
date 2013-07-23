package billiongoods.server.warehouse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Genealogy implements Iterable<Category> {
	private final LinkedList<Category> categories = new LinkedList<>();

	public Genealogy(Category category) {
		Category cat = category;
		while (cat != null) {
			categories.addFirst(cat);
			cat = cat.getParent();
		}
	}

	public List<Category> getParents() {
		return categories;
	}

	@Override
	public Iterator<Category> iterator() {
		return categories.iterator();
	}
}
