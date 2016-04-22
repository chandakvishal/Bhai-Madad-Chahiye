package com.bhaimadadchahiye.club.ActualMatter.Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.bhaimadadchahiye.club.ActualMatter.Answers.Question;
import com.bhaimadadchahiye.club.R;

import java.util.ArrayList;

public class Search extends AppCompatActivity{

    // Listview Adapter
    private ArrayAdapter<String> adapter;

    private ArrayList<String> questions = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Bundle b = this.getIntent().getExtras();

        ArrayList<Question> questionList = b.getParcelableArrayList("data");

        assert questionList != null;
        for ( Question question : questionList) {
            questions.add(question.body);
        }

        // Listview Data
        String products[] = new String[questions.size()];
        products = questions.toArray(products);

        ListView lv = (ListView) findViewById(R.id.list_view);
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);

        // Adding items to listview
        adapter = new ArrayAdapter<>(this, R.layout.search_bar, R.id.product_name, products);
        lv.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Search.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}
