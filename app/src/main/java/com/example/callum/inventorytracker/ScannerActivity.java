package com.example.callum.inventorytracker;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.*;
import android.widget.Toast;
import java.util.Date;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {

    private Button scanButton;
    DBManager manager;

    /**
     * Created by Callum on 18/11/2016.
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
         //button to open camera for scanning
        scanButton = (Button) findViewById(R.id.scanButton);
        final Activity activity = this;
        manager = new DBManager(this,null,null,1);
        //method to activate when the button is pressed
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //simple parameters for the viewing, post button press
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    /**
     * Created by Callum on 18/11/2016.
     * Method to process the data for whenever a code has been scanned/not found
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {//as long as code is found or test in cancelled
            if (result.getContents() == null) {
                //user has exited the scanner and exit code will be printed to screen
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                //if not already in DB, create item and add it
                if(manager.checkBarcode(result.getContents())==null){
                    //TODO link forms to input data
                    EquipmentItem item = new EquipmentItem("TestName", "TestIndividual",2,result.getContents(),"Date",false);
                    manager.addEquipment(item);
                    //just used for testing
                    Toast.makeText(this, item.getData(), Toast.LENGTH_LONG).show();
                }
                //if in the DB
                else{
                    //TODO : link data to output view page
                    Toast.makeText(this, "Found it", Toast.LENGTH_LONG).show();
                    //EquipmentItem item = manager.getEquipmentItem(result.getContents());
                    //Toast.makeText(this,item.getData(),Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
