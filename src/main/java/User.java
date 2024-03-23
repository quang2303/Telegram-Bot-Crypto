import java.util.ArrayList;
import java.util.List;

public class User {
    private final String id;
    private List<WatchedCrypto> watchedCryptoList = new ArrayList<>();

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<WatchedCrypto> getWatchedCryptoList() {
        return watchedCryptoList;
    }

    public void updateWatchedCrypto(WatchedCrypto crypto) {
        boolean isFound = false;
        for (WatchedCrypto watchedCrypto : this.watchedCryptoList) {
            if (crypto.getCryptoId().equals(watchedCrypto.getCryptoId())) {
                watchedCrypto.setBasePrice(crypto.getBasePrice());
                isFound = true;
                break;
            }
        }
        if(!isFound){
            this.watchedCryptoList.add(crypto);
        }
    }

    public boolean deleteWatchedCrypto (String cryptoName) {
        boolean success = false;
        for(int i=0; i<this.watchedCryptoList.size(); i++){
            if(cryptoName.equals(this.watchedCryptoList.get(i).getCryptoId())){
                this.watchedCryptoList.remove(i);
                success = true;
                break;
            }
        }
        return success;
    }
}
