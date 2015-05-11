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
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eggert on 09/03/15.
 *
 * This static class handles persisting data
 * Stores all data in private files on local storage
 */
public class Repository {
    private static final String USER_FILE_NAME = "user.json";
    private static final String TOKEN_FILE_NAME = "token.json";
    private static final String CARD_FILE_NAME = "cards.json";

    private static Gson gson = new Gson();

    /**
     * Set the current user object of the app.
     * There can be only one user, it will be overwritten if called again
     * @param ctx application context that the file data is saved for   Application context
     * @param user new user object
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
     * @param ctx application context that the file data is saved for
     * @return User object or null if no user set
     */
    public static User getUser(Context ctx) {
        FileInputStream fis = null;
        User user = null;

        if (!fileExists(ctx, USER_FILE_NAME)) {
            return null;
        }

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
     * @param ctx application context that the file data is saved for
     * @param token the new token to save
     */
    public static void addToken(Context ctx, int card_id, Token token) {
        ArrayDeque<Token> tokens = loadQueue(ctx, card_id);
        tokens.add(token);
        saveQueue(ctx, card_id, tokens);
    }

    /**
     * Get the next token from token queue
     * @param ctx application context that the file data is saved for
     * @return Token or null if empty
     */
    public static Token getToken(Context ctx, int card_id) {

        ArrayDeque<Token> tokens = loadQueue(ctx, card_id);

        Token token = tokens.poll();
        saveQueue(ctx, card_id, tokens);
        return token;
    }

    /**
     * Get the number of available tokens in the queue
     * @param ctx application context that the file data is saved for
     * @return  int number of tokens
     */
    public static int getTokenCount(Context ctx, int card_id) {
        ArrayDeque<Token> tokens = loadQueue(ctx, card_id);
        return tokens.size();
    }

    /**
     * Load the queue from internal storage
     * @param ctx application context that the file data is saved for       The application context
     * @param card_id   Id of card the queue applies to
     * @return ArrayDeque of tokens
     */
    private static ArrayDeque<Token> loadQueue(Context ctx, int card_id) {
        // TODO: store in memory to avoid constant loading from file

        FileInputStream fin = null;

        String filename = Integer.toString(card_id) + TOKEN_FILE_NAME;

        ArrayDeque<Token> tokenQueue = new ArrayDeque<>();

        if (!fileExists(ctx, filename)) {
            return tokenQueue;
        }

        try {
            fin = ctx.openFileInput(filename);

            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + filename).length();

            byte[] buffer = new byte[(int) length];
            int i = fin.read(buffer);
            String cardJson = new String(buffer, "UTF-8");
            Type tokenDequeType =  new TypeToken<ArrayDeque<Token>>() {}.getType();

            // TODO: For some reason this code returns a ArrayDeque<LinkedTreeMap> on some devices
            // (The hacked S3 at least) Need to handle that somehow
            tokenQueue = gson.fromJson(cardJson, tokenDequeType);

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

    /**
     * Save the queue to internal storage
     * @param ctx application context that the file data is saved for       The application context
     * @param card_id   Id of card the queue applies to
     * @param tokens    ArrayDeque of tokens to save
     */
    private static void saveQueue(Context ctx, int card_id, ArrayDeque<Token> tokens) {
        FileOutputStream fos = null;

        String filename = Integer.toString(card_id) + TOKEN_FILE_NAME;

        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);

            String nTokenJson = gson.toJson(tokens, new TypeToken<ArrayDeque<Token>>() {}.getType());

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

    /**
     * Add a card to the list of available cards
     * @param ctx application context that the file data is saved for   The application context
     * @param card  Card to be added
     */
    public static void addCard(Context ctx, Card card) {
        FileOutputStream fos = null;
        FileInputStream fin = null;

        ArrayList<Card> cards = new ArrayList<>();

        if (fileExists(ctx, CARD_FILE_NAME)) {

            try {
                fin = ctx.openFileInput(CARD_FILE_NAME);

                long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + CARD_FILE_NAME).length();

                byte[] buffer = new byte[(int) length];
                int i = fin.read(buffer);
                String cardJson = new String(buffer, "UTF-8");
                cards = gson.fromJson(cardJson, new TypeToken<ArrayList<Card>>() {
                }.getType());
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
        }

        cards.add(card);

        try {
            fos = ctx.openFileOutput(CARD_FILE_NAME, Context.MODE_PRIVATE);

            String nCardJson = gson.toJson(cards);
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
    /**
     * Removes all saved data from the repository
     */
    public static void clearData(Context ctx) {
        File userFile = new File(USER_FILE_NAME);
        File cardFile = new File(CARD_FILE_NAME);
        File tokenFile = new File(TOKEN_FILE_NAME);

        try {
            userFile.delete();
            cardFile.delete();
            tokenFile.delete();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all saved cards from the Repository
     */
    public static void removeAllCards(Context ctx) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(CARD_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(0);
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

    /**
     * Removes all saved user info from the repository
     */
    public static void removeUserInfo(Context ctx){
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(0);
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

    /**
     * Removes all token info from the repository
     */
    public static void removeTokens(Context ctx) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(TOKEN_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(0);
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


    /**
     * Get the collection of saved cards
     * @param ctx application context that the file data is saved for   The application context
     * @return      ArrayList of stored cards
     */
    public static ArrayList<Card> getCards(Context ctx) {
        FileInputStream fin = null;
        ArrayList<Card> cards = new ArrayList<>();

        if (!fileExists(ctx, CARD_FILE_NAME)) {
            return cards;
        }

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
            Type cardArrayType =  new TypeToken<ArrayList<Card>>() {}.getType();
            cards = gson.fromJson(cardJson, cardArrayType);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    /**
     * Convenience method for getting the number of saved cards.
     * @param ctx application context that the file data is saved for
     * @return
     */
    public static int getCardCount(Context ctx) {
        ArrayList<Card> cards = getCards(ctx);
        return cards.size();
    }

    /**
     * Convenience method for getting a card by index in storage.
     * @param ctx application context that the file data is saved for
     * @param index
     * @return
     */
    public static Card getCardByIndex(Context ctx, int index) {
        ArrayList<Card> cards = getCards(ctx);
        return cards.get(index);
    }

    public static Card getCardById(Context ctx, int id) {
        ArrayList<Card> cards = getCards(ctx);

        for(Card c : cards) {
            if (c.getCard_id() == id) {
                return c;
            }
        }
        return null;
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

        if (cards.size() > 0) {
            return cards.get(0);
        }
//         for(Card c : cards) {
//             return c;
//         }
        return null;
    }

    /**
     * Returns the selected default card
     * @param ctx application context that the file data is saved for
     * @return the currently selected card object
     */
    public static Card getSelectedCard(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        // Second string in function call is value used if no value found in preferences
        int cardId = sp.getInt("selectedCardId", -1);

        if (cardId == -1) {
            //fallback if there is no selected card_id
            List<Card> cards = getCards(ctx);
            if(cards.size()> 0)
            {
                return cards.get(0);
            }
            return null;
        }

        Card rCard = getCardById(ctx, cardId);
        if(rCard != null) {
            return rCard;
        }
        return getFirstCard(ctx);
    }

    /**
     * Returns the index of selected default card in cardList
     * @param ctx application context that the file data is saved for
     * @return the index of selected card in list of stored cards
     */
    public static int getSelectedCardIndex(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        // Second string in function call is value used if no value found in preferences
        int cardId = sp.getInt("selectedCardId", -1);

        if (cardId == -1) {
            return 0;
        }

        ArrayList<Card> cards = getCards(ctx);

        int index = 0;
        for(Card c : cards) {
            if (c.getCard_id() == cardId) {
                return index;
            }
            index ++;
        }

        // Not found default to 0
        return -1;
    }

    /**
     * Set the given card as default card
     * @param ctx application context that the file data is saved for
     * @param card the card to be selected
     */
    public static void setSelectedCard(Context ctx, Card card) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();

        int cardId = card.getCard_id();
        editor.putInt("selectedCardId", cardId);
        editor.commit();
    }

    /**
     * Set the card at given index as default card
     * @param ctx application context that the file data is saved for
     * @param index index of new selected card in list of stored cards
     */
    public static void setSelectedCardByIndex(Context ctx, int index) {
        setSelectedCard(ctx, getCardByIndex(ctx, index));
    }


    private static boolean fileExists(Context ctx, String filename) {
        File f = new File(ctx.getFilesDir(), filename);
        return f.exists();
    }
}