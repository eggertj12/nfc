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
 * Created by kla on 29.4.2015.
 */
public class TransactionArrayAdapter extends ArrayAdapter {

    List<Transaction> transList = new ArrayList<Transaction>();
    Context context;


    public TransactionArrayAdapter(List<Transaction> t, Context ctx)
    {
        super(ctx,R.layout.list_transactions,t);
        transList = t;
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
        TextView dateText = (TextView)rowView.findViewById(R.id.date);

        // Set values to text boxes from transaction row
        vendorText.setText(trans.getVendor());
        DecimalFormat dcf = new DecimalFormat("###,###");
        priceText.setText(dcf.format(trans.getPrice()) + " kr.");
        dateText.setText(new SimpleDateFormat("dd.MM.yyyy  HH:mm").format(trans.getDate()));


        // Zebra the listbrah
        /*if(position % 2 != 0) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.black));
        }
        else {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.grey05));
        }*/




        // Return the filled out view
        return rowView;
    }
}
