package is.valitor.lokaverkefni.oturgjold.service;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import is.valitor.lokaverkefni.oturgjold.R;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;

/**
 * For retrieving tokens from web service.
 * Created by kla on 23.3.2015.
 */
public class GetTokenTask extends RequestTask {
    final private Context appContext;

    public GetTokenTask(Context appContext) {
        super("POST", "application/json", "application/json");
        this.appContext = appContext;
    }

    @Override
    protected void onPostExecute(RequestResult result) {

        // Network error
        if (result.getException() instanceof IOException) {
            Toast.makeText(appContext, appContext.getString(R.string.error_network_token), Toast.LENGTH_LONG).show();
            return;
        }

        if (result.getResultCode() != 200) {
            Toast.makeText(appContext, appContext.getString(R.string.error_general), Toast.LENGTH_LONG).show();
            return;
        }

        try{
            //Retrieve the content from the response and add to repository
            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new StringReader(result.getResultContent()));
            jsonReader.setLenient(true);
            Token token = gson.fromJson(jsonReader, Token.class);

            // Keeping this line in, in case we ever want to notify user of this
            //Toast.makeText(appContext, "Sótti tóken", Toast.LENGTH_LONG).show();

            int currentCard = token.getCard_id();
            Repository.addToken(this.appContext, currentCard, token);

            // Sequentially fill upp token limit
            // Could this be solved more elegantly?
            if (Repository.getTokenCount(this.appContext, currentCard) < 3) {

                // Clear token data, but reuse rest
                token.setTokenitem("");
                String tokenJson = gson.toJson(token, Token.class);
                new GetTokenTask(this.appContext)
                        .execute(this.appContext.getString(R.string.service_token_url), tokenJson);
            }

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(appContext, appContext.getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
    }
}
