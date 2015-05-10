package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Transaction;
import is.valitor.lokaverkefni.oturgjold.service.AsyncTaskCompleteListener;
import is.valitor.lokaverkefni.oturgjold.service.GetTransactionsTask;


public class ShowTransactionsActivity extends Activity implements AsyncTaskCompleteListener<List<Transaction>>{

    static final String TAG = "Show transactions";
    private ProgressBar loadingThings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transactions);
        loadingThings = (ProgressBar) findViewById(R.id.transactionsProgressBar);
        loadingThings.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Card card = Repository.getSelectedCard(getApplication());
        int currentCard = card.getCard_id();
        new GetTransactionsTask(getApplication(),this).execute("https://kortagleypir.herokuapp.com/transaction/all/" + currentCard);
        loadingThings.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskComplete(List<Transaction> result) {

        final TextView noTrans = (TextView) findViewById(R.id.no_transactions);
        loadingThings.setVisibility(View.INVISIBLE);

        if (result.size() > 0) {
            ListAdapter transList;
            transList = new TransactionArrayAdapter(result, this);
            ListView viewList = (ListView) findViewById(R.id.list_transactions);
            viewList.setAdapter(transList);
            noTrans.setText("");
        } else {
            noTrans.setText(getString(R.string.transactions_no_transaction));
        }
    }

}
