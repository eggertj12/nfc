package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
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
 * Created by kla on 30.4.2015.
 */
public class ChangeSelectedCardArrayAdapter extends ArrayAdapter {

    List<Card> cards = new ArrayList<Card>();
    Context context;

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
        TextView lastFour = (TextView) rowView.findViewById(R.id.last_four);

        // Set values to text boxes from transaction row
        cardNameText.setText(card.getCard_name());
        lastFour.setText(context.getResources().getString(R.string.card_number_xes) + card.getLast_four());

        // Set background color of currently selected card to background blue
        if(card.getCard_id() == Repository.getSelectedCard(context).getCard_id()) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.background));
        };

        // Return the filled out view
        return rowView;
    }

}