package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

import im.dacer.androidcharts.LineView;

public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER = 0;
    private LineView lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        lineView = (LineView) findViewById(R.id.line_view);
        lineView.setDrawDotLine(true); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        Bundle args = new Bundle();
        args.putString("symbol", getIntent().getStringExtra("symbol"));
        getSupportLoaderManager().initLoader(CURSOR_LOADER, args, this);
        TextView symbol = (TextView) findViewById(R.id.below_graph_title);
        symbol.setText(getIntent().getStringExtra("symbol"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI, new String[]{QuoteColumns.BIDPRICE, QuoteColumns.CREATED},
                QuoteColumns.SYMBOL + "=?", new String[]{args.getString("symbol")}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<String> bottom = new ArrayList<>(data.getCount());
        ArrayList<ArrayList<Integer>> dataSet = new ArrayList<>();
        data.moveToFirst();
        ArrayList<Integer> temp = new ArrayList<>(1);
        for (int i = 0; i < data.getCount(); i++) {
            String created = data.getString(data.getColumnIndex(QuoteColumns.CREATED));
            String bText = created == null ? "D:" + i : created;
            bottom.add(bText);
            int price = (int) Float.parseFloat(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
            temp.add(price);
            data.moveToNext();
        }
        dataSet.add(temp);
        lineView.setBottomTextList(bottom);
        lineView.setDataList(dataSet);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
