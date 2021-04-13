package ru.hse.edu.srzhuchkov.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends BaseCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public HelpCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    /**
     * Execute the command
     *
     * @param absSender absSender to send messages over
     * @param user      the user who sent the command
     * @param chat      the chat, to be able to send replies
     * @param arguments passed arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        sendAnswer(absSender, chat.getId(),
                "*«Добавить покупку»* - добавить в базу данные о покупке: сумму, валюту, дату, категорию и описание;\n" +
                        "*«Общая сумма расходов»* - вывести суммарные расходы в выбранный промежуток времени в указанной валюте;\n" +
                        "*«Список расходов»* - просмотр данных о покупках, совершенных в выбранный промежуток времени;\n" +
                        "*«Расходы в категории»* - просмотр данных о покупках выбранной категории;\n" +
                        "*«Распределение расходов по категориям»* - вывод суммы расходов во для каждой категории, учитывается только выбранная валюта;\n" +
                        "*«Счет»* - вывод текущего остатка для каждой валюты.\n" +
                        "\n" +
                        "*«Счет» -> «Пополнить»* - увеличить баланс для выбранной валюты в базе данных;\n" +
                        "*«Счет» -> «Цель для накопления»* - меню управления целью накопления. При достижении цели будет отправлено сообщение об этом."
        );
    }
}
