package com.autoai.readnotification;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.autoai.readnotification.models.RepliesData;

import java.io.IOException;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView rc_contacts;
    ImageView done;
    ArrayList<RepliesData> contactList=new ArrayList<>();
    ArrayList<RepliesData> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);

        rc_contacts=findViewById(R.id.rc_contacts);
        done=findViewById(R.id.done);

        getContactList();

    }


    public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{

        ArrayList<RepliesData> arrayList;
        ArrayList<RepliesData> arrayList2=new ArrayList<>();
        Context context;

        public ContactsAdapter(ArrayList<RepliesData> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View v= LayoutInflater.from(context).inflate(R.layout.contact_layout,viewGroup,false);

            return new ContactsAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsAdapter.MyViewHolder myViewHolder, int i) {

            final RepliesData repliesData=arrayList.get(i);

            myViewHolder.txt_name.setText("Name : "+repliesData.getName());
            myViewHolder.txt_number.setText("Phone : "+repliesData.getNumber());

            myViewHolder.cb.setChecked(repliesData.isAdded());

            myViewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        arrayList2.add(repliesData);
                    }
                    else {
                        arrayList2.remove(repliesData);
                    }

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addReply(arrayList2);
                            finish();
                        }
                    });

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txt_name,txt_number;
            CheckBox cb;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_name=itemView.findViewById(R.id.txt_name);
                txt_number=itemView.findViewById(R.id.txt_number);
                cb=itemView.findViewById(R.id.cb);

            }
        }

        public void addReply(ArrayList<RepliesData> arrayList){
        /*if (null == arrayList) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(replies);*/

            // save the task list to preference
            SharedPreferences prefs = context.getSharedPreferences("AutoReply", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            try {
                editor.putString("added_list", ObjectSerializer.serialize(arrayList));
            } catch (IOException e) {
                e.printStackTrace();
            }
            editor.commit();
            //notifyDataSetChanged();

        }

    }


    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


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

                        RepliesData repliesData = null;
                        repliesData=new RepliesData(id,name,phoneNo,"Hey there!",false);
                        for(int i=0;i<arrayList.size();i++){
                            if(arrayList.get(i).getNumber().equalsIgnoreCase(phoneNo)){
                                repliesData=new RepliesData(id,name,phoneNo,"Hey there!",true);
                            }
                            else
                            {
                            }
                        }

                        contactList.add(repliesData);

                        rc_contacts.setAdapter(new ContactsAdapter(contactList,this));

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
