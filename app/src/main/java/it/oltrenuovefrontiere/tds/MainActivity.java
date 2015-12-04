package it.oltrenuovefrontiere.tds;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.facebook.stetho.Stetho;


import it.oltrenuovefrontiere.tds.model.DbAdapter;
import it.oltrenuovefrontiere.tds.view.SheetTableView;

public class MainActivity extends AppCompatActivity {

    DbAdapter adapter;
    ScrollView technicalScrollView;
    SheetTableView sheetView;
    EditText txtCerca;
    String searchString = "";
    String typeString = "";
    ProgressDialog progress;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtCerca = (EditText) findViewById(R.id.txtCerca);


        /* adapter = new DbAdapter(this);
        adapter.open();

        Cursor cursor = adapter.fetchAllTechnical();

        technicalScrollView = (ScrollView) findViewById(R.id.tecnicalScrollView);
        sheetView = new SheetTableView(this , cursor);
        technicalScrollView.addView(sheetView.makeTable());
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new DbAdapter(MainActivity.this);
        adapter.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View v) {
        searchString = txtCerca.getText().toString();
        // progress = ProgressDialog.show(this, "Sto cercando le schede tecniche", "...", true);
        Cursor cursor;
        if (searchString.isEmpty()) {
            cursor = adapter.fetchAllTechnical();
        } else {
            cursor = adapter.fetchTechnicalByFilter(searchString, typeString);
        }
        sheetView = new SheetTableView(MainActivity.this, cursor);
        tableLayout = sheetView.makeTable();
        technicalScrollView = (ScrollView) findViewById(R.id.tecnicalScrollView);
        technicalScrollView.removeAllViews();
        technicalScrollView.addView(tableLayout);

        // Toast toast = Toast.makeText(MainActivity.this, "Ho cercato tutte le schede tecniche contenenti " + searchString, Toast.LENGTH_LONG);
        // toast.show();
    }

    public void radioSelection(View v) {
        RadioButton radioBtn = (RadioButton) v;
        boolean checked = radioBtn.isChecked();

        switch (radioBtn.getId()) {
            case R.id.tuttiRadio:
                if (checked) {
                    typeString = "";
                }
                break;
            case R.id.anelliRadio:
                if (checked) {
                    typeString = "Anelli";
                }
                break;
            case R.id.braccialiRadio:
                if (checked) {
                    typeString = "Bracciali";
                }
                break;
            case R.id.biglierineRadio:
                if (checked) {
                    typeString = "Biglierine";
                }
                break;
            case R.id.ciondoliRadio:
                if (checked) {
                    typeString = "Ciondoli";
                }
                break;
            case R.id.collaneRadio:
                if (checked) {
                    typeString = "Collane";
                }
                break;
            case R.id.orecchiniRadio:
                if (checked) {
                    typeString = "Orecchini";
                }
                break;
            case R.id.orologiRadio:
                if (checked) {
                    typeString = "Orologi";
                }
                break;
        }
    }

    public void aggiornaDB(MenuItem item) {
        adapter.aggiornaDB();
    }
}
