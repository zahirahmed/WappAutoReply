package com.autoai.readnotification;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.autoai.readnotification.adapters.AddedReplyAdapter;
import com.autoai.readnotification.models.RepliesData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView rc_added;
    EditText editText,ed_name,ed_message;
    FloatingActionButton btn_add;
    ArrayList<RepliesData> arrayList;
    AddedReplyAdapter addedReplyAdapter;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, NotificationCollectorMonitorService.class));

        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        rc_added = findViewById(R.id.rc_added);
        editText = findViewById(R.id.editText);
        ed_name = findViewById(R.id.ed_name);
        ed_message = findViewById(R.id.ed_message);
        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!ed_name.getText().toString().isEmpty() && !ed_message.getText().toString().isEmpty()){
                    addedReplyAdapter.addReply(ed_name.getText().toString(),ed_message.getText().toString());
                }*/

                Dexter.withActivity(MainActivity.this)
                        .withPermissions(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                startActivity(new Intent(MainActivity.this,ContactsActivity.class));
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            }
                        }).check();

            }
        });

        Intent intent = new Intent(MainActivity.this, MyNotifiService.class);//启动服务
        startService(intent);//Start service
        final SharedPreferences sp = getSharedPreferences("msg", MODE_PRIVATE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getMsg = sp.getString("getMsg", "");
                if (!TextUtils.isEmpty(getMsg)) {
                    editText.setText(getMsg);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                //Open listener reference message // Notification access
                Intent intent_s = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent_s);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_p = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                startActivity(intent_p);
            }
        });

        //getContactList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null == arrayList) {
            arrayList = new ArrayList<>();
        }

        // load tasks from preference
        SharedPreferences prefs = getSharedPreferences("AutoReply", Context.MODE_PRIVATE);

        try {
            arrayList = (ArrayList<RepliesData>) ObjectSerializer.deserialize(prefs.getString("added_list", ObjectSerializer.serialize(new ArrayList<RepliesData>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<RepliesData> selectedList =new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).isAdded()){
                selectedList.add(arrayList.get(i));
            }
        }

        addedReplyAdapter=new AddedReplyAdapter(selectedList,arrayList,this);

        rc_added.setAdapter(addedReplyAdapter);

    }

    public void openNotificationSettings(View view) {
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("contcxxx", "id: " + id);
                        Log.i("contcxxx", "Name: " + name);
                        Log.i("contcxxx", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
}