package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.models.Card;
import is.valitor.lokaverkefni.oturgjold.models.Token;
import is.valitor.lokaverkefni.oturgjold.models.User;

/**
 * Created by eggert on 09/03/15.
 */
public class Repository {
    private static final String USER_FILE_NAME = "user.json";
    private static final String TOKEN_FILE_NAME = "user.token";
    private static final String CARD_FILE_NAME = "cards.json";

    private static Gson gson = new Gson();
    private static Object lockObject = new Object();

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

    public static void setToken(Context ctx, Token token) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(TOKEN_FILE_NAME, Context.MODE_PRIVATE);
            String tokenJson = gson.toJson(token);
            fos.write(tokenJson.getBytes());

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

    public static Token getToken(Context ctx) {
        FileInputStream fis = null;
        Token token = null;

        try {
            fis = ctx.openFileInput(TOKEN_FILE_NAME);
            long length = new File(ctx.getFilesDir().getAbsolutePath() + "/" + TOKEN_FILE_NAME).length();

            byte[] buffer = new byte[(int) length];
            int i = fis.read(buffer);
            String tokenJson = new String(buffer, "UTF-8");
            token = gson.fromJson(tokenJson, Token.class);

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

        return token;
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

}