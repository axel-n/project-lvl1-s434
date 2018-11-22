package games;

import org.apache.commons.math3.util.MathArrays;

import java.util.Arrays;

public class Drunkard {
    private static final int PARS_TOTAL_COUNT = Par.values().length; // 9
    private static final int CARDS_TOTAL_COUNT = PARS_TOTAL_COUNT * Suit.values().length; // 36

    // чтобы можно было скопировать карту свою и игрока2 в конец перед победой
    private static int[][] playersCards = new int[2][CARDS_TOTAL_COUNT + 1];
    private static int[] playersCardTails = new int[2];

    // не двигаем карты по колоде, постоянно смотрим 1 элемент
    public static void main(String... __) {
        int counter = 1;

        // раздаем карты
        distributeCards();

        playersCardTails[0] = CARDS_TOTAL_COUNT / 2;
        playersCardTails[1] = CARDS_TOTAL_COUNT / 2;


        while (!isEnd()) {

            System.out.printf("Итерация №%d ", counter);
            System.out.printf("игрок №1 карта: %s, игрок №2 карта: %s%n", toString(playersCards[0][0]), toString(playersCards[1][0]));

            int card1 = getIndexSuite(playersCards[0][0]);
            int card2 = getIndexSuite(playersCards[1][0]);

            int winner = checkCards(card1, card2);

            switch (winner) {
                case 0: {
                    System.out.printf("Выиграл игрок №1!%n");

                    playersCards[0][playersCardTails[0]] = playersCards[0][0];
                    playersCards[0][playersCardTails[0] + 1] = playersCards[1][0];

                    playersCardTails[0]++;
                    playersCardTails[1]--;

                    moveCards();

                    System.out.printf("У игрока №1 %d карт, у игрока №2 %d карт%n", playersCardTails[0], playersCardTails[1]);
                    break;
                }


                case 1: {
                    System.out.printf("Выиграл игрок №2!%n");

                    playersCards[1][playersCardTails[1]] = playersCards[1][0];
                    playersCards[1][playersCardTails[1] + 1] = playersCards[0][0];

                    playersCardTails[0]--;
                    playersCardTails[1]++;

                    moveCards();

                    System.out.printf("У игрока №1 %d карт, у игрока №2 %d карт%n", playersCardTails[0], playersCardTails[1]);
                    break;
                }


                case 2: {
                    System.out.printf("Спор - каждый остаётся при своих!%n");

                    playersCards[0][playersCardTails[0]] = playersCards[0][0];
                    playersCards[1][playersCardTails[1]] = playersCards[1][0];

                    moveCards();

                    System.out.printf("У игрока №1 %d карт, у игрока №2 %d карт%n", playersCardTails[0], playersCardTails[1]);
                    break;
                }

            }


            counter++;

        }

        System.out.printf("Выиграл %s игрок. Количество произведённых итераций: %d.", getWinner(), counter);


    }

    enum Suit {
        SPADES, // пики
        HEARTS, // червы
        CLUBS, // трефы
        DIAMONDS // бубны
    }

    enum Par {
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK, // Валет
        QUEEN, // Дама
        KING, // Король
        ACE // Туз
    }

    private static Par getPar(int cardNumber) {
        return Par.values()[cardNumber % PARS_TOTAL_COUNT];
    }

    private static Suit getSuite(int cardNumber) {
        return Suit.values()[cardNumber / PARS_TOTAL_COUNT];
    }

    private static String toString(int cardNumber) {
        return getPar(cardNumber) + " " + getSuite(cardNumber);
    }

    private static void distributeCards() {

        int[] deck = new int[CARDS_TOTAL_COUNT];

        // "создаем" колоду
        for (int i = 0; i < CARDS_TOTAL_COUNT; i++) {
            deck[i] = i;
        }

        // перемешиваем колоду
        MathArrays.shuffle(deck);

        // отмечаем пустые ячейки спец значением
        Arrays.fill(playersCards[0], -1);
        Arrays.fill(playersCards[1], -1);

        System.arraycopy(deck, 0, playersCards[0], 0, CARDS_TOTAL_COUNT / 2);
        System.arraycopy(deck, CARDS_TOTAL_COUNT / 2, playersCards[1], 0, CARDS_TOTAL_COUNT / 2);
    }

    private static int getIndexSuite(int cardNumber) {
        return cardNumber % PARS_TOTAL_COUNT;

        // return Suit.values()[cardNumber % PARS_TOTAL_COUNT];
    }

    // двигаем карты на 1 элемент влево
    public static void moveCards() {

        System.arraycopy(playersCards[0], 1, playersCards[0], 0, playersCardTails[0]);
        System.arraycopy(playersCards[1], 1, playersCards[1], 0, playersCardTails[1]);

        playersCards[0][playersCardTails[0]] = -1;
        playersCards[1][playersCardTails[1]] = -1;
    }

    private static boolean isEnd() {

        if (playersCardTails[0] > 0 || playersCardTails[1] > 0) {
            if (playersCardTails[0] == CARDS_TOTAL_COUNT || playersCardTails[1] == CARDS_TOTAL_COUNT) {
                return true;
            }
        }
        return false;
    }

    private static String getWinner() {

        return playersCardTails[0] == CARDS_TOTAL_COUNT ? "первый" : "второй";
    }

    // узнаем чья карта старше без учета масти
    // возращем код победетеля (или 2, если спор)
    private static int checkCards(int card1, int card2) {

        // проверяем случай, чтобы "шестерка" была больше "туза"
        if ((card1 == 0) && (card2 == 8)) {
            return 0;
        }

        if ((card2 == 0) && (card1 == 8)) {
            return 1;
        }

        if (card1 > card2) {
            return 0;

        } else if (card1 == card2) {
            // спор
            return 2;
        } else return 1;

    }
}