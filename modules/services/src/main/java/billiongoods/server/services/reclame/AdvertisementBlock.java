package billiongoods.server.services.reclame;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AdvertisementBlock {
    private final String client;
    private final String slot;
    private final int width;
    private final int height;
    private final AdvertisementProvider provider;

    public AdvertisementBlock(String client, String slot, int width, int height, AdvertisementProvider provider) {
        this.client = client;
        this.slot = slot;
        this.width = width;
        this.height = height;
        this.provider = provider;
    }

    public String getClient() {
        return client;
    }

    public String getSlot() {
        return slot;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AdvertisementProvider getProvider() {
        return provider;
    }
}
