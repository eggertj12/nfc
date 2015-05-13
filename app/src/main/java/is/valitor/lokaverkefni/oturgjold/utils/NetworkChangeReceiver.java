package is.valitor.lokaverkefni.oturgjold.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import is.valitor.lokaverkefni.oturgjold.R;
import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.repository.User;
import is.valitor.lokaverkefni.oturgjold.service.GetTokenTask;

/**
 * For monitoring network connection and initiating retrieval of tokens on network status change
 * Created by eggert on 10/04/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (NetworkUtil.isConnected(ctx)) {

            // Check all cards for missing tokens when we get connectivity
            ArrayList<Card> cards = Repository.getCards(ctx);
            User theUser = Repository.getUser(ctx);

            for (Card card : cards) {
                if (Repository.getTokenCount(ctx, card.getCard_id()) < 3) {
                    Token token = new Token();
                    token.setUsr_id(theUser.getUsr_id());
                    token.setDevice_id(theUser.getDevice_id());
                    token.setCard_id(card.getCard_id());

                    Gson gson = new Gson();
                    String tokenJson = gson.toJson(token, Token.class);
                    new GetTokenTask(ctx).execute(ctx.getString(R.string.service_token_url), tokenJson);
                }
            }

            // Now all cards should have full token count, no need to waste resources by monitoring network
            // For some reason this keeps failing in different ways. Disabled for now
//            NetworkUtil.disableNetworkMonitoring(ctx);

        } else {
            NetworkUtil.enableNetworkMonitoring(ctx);
        }
    }
}
