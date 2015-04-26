package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.GetBalanceTask;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;


/**
 * Fragment for displaying the selected card
 */
public class CardFragment extends Fragment {

    public static final String CARDFRAGMENT_CARDINDEX = "cardindex";

    private Context ctx;
    private View rootView;
    private int cardIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_card, container, false);

        Bundle args = getArguments();
        cardIndex = args.getInt(CARDFRAGMENT_CARDINDEX);

        ctx = getActivity().getApplication();

        TextView cardNumber = (TextView) rootView.findViewById(R.id.fragmentCardNumber);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "ocr_a_std.ttf");
        cardNumber.setTypeface(font);

        drawUI();

        return rootView;
    }

    public void refreshUI() {
        drawUI();
    }

    private void drawUI() {
        Card card = Repository.getCardByIndex(ctx, cardIndex);
        int cardCount = Repository.getCardCount(ctx);

        String name = card.getCard_name();
        if (name.equals("")) {
            name = String.format("Kort %d", cardIndex + 1);
        }

        ((TextView) rootView.findViewById(R.id.fragmentCardName)).setText(name);
        ((TextView) rootView.findViewById(R.id.fragmentCardNumber)).setText("XXXX XXXX XXXX " + card.getLast_four());

        TextView balance = (TextView) rootView.findViewById(R.id.fragmentCardBalance);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.isConnected(ctx)) {
                    getCurrentBalance();
                }

                else {

                    Toast toast = Toast.makeText(ctx, "Ekkert netsamband", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        getCurrentBalance();
    }

    /*
    Fetch the balance of the currentCard
     */
    public void getCurrentBalance()
    {
        if (NetworkUtil.isConnected(ctx)) {
            try {

                final TextView balance = (TextView) rootView.findViewById(R.id.fragmentCardBalance);
                final AsyncTaskCompleteListener<Integer> listener = new AsyncTaskCompleteListener<Integer>() {
                    @Override
                    public void onTaskComplete(final Integer result) {
                        DecimalFormat fmt = new DecimalFormat("###,###.##");
                        balance.setText(fmt.format(result));
                    }
                };

                // get currently selected card
                Card card = Repository.getSelectedCard(ctx);
                int currentCard = card.getCard_id();
                balance.setText(String.format("Sæki stöðu", currentCard));
                new GetBalanceTask(listener).execute("https://kortagleypir.herokuapp.com/card/balance/" + currentCard);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            final TextView balance = (TextView) rootView.findViewById(R.id.fragmentCardBalance);
            balance.setText(R.string.card_fragment_balance);
        }
    }
}
