package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

        int count = Repository.getTokenCount(ctx);
        assertEquals(count, 0);

        Token token = new Token();
        token.setTokenitem("Blabla");
        Repository.addToken(ctx, token);

        count = Repository.getTokenCount(ctx);
        assertEquals(1, count);

        Repository.getToken(ctx);

        count = Repository.getTokenCount(ctx);
        assertEquals(0, count);
    }

    @Test
    public void SaveTokenTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        Token token = new Token();
        token.setTokenitem("Blabla");
        Repository.addToken(ctx, token);

        token = Repository.getToken(ctx);
        assertEquals("Blabla", token.getTokenitem());
    }

    @Test
    public void QueueTokenTest()
    {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Context ctx = activity.getApplication();

        Token token = new Token();
        token.setTokenitem("Token1");
        Repository.addToken(ctx, token);
        token = new Token();
        token.setTokenitem("Token2");
        Repository.addToken(ctx, token);

        token = Repository.getToken(ctx);
        assertEquals("Token1", token.getTokenitem());

        token = Repository.getToken(ctx);
        assertEquals("Token2", token.getTokenitem());
    }
}
