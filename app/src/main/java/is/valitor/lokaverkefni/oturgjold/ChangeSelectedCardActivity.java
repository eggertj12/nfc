package is.valitor.lokaverkefni.oturgjold;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;


public class ChangeSelectedCardActivity extends Activity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_selected_card);

        Context context = getApplicationContext();
        List<Card> cards = Repository.getCards(context);

        ListAdapter cardList = new ChangeSelectedCardArrayAdapter(cards,this);
        ListView viewList = (ListView)findViewById(R.id.change_selected_card);
        viewList.setAdapter(cardList);

        viewList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Context context = getApplicationContext();
        List<Card> cards = Repository.getCards(context);

        Card card = cards.get(position);
//      Toast.makeText(this,card.getCard_name(),Toast.LENGTH_LONG).show();
        Repository.setSelectedCard(context,card);

        finish();
    }
}
