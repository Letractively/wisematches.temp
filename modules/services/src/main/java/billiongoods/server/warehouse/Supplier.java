package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Supplier {
    BANGGOOD("http://www.banggood.com/");

    private final String site;

    Supplier(String site) {
        this.site = site;
    }

    public String getSite() {
        return site;
    }
}
