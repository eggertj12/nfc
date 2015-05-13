package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Transaction;

/**
 * For conforming payment transactions to display in row view of ShowTransactionsActivity
 * Created by kla on 29.4.2015.
 */
public class TransactionArrayAdapter extends ArrayAdapter {

    private List<Transaction> transList = new ArrayList<>();
    private Context context;

    public TransactionArrayAdapter(List<Transaction> transactions, Context ctx) {
        super(ctx, R.layout.list_transactions, transactions);
        transList = transactions;
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get hold of the transaction item from list
        Transaction trans = transList.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the view
        View rowView = inflater.inflate(R.layout.list_transactions, parent, false);

        // Get handle of text boxes
        TextView vendorText = (TextView) rowView.findViewById(R.id.vendor);
        TextView priceText = (TextView) rowView.findViewById(R.id.price);
        TextView dateText = (TextView) rowView.findViewById(R.id.date);

        // Set values to text boxes from transaction row
        vendorText.setText(trans.getVendor());
        DecimalFormat dcf = new DecimalFormat("###,###");
        priceText.setText(dcf.format(trans.getPrice()) + " kr.");

        // American date format is for the weak
        dateText.setText(new SimpleDateFormat("dd.MM.yyyy  HH:mm").format(trans.getDate()));

        // Return the filled out view
        return rowView;
    }
}
