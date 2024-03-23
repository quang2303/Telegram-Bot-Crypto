import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramBot telegramBot = new TelegramBot();

            int timeSleep = 60000;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    telegramBot.updateCryptoList(timeSleep);
                }
            }).start();

            botsApi.registerBot(telegramBot);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    telegramBot.watchAndWarn(timeSleep);
                }
            }).start();

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
