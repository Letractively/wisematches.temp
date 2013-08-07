package billiongoods.server.web.servlet.mvc.maintain.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImportArticlesForm {
    private Integer category;
    private CommonsMultipartFile file;

    public ImportArticlesForm() {
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }
}
