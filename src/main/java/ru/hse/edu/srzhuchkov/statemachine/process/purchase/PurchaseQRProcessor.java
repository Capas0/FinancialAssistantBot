package ru.hse.edu.srzhuchkov.statemachine.process.purchase;

import com.google.zxing.NotFoundException;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.edu.srzhuchkov.QRCodeReader;
import ru.hse.edu.srzhuchkov.database.TempPurchase;
import ru.hse.edu.srzhuchkov.statemachine.State;
import ru.hse.edu.srzhuchkov.statemachine.process.StateProcessor;
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
    @Override
    protected boolean validate(Message message) {
        if (message.hasPhoto()) {
            return true;
        }
        if (message.hasText() && message.getText().equals("Отмена")) {
            state = State.ADD_PURCHASE;
            TempPurchase tempPurchase = TempPurchase.load(userId);
            sendMessage.setText(tempPurchase.toString());
        }
        else {
            sendMessage.setText("Фото не получено.");
        }
        return false;
    }

    /**
     * Processes the received message in a certain state
     *
     * @param message the received message
     */
    @Override
    protected void deepProcess(Message message) {
        state = State.ADD_PURCHASE;

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
            sendMessage.setText("Не удалось загрузить фото с сервера, попробуйте позднее.");
        } catch (NotFoundException e) {
            state = State.ADD_PURCHASE_QR;
            sendMessage.setText("QR код не найден.");
        } catch (IllegalArgumentException | ParseException e) {
            state = State.ADD_PURCHASE_QR;
            sendMessage.setText("QR код не содержит нужной информации.");
        }
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
        if (!text.matches("t=\\d{8}T\\d+&s=[\\d.]+&fn=\\d+&i=\\d+&fp=\\d+&n=\\d+")) {
            throw new IllegalArgumentException();
        }
        res.put("date", new SimpleDateFormat("yyyyMMdd").parse(text.substring(2, 10)));
        res.put("amount", BigDecimal.valueOf(Double.parseDouble(text.split("&")[1].substring(2))));
        return res;
    }
}
