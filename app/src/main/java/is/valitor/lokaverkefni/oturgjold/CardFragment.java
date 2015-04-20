package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;


/**
 * Fragment for displaying the selected card
 */
public class CardFragment extends Fragment {

    public static final String CARDFRAGMENT_CARDINDEX = "cardindex";

    private View rootView;
    private int cardIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_card, container, false);

        Bundle args = getArguments();
        cardIndex = args.getInt(CARDFRAGMENT_CARDINDEX);

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
        Card card = Repository.getCardByIndex(getActivity().getApplication(), cardIndex);
        int cardCount = Repository.getCardCount(getActivity().getApplication());

        String name = card.getCard_name();
        if (name.equals("")) {
            name = String.format("Kort %d", cardIndex + 1);
        }

        ((TextView) rootView.findViewById(R.id.fragmentCardName)).setText(name);
        ((TextView) rootView.findViewById(R.id.fragmentCardNumber)).setText("XXXX XXXX XXXX " + card.getLast_four());

    }
}
