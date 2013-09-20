package billiongoods.server.warehouse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Genealogy implements Iterable<Category> {
	private final Category category;
	private final LinkedList<Category> parents = new LinkedList<>();

	public Genealogy(Category category) {
		this.category = category;
		Category cat = category.getParent();
		while (cat != null) {
			parents.addFirst(cat);
			cat = cat.getParent();
		}
	}

	public Category getRoot() {
		return parents.size() == 0 ? category : parents.getFirst();
	}

	public int getGeneration() {
		return parents.size();
	}

	public Category getCategory() {
		return category;
	}

	public List<Category> getParents() {
		return parents;
	}

	@Override
	public Iterator<Category> iterator() {
		return parents.iterator();
	}
}
