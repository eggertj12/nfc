package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;

/**
 * For conforming cards to display in rows on ChangeSelectedCardActivity
 * Created by kla on 30.4.2015.
 */
public class ChangeSelectedCardArrayAdapter extends ArrayAdapter {

    private List<Card> cards = new ArrayList<>();
    private final Context context;

    public ChangeSelectedCardArrayAdapter(List<Card> c, Context ctx) {
        super(ctx, R.layout.list_cards, c);
        cards = c;
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get hold of the transaction item from list
        Card card = cards.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the view
        View rowView = inflater.inflate(R.layout.list_cards, parent, false);

        // Get handle of text boxes
        TextView cardNameText = (TextView) rowView.findViewById(R.id.card_name);
        TextView lastFour = (TextView) rowView.findViewById(R.id.card_number);
        TextView cardIcon = (TextView) rowView.findViewById(R.id.card_icon);
        TextView radioButton = (TextView) rowView.findViewById(R.id.radio_Button);

        //to get the picture of the card and radio button right
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/icomoon.ttf");
        cardIcon.setTypeface(tf);
        radioButton.setTypeface(tf);

        //Get the selected card
        Card c =  Repository.getSelectedCard(context);

        if(card.getCard_id()== c.getCard_id())
        {
            radioButton.setText(context.getResources().getString(R.string.selected_card));
            radioButton.setTextColor(context.getResources().getColor(R.color.orange4));
        }else{
            // Set values to text boxes from transaction row
            radioButton.setText(context.getResources().getString(R.string.not_selected_card));
        }

        cardNameText.setText(card.getCard_name());
        lastFour.setText(context.getResources().getString(R.string.card_number_xes) + card.getLast_four());

        // Return the filled out view
        return rowView;
    }

}