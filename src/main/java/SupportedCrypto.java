import java.util.ArrayList;
import java.util.List;

public class SupportedCrypto {
    public static SupportedCrypto supportedCrypto;
    private final List<Crypto> supportedCryptoList = new ArrayList<>();
    private final String[] list = {"BTC", "ETH", "USDT", "USDC", "ADA", "SOL", "AVAX", "DOT",
        "DOGE", "UST", "SHIB", "MATIC", "WBTC", "CRO", "DAI", "ATOM", "LTC", "LINK"};

    private SupportedCrypto() {}

    private void buildSupportedCryptoList() {
        for (String s : list) {
            supportedCryptoList.add(new Crypto(s));
        }
    }

    public static SupportedCrypto getSupportedCrypto() {
        if(supportedCrypto == null){
            supportedCrypto = new SupportedCrypto();
            supportedCrypto.buildSupportedCryptoList();
        }
        return supportedCrypto;
    }

    public List<Crypto> getSupportedCryptoList (){
        return supportedCryptoList;
    }

    public boolean isSupportedCrypto(String cryptoId){
        for (Crypto s : supportedCryptoList) {
            if (s.getId().equals(cryptoId)) {
                return true;
            }
        }
        return false;
    }
}
