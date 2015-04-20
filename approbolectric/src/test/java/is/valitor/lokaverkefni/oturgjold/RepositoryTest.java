package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by eggert on 08/04/15.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RepositoryTest {

    @Test
    public void UserTest() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        User user = new User();
        user.setName("Eggert");

        Repository.setUser(ctx, user);
        user = Repository.getUser(ctx);

        assertTrue(user.getName().equals("Eggert"));
    }

    @Test
    public void CountTokenTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        int card_id = 1;
        int count = Repository.getTokenCount(ctx, card_id);
        assertEquals(count, 0);

        Token token = new Token();
        token.setTokenitem("Blabla");
        Repository.addToken(ctx, card_id, token);

        count = Repository.getTokenCount(ctx, card_id);
        assertEquals(1, count);

        Repository.getToken(ctx, card_id);

        count = Repository.getTokenCount(ctx, card_id);
        assertEquals(0, count);
    }

    @Test
    public void CountTokensForCardTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        int card_id = 1;

        Token token = new Token();
        token.setTokenitem("Blabla");
        Repository.addToken(ctx, card_id, token);

        card_id = 2;
        Repository.addToken(ctx, card_id, token);

        int count = Repository.getTokenCount(ctx, card_id);
        assertEquals(1, count);
    }

    @Test
    public void SaveTokenTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        int card_id = 1;
        Token token = new Token();
        token.setTokenitem("Card1token");
        Repository.addToken(ctx, card_id, token);

        card_id = 2;
        token = new Token();
        token.setTokenitem("Card2token1");
        Repository.addToken(ctx, card_id, token);
        token.setTokenitem("Card2token2");
        Repository.addToken(ctx, card_id, token);

        token = Repository.getToken(ctx, card_id);
        assertEquals("Card2token1", token.getTokenitem());

        card_id = 1;
        token = Repository.getToken(ctx, card_id);
        assertEquals("Card1token", token.getTokenitem());
    }

    @Test
    public void QueueTokenTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();
        int card_id = 1;

        Token token = new Token();
        token.setTokenitem("Token1");
        Repository.addToken(ctx, card_id, token);
        token = new Token();
        token.setTokenitem("Token2");
        Repository.addToken(ctx, card_id, token);

        Token readToken = Repository.getToken(ctx, card_id);
        assertEquals("Token1", readToken.getTokenitem());

        readToken = Repository.getToken(ctx, card_id);
        assertEquals("Token2", readToken.getTokenitem());

        readToken = Repository.getToken(ctx, card_id);
        assertTrue(readToken == null);
    }

    @Test
    public void getCardByNameTest() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        Card card = new Card();
        card.setCard_name("Card1");
        card.setLast_four("1234");
        Repository.addCard(ctx, card);

        card = null;

        card = Repository.getCardByName(ctx, "Card1");
        assertTrue(card != null);
        assertEquals(card.getLast_four(), "1234");
    }

    @Test
    public void getCardsTest() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        Card card = new Card();
        card.setCard_name("Card1");
        Repository.addCard(ctx, card);

        card = new Card();
        card.setCard_name("Card2");
        Repository.addCard(ctx, card);

        ArrayList<Card> cards = Repository.getCards(ctx);

        assertEquals(2, Repository.getCardCount(ctx));

        String names = "";
        for(Card c : cards) {
            names = names + c.getCard_name();
        }
        assertEquals("Card1Card2", names);
    }


    @Test
    public void selectedCardsTest() {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        Card card1 = new Card();
        card1.setCard_name("Card1");
        card1.setCard_id(1);
        Repository.addCard(ctx, card1);

        Card card2 = new Card();
        card2.setCard_name("Card2");
        card2.setCard_id(2);
        Repository.addCard(ctx, card2);

        Repository.setSelectedCard(ctx, card2);

        assertEquals(1, Repository.getSelectedCardIndex(ctx));
        assertEquals("Card2", Repository.getSelectedCard(ctx).getCard_name());

        Repository.setSelectedCardByIndex(ctx, 0);
        assertEquals(0, Repository.getSelectedCardIndex(ctx));
        assertEquals("Card1", Repository.getSelectedCard(ctx).getCard_name());

    }
}
