public class WatchedCrypto {
    private final String cryptoId;
    private float basePrice;
    private boolean isWarned = false;

    public WatchedCrypto(String cryptoId, float basePrice) {
        this.cryptoId = cryptoId;
        this.basePrice = basePrice;
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public boolean isWarned() {
        return isWarned;
    }

    public void setWarned(boolean warned) {
        isWarned = warned;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isLowerThanBase(float currentPrice) {
        return currentPrice < this.basePrice;
    }
}
