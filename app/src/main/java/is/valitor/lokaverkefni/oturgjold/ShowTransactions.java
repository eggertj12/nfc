package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.os.Bundle;
import org.json.JSONArray;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Transactions;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.GetTransactionsTask;


public class ShowTransactions extends Activity implements AsyncTaskCompleteListener<List<Transactions>>{
    static  final String TAG = "Show transactions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transactions);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Card card = Repository.getSelectedCard(getApplication());
        int currentCard = card.getCard_id();
        new GetTransactionsTask(getApplication(),this).execute("https://kortagleypir.herokuapp.com/transaction/all/" + currentCard);
    }

    @Override
    public void onTaskComplete(List<Transactions> result) {
        Toast.makeText(getApplicationContext(), "Got result "+result.size(), Toast.LENGTH_LONG).show();

        ArrayList<Transactions> tList = new ArrayList<Transactions>();
        try {
            String a[] = null;
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                Transactions t = new Transactions();
                String s = jArray.getJSONObject(i).getString("vendor");
                int price = jArray.getJSONObject(i).getInt("price");
                String.valueOf(price);
                t.setVendor(s);
                t.setPrice(String.valueOf(price));
                tList.add(t);
            }
        }catch (Exception e){

        }

        ListAdapter transList = new ArrayAdapter<Transactions>(this,android.R.layout.simple_list_item_1,tList);
        ListView viewList = (ListView)findViewById(R.id.list_transactions);
        viewList.setAdapter(transList);
    }

}
