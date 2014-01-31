/*
 * Copyright (c) 2010, BillionGoods.
 */

package billiongoods.server.web.servlet.view;

import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class StaticContentGeneratorTest {
    public StaticContentGeneratorTest() {
    }

    @Test
    public void testparseInfoHolder() throws Exception {
        final Resource r = new ClassPathResource("/i18n/assistance/parsingTest.properties");

        final StaticContentGenerator c = new StaticContentGenerator();
        final NodeModel holder = c.parseInfoHolder(r);
        assertNotNull(holder);

        final TemplateSequenceModel m1 = assertHolder(holder, "L", "I", "D", 4);

        final TemplateSequenceModel m1_1 = assertHolder((TemplateHashModel) m1.get(0), "S1L", "S1I", "S1D", 3);
        assertHolder((TemplateHashModel) m1_1.get(0), "S1I1L", "S1I1I", "S1I1D", 0);
        assertHolder((TemplateHashModel) m1_1.get(1), "S1I2L", null, "S1I2D", 0);
        assertHolder((TemplateHashModel) m1_1.get(2), "S1I3L", null, "S1I3D", 0);

        final TemplateSequenceModel m1_2 = assertHolder((TemplateHashModel) m1.get(1), "S2L", null, "S2D", 3);
        assertHolder((TemplateHashModel) m1_2.get(0), null, "S2I1I", "S2I1D", 0);
        assertHolder((TemplateHashModel) m1_2.get(1), null, null, "S2I2D", 0);
        assertHolder((TemplateHashModel) m1_2.get(2), null, null, "S2I3D", 0);

        final TemplateSequenceModel m1_3 = assertHolder((TemplateHashModel) m1.get(2), "S12L", null, null, 3);
        assertHolder((TemplateHashModel) m1_3.get(0), null, "S12I1I", "S12I1D", 0);
        assertHolder((TemplateHashModel) m1_3.get(1), null, null, "S12I12D", 0);
        assertHolder((TemplateHashModel) m1_3.get(2), null, null, "S12I13D", 0);


        final TemplateSequenceModel m1_4 = assertHolder((TemplateHashModel) m1.get(3), "S123L", null, null, 3);
        assertHolder((TemplateHashModel) m1_4.get(0), null, "S123I1I", "S123I1D", 0);
        assertHolder((TemplateHashModel) m1_4.get(1), null, null, "S12I121D", 0);
        assertHolder((TemplateHashModel) m1_4.get(2), null, null, "S12I132D", 0);
    }

    @Test
    public void testgetInfoHolder() throws Exception {
        final StaticContentGenerator c = new StaticContentGenerator();
        c.setResourcesPaths("classpath:/i18n/assistance/");
        assertHolder(c.loadInfoHolder("loadTest", new Locale("en")), "L_default", "I_default", "D_default", 0);
        assertHolder(c.loadInfoHolder("loadTest", new Locale("jp")), "L_default", "I_default", "D_default", 0);
        assertHolder(c.loadInfoHolder("loadTest", new Locale("ru")), "L_ru", "I_ru", "D_ru", 0);
        assertHolder(c.loadInfoHolder("loadTest", new Locale("de")), "L_de", "I_de", "D_de", 0);
    }

    private static TemplateSequenceModel assertHolder(TemplateHashModel holder,
                                                      String label, String image, String desc, int items) throws Exception {
        if (label != null) {
            assertEquals(label, ((TemplateScalarModel) holder.get("label/text()")).getAsString());
        } else {
            assertEquals(0, ((TemplateSequenceModel) holder.get("label/text()")).size());
        }
        if (image != null) {
            assertEquals(image, ((TemplateScalarModel) holder.get("image/text()")).getAsString());
        } else {
            assertEquals(0, ((TemplateSequenceModel) holder.get("image/text()")).size());
        }

        if (desc != null) {
            assertEquals(desc, ((TemplateScalarModel) holder.get("description/text()")).getAsString());
        } else {
            assertEquals(0, ((TemplateSequenceModel) holder.get("description/text()")).size());
        }

        final TemplateSequenceModel res = (TemplateSequenceModel) holder.get("items/*");
        assertEquals(items, res.size());
        return res;
    }
}
