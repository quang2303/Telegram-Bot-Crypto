import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private Semaphore user_lock = new Semaphore(1);
    private Semaphore crypto_list_lock = new Semaphore(1);
    public static List<User> userList = new ArrayList<>();
    public static List<Crypto> cryptoList = SupportedCrypto.getSupportedCrypto().getSupportedCryptoList();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userRequest = update.getMessage().getText();
            String userid = update.getMessage().getChatId().toString();
            String message;
            SendMessage response = new SendMessage();
            response.setChatId(userid);

            if (userRequest.equals("/start")) {
                message = join(userid);
                response.setText(message);
            } else if (userRequest.equals("/end")) {
                message = exit(userid);
                response.setText(message);
            } else if (userRequest.startsWith("base")) {
                String[] element = userRequest.split(" "); // base <cryptoId> <base>
                if (element.length == 3) {
                    float base;
                    try {
                        base = Float.parseFloat(element[2]);
                        message = setBase(userid, element[1], base);
                    } catch (NumberFormatException e) {
                        message = "Please set base price in form: base <cryptoId> <basePrice>";
                    }
                } else {
                    message = "Please set base price in form: base <cryptoId> <basePrice>";
                }
                response.setText(message);
            } else if (userRequest.startsWith("drop")) {
                String[] element = userRequest.split(" "); // drop <crypto>
                if (element.length == 2) {
                    message = drop(userid, element[1]);
                } else {
                    message = "Please stop watching crypto price in form: drop <cryptoId>";
                }
                response.setText(message);
            } else if (userRequest.startsWith("price")) {
                String[] element = userRequest.split(" "); // price <cryptoId>
                if (element.length == 2) {
                    message = getPriceById(element[1]);
                } else {
                    message = "Please get crypto price in form: price <cryptoId>";
                }
                response.setText(message);
            } else {
                message = "Cannot define your request";
                response.setText(message);
            }

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /// need lock
    private boolean isExistedUser(String userId) {
        for (User user : userList) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public int findCryptoIndexById(String cryptoId) {
        int index = -1;
        for (int i = 0; i < cryptoList.size(); i++) {
            if (cryptoList.get(i).getId().equals(cryptoId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //// need lock //added lock
    private String join(String userid) {
        try {
            user_lock.acquire();
            if (isExistedUser(userid)) {
                return "Register denied. You are current in JD Cannot Scam system";
            }
            userList.add(new User(userid)); // lock here, insert new user
            return "Register successfully";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Sorry something wrong happen to the system!";
        } finally {
            user_lock.release();
        }
    }

    //// need lock //added lock
    private String exit(String userid) {
        int index = -1;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(userid)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            try {
                user_lock.acquire();
                userList.remove(index); // lock here, modify user list
                return "Exit successfully";
            } catch (Exception e) {
                e.printStackTrace();
                return "Something wrong happen to the system!";
            } finally {
                user_lock.release();
            }
        } else {
            return "You have not registered yet. Command /start to register";
        }
    }

    // need lock //added lock
    private String setBase(String userId, String cryptoId, float base) {
        cryptoId = cryptoId.toUpperCase(Locale.ROOT);
        if (!SupportedCrypto.getSupportedCrypto().isSupportedCrypto(cryptoId))
            return "Cannot define crypto code";
        try {
            user_lock.acquire();
            for (User user : userList) {
                if (user.getId().equals(userId)) {
                    try {
                        crypto_list_lock.acquire();
                        user.updateWatchedCrypto(new WatchedCrypto(cryptoId, base)); // lock here, modify user's crypto
                                                                                     // list
                        return "Successfully update";
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Cannot update due to some error!";
                    } finally {
                        crypto_list_lock.release();
                    }
                }
            }
            return "You have not registered yet. Command /start to register";
        } catch (Exception e) {
            e.printStackTrace();
            return "Something wrong happen to the system!";
        } finally {
            user_lock.release();
        }

    }

    //// need lock //added lock
    private String drop(String userId, String cryptoId) {
        cryptoId = cryptoId.toUpperCase(Locale.ROOT);
        if (SupportedCrypto.getSupportedCrypto().isSupportedCrypto(cryptoId)) {
            try {
                user_lock.acquire();
                for (User user : userList) {/// iterating, lock
                    if (user.getId().equals(userId)) {
                        try {
                            crypto_list_lock.acquire();
                            boolean success = user.deleteWatchedCrypto(cryptoId); // lock here, modify user's crypto
                                                                                  // list
                            if (success)
                                return "Successfully stop watching " + cryptoId;
                            else
                                return "You have not watched " + cryptoId + " yet";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Cannot update due to some error";
                        } finally {
                            crypto_list_lock.release();
                        }

                    }
                }
                return "You have not registered yet. Command /start to register";
            } catch (Exception e) {
                e.printStackTrace();
                return "Something wrong happen to the system!";
            } finally {
                user_lock.release();
            }

        }
        return "Cannot define crypto code";
    }

    //// don't need lock
    private String getPriceById(String cryptoId) {
        cryptoId = cryptoId.toUpperCase(Locale.ROOT);
        for (Crypto crypto : cryptoList) {
            if (crypto.getId().equals(cryptoId)) {
                return cryptoId + "/USD: " + String.valueOf(crypto.getCurrentPrice());
            }
        }
        return "Crypto is not supported";
    }

    //// don't need lock
    public void updateCryptoList(int timeSleep) {
        HttpRequest httpRequest = new HttpRequest();
        while (true) {
            for (Crypto crypto : cryptoList) {
                crypto.setCurrentPrice(httpRequest.getPrice(crypto.getId()));
            }
            try {
                Thread.sleep(timeSleep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ///// don't need lock
    private void sendWarning(User user, WatchedCrypto watchedCrypto, Crypto crypto) {
        String message = watchedCrypto.getCryptoId() + " is under your base price now. "
                + watchedCrypto.getCryptoId() + "/USD: " + crypto.getCurrentPrice();
        SendMessage response = new SendMessage();
        response.setChatId(user.getId());
        response.setText(message);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        // watchedCrypto.setWarned(true);
    }

    ///// need lock //added lock
    public void watchAndWarn(int timeSleep) {
        while (true) {
            try {
                user_lock.acquire();
                for (User user : userList) { /// iterating, lock
                    if (user.getWatchedCryptoList().size() > 0) {
                        try {
                            List<WatchedCrypto> user_list = user.getWatchedCryptoList();
                            crypto_list_lock.acquire();
                            for (WatchedCrypto watchedCrypto : user_list) {//// iterating, lock
                                Crypto crypto = cryptoList.get(findCryptoIndexById(watchedCrypto.getCryptoId()));
                                if (watchedCrypto.isLowerThanBase(crypto.getCurrentPrice())
                                        && !watchedCrypto.isWarned()) {
                                    sendWarning(user, watchedCrypto, crypto);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            crypto_list_lock.release();
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                user_lock.release();
            }

            try {
                Thread.sleep(timeSleep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "hnhntestbot";
    }

    @Override
    public String getBotToken() {
        return "5155661093:AAGt2811TXKw6iUCTBcQj8acKZUEE7w67iQ";
    }
}
