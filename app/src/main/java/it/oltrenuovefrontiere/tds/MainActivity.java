package it.oltrenuovefrontiere.tds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import it.oltrenuovefrontiere.tds.model.DbAdapter;
import it.oltrenuovefrontiere.tds.model.TechnicalQueryBuilder;
import it.oltrenuovefrontiere.tds.view.SheetTableView;

public class MainActivity extends AppCompatActivity {

    private DbAdapter adapter;
    private ScrollView technicalScrollView;
    private SheetTableView sheetView;
    private EditText txtCerca;
    private String searchString = "";
    private String typeString = "";
    private TableLayout tableLayout;
    private Button btnLinea;
    private AlertDialog.Builder builder;
    private String lineaString = "";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnLinea = (Button) findViewById(R.id.lineeBtn);
        builder = new AlertDialog.Builder(MainActivity.this);

        txtCerca = (EditText) findViewById(R.id.txtCerca);
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
        if (id == R.id.aggiornaDB) {
            aggiornaDB(item);
            return true;
        }
        if (id == R.id.esportaDB) {
            exportDB(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void exportDB(MenuItem item) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/it.oltrenuovefrontiere.tds/databases/datasheets.db";
                String backupDBPath = "datasheets.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(this, "DB Esportato", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
        }
    }

    public void search(View v) {
        searchString = txtCerca.getText().toString();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Elaborazione in corso...");
                pd.setMessage("Attendere prego");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                Cursor cursor;
                cursor = adapter.fetchTechnicalWithBuilder(new TechnicalQueryBuilder(searchString, typeString, lineaString));
                sheetView = new SheetTableView(MainActivity.this, cursor);
                tableLayout = sheetView.makeTable();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                technicalScrollView = (ScrollView) findViewById(R.id.tecnicalScrollView);
                technicalScrollView.removeAllViews();
                technicalScrollView.addView(tableLayout);
                if (pd != null) {
                    pd.dismiss();
                }
            }

        };

        task.execute((Void[]) null);
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
                    typeString = "Anello";
                }
                break;
            case R.id.braccialiRadio:
                if (checked) {
                    typeString = "Bracciale";
                }
                break;
            case R.id.biglierineRadio:
                if (checked) {
                    typeString = "Biglierina";
                }
                break;
            case R.id.ciondoliRadio:
                if (checked) {
                    typeString = "Ciondolo";
                }
                break;
            case R.id.collaneRadio:
                if (checked) {
                    typeString = "Collana";
                }
                break;
            case R.id.orecchiniRadio:
                if (checked) {
                    typeString = "Orecchini";
                }
                break;
            case R.id.orologiRadio:
                if (checked) {
                    typeString = "Orologio";
                }
                break;
        }
    }

    public void aggiornaDB(MenuItem item) {
        adapter.aggiornaDB();
    }

    public Dialog onCreateDialog(View v) {
        v.setEnabled(false);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            ArrayList<String> lineeList = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Elaborazione in corso...");
                pd.setMessage("Attendere prego");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                lineeList = adapter.fetchDistinctLinea();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                final CharSequence[] items = lineeList.toArray(new CharSequence[lineeList.size()]);
                builder.setTitle("Seleziona una Linea estetica")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String toBeSetted = new String(items[which].toString());
                                if (toBeSetted.contentEquals("Tutte le linee")) {
                                    lineaString = "";
                                    btnLinea.setText("Tutte le linee");
                                } else {
                                    lineaString = toBeSetted;
                                    btnLinea.setText(lineaString);
                                }
                            }
                        });
                if (pd != null) {
                    pd.dismiss();
                    btnLinea.setEnabled(true);
                }
            }

        };
        task.execute((Void[]) null);
        return builder.create();
    }

    public void onLineaChoose(View v) {
        onCreateDialog(v).show();
    }
}