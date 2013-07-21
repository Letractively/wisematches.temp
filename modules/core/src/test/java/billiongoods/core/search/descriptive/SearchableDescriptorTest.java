package billiongoods.core.search.descriptive;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SearchableDescriptorTest {
    public SearchableDescriptorTest() {
    }

    @Test
    public void test() {
        SearchableDescriptor d = SearchableDescriptor.valueOf(MockSearchableEntity.class);
        assertEquals("pid", d.getBeanAnnotation().uniformityProperty());
        assertEquals("account.id", d.getUniformityProperty().column());

        assertEquals(3, d.getPropertyNames().size());
        assertFalse(d.getProperty("pid").sortable());
        assertEquals("account.id", d.getProperty("pid").column());

        assertTrue(d.getProperty("nickname").sortable());
        assertEquals("account.nickname", d.getProperty("nickname").column());

        assertTrue(d.getProperty("language").sortable());
        assertEquals("profile.language", d.getProperty("language").column());
    }
}
