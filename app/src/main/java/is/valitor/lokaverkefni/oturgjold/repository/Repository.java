package is.valitor.lokaverkefni.oturgjold.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;

/**
 * Created by eggert on 09/03/15.
 */
public class Repository {
    private static final String USER_FILE_NAME = "user.json";
    private static final String TOKEN_FILE_NAME = "user.token";
    private static final String CARD_FILE_NAME = "cards.json";

    private static Gson gson = new Gson();
    private static Object lockObject = new Object();

    private static ArrayDeque<Token> tokenQueue = null;

    /**
     * Set the current user object of the app.
     * There can be only one user, it will be overwritten if called again
     * @param ctx   Application context
     * @param user
     */
    public static void setUser(Context ctx, User user) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE);
            String userJson = gson.toJson(user);
            fos.write(userJson.getBytes());

        } catch (FileNotFoundException e) {
            // Should never happen since it is just created if not found
        } catch (IOException e) {
            // TODO:
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // TODO:
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the current registered user.
     *
     * @param ctx
     * @return User object or null if no user set
     */
    public static User getUser(Context ctx) {
        FileInputStream fis = null;
        User user = null;

        try {
            fis = ctx.openFileInput(USER_FILE_NAME);
            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + USER_FILE_NAME).length();

            byte[] buffer = new byte[(int) length];
            int i = fis.read(buffer);
            String userJson = new String(buffer, "UTF-8");
            user = gson.fromJson(userJson, User.class);

        } catch (FileNotFoundException e) {
            // Nothing found
            return null;
        } catch (IOException e) {
            // TODO:
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // TODO:
                e.printStackTrace();
            }
        }

        return user;
    }


    /**
     * Add a token to the current token queue.
     * @param ctx
     * @param token
     */
    public static void addToken(Context ctx, Token token) {
        ArrayDeque<Token> tokens = loadQueue(ctx);
        tokens.add(token);
        saveQueue(ctx, tokens);
    }

    /**
     * Get the next token from token queue
     * @param ctx
     * @return Token or null if empty
     */
    public static Token getToken(Context ctx) {
        ArrayDeque<Token> tokens = loadQueue(ctx);
        Token token = tokens.poll();
        saveQueue(ctx, tokens);
        return token;
    }

    /**
     * Get the number of available tokens in the queue
     * @param ctx
     * @return  int number of tokens
     */
    public static int getTokenCount(Context ctx) {
        ArrayDeque<Token> tokens = loadQueue(ctx);
        return tokens.size();
    }

    /**
     * Load the queue from internal storage
     * @param ctx
     * @return
     */
    private static ArrayDeque<Token> loadQueue(Context ctx) {
// Disabled to test that file saving is for sure working
//        if (tokenQueue != null) {
//            return tokenQueue;
//        }
        FileInputStream fin = null;

        ArrayDeque<Token> tokenQueue = new ArrayDeque<Token>();
        try {
            fin = ctx.openFileInput(CARD_FILE_NAME);

            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + CARD_FILE_NAME).length();

            byte[] buffer = new byte[(int) length];
            int i = fin.read(buffer);
            String cardJson = new String(buffer, "UTF-8");
            tokenQueue = gson.fromJson(cardJson, new TypeToken<ArrayDeque<Token>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tokenQueue;
    }

    private static void saveQueue(Context ctx, ArrayDeque<Token> tokens) {
        FileOutputStream fos = null;

        try {
            fos = ctx.openFileOutput(CARD_FILE_NAME, Context.MODE_PRIVATE);

            String nTokenJson = gson.toJson(tokens);
            fos.write(nTokenJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addCard(Context ctx, Card card) {
        FileOutputStream fos = null;
        FileInputStream fin = null;

        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            fin = ctx.openFileInput(CARD_FILE_NAME);

            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + CARD_FILE_NAME).length();

            byte[] buffer = new byte[(int) length];
            int i = fin.read(buffer);
            String cardJson = new String(buffer, "UTF-8");
            cards = gson.fromJson(cardJson, new TypeToken<ArrayList<Card>>() {
            }.getType());
            System.out.println(cards.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cards.add(card);

        try {
            fos = ctx.openFileOutput(CARD_FILE_NAME, Context.MODE_PRIVATE);

            String nCardJson = gson.toJson(cards);
            System.out.println(nCardJson);
            fos.write(nCardJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Card> getCards(Context ctx) {
        FileInputStream fin = null;
        Card card = null;
        ArrayList<Card> cards = new ArrayList<Card>();

        try{
            fin = ctx.openFileInput(CARD_FILE_NAME);
        }
        catch (IOException e) {

        }
        try {
            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + CARD_FILE_NAME).length();
            byte[] buffer = new byte[(int) length];
            int i = fin.read(buffer);
            String cardJson = new String(buffer, "UTF-8");
            cards = gson.fromJson(cardJson, new TypeToken<ArrayList<Card>>() {}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

   
    public static Card getCardByName(Context ctx, String name) {
        ArrayList<Card> cards = getCards(ctx);

        for(Card c : cards) {
            if (c.getCard_name().contentEquals(name)) {
                return c;
            }
        }
        return null;
    }

    private static Card getFirstCard(Context ctx) {
        ArrayList<Card> cards = getCards(ctx);

        for(Card c : cards) {
            return c;
        }
        return null;
    }

    /**
     * Returns the selected default card
     * @param ctx
     * @return
     */
    public static Card getDefaultCard(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        // Second string in function call is value used if no value found in preferences
        String defaultCardName = sp.getString("defaultCard", "main");

        Card rCard = getCardByName(ctx, defaultCardName);
        if(rCard != null) {
            return rCard;
        }
        return getFirstCard(ctx);
    }

}