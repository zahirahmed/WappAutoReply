package com.autoai.readnotification;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autoai.readnotification.models.RepliesData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView rc_contacts;
    ImageView done;
    ArrayList<RepliesData> contactList=new ArrayList<>();
    ArrayList<RepliesData> selectedList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);

        rc_contacts=findViewById(R.id.rc_contacts);
        done=findViewById(R.id.done);

        getContactList();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save the task list to preference

                SharedPreferences prefs = getSharedPreferences("AutoReply", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    editor.putString("added_list", ObjectSerializer.serialize(contactList));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editor.apply();
                finish();
            }
        });


    }


    public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{

        Context context;

        public ContactsAdapter(Context context) {

            this.context = context;
        }

        @NonNull
        @Override
        public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View v= LayoutInflater.from(context).inflate(R.layout.contact_layout,viewGroup,false);

            return new ContactsAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ContactsAdapter.MyViewHolder myViewHolder, final int i) {

            final RepliesData repliesData=contactList.get(i);

            //myViewHolder.img.setImageBitmap(repliesData.getPhoto());


            Log.d("wertwerwe",repliesData.isAdded()+"");
            myViewHolder.txt_name.setText(""+repliesData.getName());
            myViewHolder.txt_number.setText(""+repliesData.getNumber());

            myViewHolder.cb.setOnCheckedChangeListener(null);

            myViewHolder.cb.setChecked(repliesData.isAdded());

            myViewHolder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!myViewHolder.cb.isChecked()){
                        myViewHolder.cb.setChecked(true);

                    }
                    else
                    {
                        myViewHolder.cb.setChecked(false);
                    }
                }
            });

            myViewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        repliesData.setAdded(isChecked);

                        Log.d("werwertyu",i+" added "+repliesData.isAdded()+"");

                }
            });

        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txt_name,txt_number;
            CheckBox cb;
            CardView main;
            ImageView img;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                main=itemView.findViewById(R.id.main);
                img=itemView.findViewById(R.id.img);
                txt_name=itemView.findViewById(R.id.txt_name);
                txt_number=itemView.findViewById(R.id.txt_number);
                cb=itemView.findViewById(R.id.cb);

            }
        }

        public void addContact(ArrayList<RepliesData> arrayList){
        /*if (null == arrayList) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(replies);*/

            // save the task list to preference
            SharedPreferences prefs = context.getSharedPreferences("AutoReply", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            try {
                editor.putString("added_list", ObjectSerializer.serialize(contactList));
            } catch (IOException e) {
                e.printStackTrace();
            }
            editor.apply();
            //notifyDataSetChanged();

        }

    }


    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if (null == selectedList) {
            selectedList = new ArrayList<>();
        }

        // load tasks from preference
        SharedPreferences prefs = getSharedPreferences("AutoReply", Context.MODE_PRIVATE);

        try {
            selectedList = (ArrayList<RepliesData>) ObjectSerializer.deserialize(prefs.getString("added_list", ObjectSerializer.serialize(new ArrayList<RepliesData>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                        //retrieveContactPhoto(this,phoneNo);
                        Log.i("contcxxx", "id: " + id);
                        Log.i("contcxxx", "Name: " + name);
                        RepliesData repliesData=new RepliesData(id,name,phoneNo,"Hey there!");

                        contactList.add(repliesData);
                    }


                    if(contactList.size()==selectedList.size()){
                        if(contactList.size()>0 && selectedList.size()>0){
                            for(int i=0;i<contactList.size();i++){
                                contactList.get(i).setAdded(selectedList.get(i).isAdded());
                                contactList.get(i).setMessage(selectedList.get(i).getMessage());
                            }

                        }
                    }

                    rc_contacts.setAdapter(new ContactsAdapter(this));

                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

    }

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_user);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            if (inputStream != null)
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }


}
