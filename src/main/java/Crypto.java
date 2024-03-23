public class Crypto {
    private final String id;
    private float currentPrice;

    public Crypto(String id) {
        this.id = id;
    }

    public Crypto(String id, float currentPrice) {
        this.id = id;
        this.currentPrice = currentPrice;
    }

    public String getId() {
        return id;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }
}
