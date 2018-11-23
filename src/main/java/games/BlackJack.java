package games;

import org.slf4j.Logger;

import java.io.IOException;

public class BlackJack {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BlackJack.class);

    private static int[] commonDeck;
    private static int commonDeckCursor;

    private static int[][] playersCards;
    private static int[] playersCursors;

    private static int[] playersMoney = {100, 100};

    private static final int MAX_VALUE = 21;
    private static final int MAX_VALUE_FOR_AI = 16;
    private static final int MAX_CARDS_COUNT = 8;

    private static final int ID_USER = 0;
    private static final int ID_AI = 1;

    private static final int COUNT_CARDS_ON_START = 2;
    private static final int BET = 10;

    // для получения временных результов с картой
    private static String card;

    private static int value(int card) {
        switch (CardUtils.getPar(card)) {
            case JACK:
                return 2;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
                return 10;
            case ACE:
            default:
                return 11;
        }
    }

    private static void initRound() {
        log.info("У Вас {}$, у компьютера {}$. Начинаем новый раунд!", playersMoney[ID_USER], playersMoney[ID_AI]);
        commonDeck = CardUtils.getShaffledCards();
        playersCards = new int[2][MAX_CARDS_COUNT];
        playersCursors = new int[]{0, 0};
        commonDeckCursor = 0;
    }

    private static String addCard2Player(int player) {

        int indexCard = commonDeck[commonDeckCursor];

        playersCards[player][playersCursors[player]] = indexCard;

        playersCursors[player]++;
        commonDeckCursor++;

        return CardUtils.toString(indexCard);
    }

    static int sumPointsPlayer(int player) {

        int sum = 0;

        for (int i = 0; i < playersCursors[player]; i++) {

            sum += value(playersCards[player][i]);
        }

        return sum;
    }

    // получение суммы игрока, если она валидная
    // 0, если превысили порог MAX_VALUE
    static int getFinalSum(int player) {
        int sum = sumPointsPlayer(player);

        return sum <= MAX_VALUE ? sum : 0;
    }

    static boolean existWinner() {
        return ((playersMoney[ID_AI] - BET) < 0) || ((playersMoney[ID_USER] - BET) < 0);
    }

    public static void main(String... __) throws IOException {


        while (!existWinner()) {

            initRound();

            // ход человека
            // по правилами, 2 раза должны давать карту
            for (int j = 0; j < COUNT_CARDS_ON_START; j++) {
                card = addCard2Player(ID_USER);
                log.info("Вам выпала карта {}", card);
            }

            for (int j = 0; j < MAX_CARDS_COUNT; j++) {
                int sum = sumPointsPlayer(ID_USER);

                if (sum <= MAX_VALUE) {

                    if (Choice.confirm("Берём ещё?")) {

                        card = addCard2Player(ID_USER);
                        log.info("Вам выпала карта {}", card);

                    } else {
                        // Пользователь не хочет больше брать
                        break;
                    }
                }
            }

            // ход компьютера
            // по правилами, 2 раза должны давать карту
            // можно конечно объединить раздачу карт, но правила hexlet требуют такой порядок действий
            for (int j = 0; j < COUNT_CARDS_ON_START; j++) {
                card = addCard2Player(ID_AI);
                log.info("Компьютеру выпала карта {}", card);
            }

            for (int j = 0; j < MAX_CARDS_COUNT; j++) {

                int sum = sumPointsPlayer(ID_AI);

                if (sum <= MAX_VALUE_FOR_AI) {

                    card = addCard2Player(ID_AI);
                    log.info("Компьютер решил взять ещё и ему выпала карта {}", card);
                }
            }

            // считаем итог
            int sum1 = getFinalSum(ID_USER);
            int sum2 = getFinalSum(ID_AI);

            log.info("Сумма ваших очков - {}, компьютера - {}", sum1, sum2);

            if (sum1 > sum2) {
                log.info("Вы выиграли раунд! Получаете {}$", BET);
                playersMoney[ID_USER] += BET;
                playersMoney[ID_AI] -= BET;
            } else if (sum1 == sum2) {
                log.info("Ничья! Все остаются при своих");
            } else {
                log.info("Вы проиграли раунд! Теряете {}$", BET);
                playersMoney[ID_USER] -= BET;
                playersMoney[ID_AI] += BET;
            }
        }

        if (playersMoney[ID_USER] > 0)
            log.info("Вы выиграли! Поздравляем!");
        else
            log.info("Вы проиграли. Соболезнуем...");
    }
}