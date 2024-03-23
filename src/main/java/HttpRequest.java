import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpRequest {
    public HttpRequest(){}

    public float getPrice(String coinName){
        float amount = -1;
        try {
            URL url = new URL("https://api.coinbase.com/v2/prices/" + coinName + "-USD/spot");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("HttpResponseCode: " + connection.getResponseCode());
            } else {
                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                scanner.close();
                JsonObject data = new Gson().fromJson(informationString.toString(), JsonObject.class);
                data = data.getAsJsonObject("data");
                amount = data.get("amount").getAsJsonPrimitive().getAsFloat();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return amount;
    }
}
