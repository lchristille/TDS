package it.oltrenuovefrontiere.tds.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;

import it.oltrenuovefrontiere.tds.MainActivity;
import it.oltrenuovefrontiere.tds.filer.FileEnumerator;
import it.oltrenuovefrontiere.tds.model.DbAdapter;

/**
 * Created by Utente on 03/12/2015.
 */
public class SheetTableView {
    Context ct;
    DbAdapter adapter;
    Cursor cursor;
    TableLayout technicalList;

    public SheetTableView(MainActivity mainActivity, Cursor cursor) {
        Context newct = (Context) mainActivity;
        SheetTableView(newct, cursor);
    }

    public void SheetTableView(Context _ct, Cursor _cursor) {
        this.ct = _ct;
        adapter = new DbAdapter(this.ct);
        this.cursor = _cursor;
    }


    public TableLayout makeTable() {
         technicalList = new TableLayout(ct);
         adapter.open();

                if (cursor.getCount() > 1) {
                    cursor.moveToFirst();
                    do {
                        TableRow row = new TableRow(ct);
                        final Button t = new Button(ct);
                        t.setOnClickListener(new View.OnClickListener() {
                            String _path;
                            Context _ct;

                            @Override
                            public void onClick(View v) {
                                File pdfToOpen = new File(_path);
                                Intent i = new Intent();
                                i.setAction(android.content.Intent.ACTION_VIEW);
                                i.setDataAndType(Uri.fromFile(pdfToOpen), "application/pdf");
                                ct.startActivity(i);
                            }

                            private View.OnClickListener init(String _path, Context _ct) {
                                this._path = _path;
                                this._ct = _ct;
                                return this;
                            }

                        }.init(cursor.getString(2), ct));
                        String backgroundPath = cursor.getString(2).replace(".pdf", ".jpg");
                        if (new File(backgroundPath).isFile()) {
                            Bitmap image = new BitmapFactory().decodeFile(backgroundPath);
                            int originalWidth = image.getWidth();
                            int originalHeight = image.getHeight();
                            int newWidth = -1;
                            int newHeight = -1;
                            float multFactor = -1.0F;
                            if (originalHeight > originalWidth) {
                                newHeight = 150;
                                multFactor = (float) originalWidth / (float) originalHeight;
                                newWidth = (int) (newHeight * multFactor);
                            } else if (originalWidth > originalHeight) {
                                newWidth = 150;
                                multFactor = (float) originalHeight / (float) originalWidth;
                                newHeight = (int) (newWidth * multFactor);
                            } else if (originalHeight == originalWidth) {
                                newHeight = 150;
                                newWidth = 150;
                            }
                            image = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
                            t.setBackground(new BitmapDrawable(image));
                        }
                        TextView txt = new TextView(ct);
                        txt.setText(cursor.getString(1).replace(".pdf", ""));
                        row.addView(t);
                        row.addView(txt);
                        technicalList.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        cursor.moveToNext();
                    } while (!cursor.isLast());
                }
                cursor.close();

                adapter.close();
                //progress.dismiss();
        });
        return technicalList;
    }
}
