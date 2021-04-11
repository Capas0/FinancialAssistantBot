package ru.hse.edu.srzhuchkov.statemachine.process;

import com.google.zxing.NotFoundException;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.QRCodeReader;
import ru.hse.edu.srzhuchkov.database.BotUser;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.telegram.Bot;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseQRProcessor extends StateProcessor {
    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    public SendMessage process(Message message) {
        if (message.hasPhoto()) {
            return deepProcess(message);
        }

        int userId = message.getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        State state = State.ADD_PURCHASE_QR;
        if (message.hasText() && message.getText().equals("Отмена")) {
            state = State.ADD_PURCHASE;
            TempPurchase tempPurchase = TempPurchase.load(userId);
            sendMessage.setText(tempPurchase.toString());
        }
        else {
            sendMessage.setText("Фото не получено.");
        }
        BotUser.setState(userId, state);
        sendMessage.setReplyMarkup(state.display());
        return sendMessage;
    }

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     * @return reply message
     */
    @Override
    protected SendMessage deepProcess(Message message) {
        int userId = message.getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        State state = State.ADD_PURCHASE;

        try {
            List<PhotoSize> photos = message.getPhoto();
            GetFile getFile = new GetFile(photos.get(photos.size() - 1).getFileId());
            Bot bot = Bot.getInstance();
            File file = bot.downloadFile(bot.execute(getFile));

            String text = QRCodeReader.decodeQRCode(file);
            Map<String, Object> info = parseText(text);
            Date date = (Date) info.get("date");
            BigDecimal amount = (BigDecimal) info.get("amount");

            TempPurchase tempPurchase = TempPurchase.load(userId);
            tempPurchase.setDate(date);
            tempPurchase.setAmount(amount);
            tempPurchase.save();
            sendMessage.setText(tempPurchase.toString());

        } catch (IOException | TelegramApiException e) {
            System.out.println("Unable to load photo.");
            e.printStackTrace();
        } catch (NotFoundException e) {
            state = State.ADD_PURCHASE_QR;
            sendMessage.setText("QR код не найден.");
        } catch (IllegalArgumentException | ParseException e) {
            state = State.ADD_PURCHASE_QR;
            sendMessage.setText("QR код не содержит нужной информации.");
        }

        BotUser.setState(userId, state);
        sendMessage.setReplyMarkup(state.display());
        return sendMessage;
    }

    /**
     * Returns the associated state
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.ADD_PURCHASE_QR;
    }

    private Map<String, Object> parseText(String text) throws ParseException {
        HashMap<String, Object> res = new HashMap<>();
        if (!text.matches("t=\\d{8}T\\d{6}&s=[\\d.]+&fn=\\d+&i=\\d+&fp=\\d+&n=\\d+")) {
            throw new IllegalArgumentException();
        }
        res.put("date", new SimpleDateFormat("yyyyMMdd").parse(text.substring(2, 10)));
        res.put("amount", BigDecimal.valueOf(Double.parseDouble(text.split("&")[1].substring(2))));
        return res;
    }
}
