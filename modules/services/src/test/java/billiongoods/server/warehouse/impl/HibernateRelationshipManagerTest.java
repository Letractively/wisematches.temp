package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml",
		"classpath:/config/personality-config.xml",
		"classpath:/config/billiongoods-config.xml"
})
public class HibernateRelationshipManagerTest {
	@Autowired
	private ArticleManager articleManager;

	@Autowired
	private RelationshipManager relationshipManager;

	public HibernateRelationshipManagerTest() {
	}

	@Test
	public void test() {
		final Category category = createMock(Category.class);
		expect(category.getId()).andReturn(13).anyTimes();
		replay(category);

		final Group group = relationshipManager.createGroup("Mock group");

		final ArticleEditor editor = new ArticleEditor();
		editor.setDescription("desc");
		editor.setCategoryId(category.getId());
		editor.setPrice(new Price(12.d));
		editor.setWeight(1.2);
		editor.setSupplierPrice(new Price(124.d));

		editor.setName("Mock art1");
		final Article a1 = articleManager.createArticle(editor);

		editor.setName("Mock art2");
		final Article a2 = articleManager.createArticle(editor);

		relationshipManager.addGroupItem(group.getId(), a1.getId());
		relationshipManager.addGroupItem(group.getId(), a2.getId());

		assertEquals(1, relationshipManager.getGroups(a1.getId()).size());
		assertEquals(1, relationshipManager.getGroups(a2.getId()).size());

		relationshipManager.addRelationship(a1.getId(), group.getId(), RelationshipType.BOUGHT);

		relationshipManager.addRelationship(a2.getId(), group.getId(), RelationshipType.BOUGHT);
		relationshipManager.addRelationship(a2.getId(), group.getId(), RelationshipType.ACCESSORIES);

		final Relationships r1 = new Relationships(relationshipManager.getRelationships(a1.getId()));
		assertNull(r1.getAssociations(RelationshipType.ACCESSORIES));
		assertEquals(2, r1.getAssociations(RelationshipType.BOUGHT).size());

		final Relationships r2 = new Relationships(relationshipManager.getRelationships(a2.getId()));
		assertEquals(2, r2.getAssociations(RelationshipType.BOUGHT).size());
		assertEquals(2, r2.getAssociations(RelationshipType.ACCESSORIES).size());

		relationshipManager.removeGroup(group.getId());
	}
}
