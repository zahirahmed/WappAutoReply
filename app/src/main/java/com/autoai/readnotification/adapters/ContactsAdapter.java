package com.autoai.readnotification.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.autoai.readnotification.ObjectSerializer;
import com.autoai.readnotification.R;
import com.autoai.readnotification.models.RepliesData;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.contact_layout,viewGroup,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final RepliesData repliesData=arrayList.get(i);

        myViewHolder.txt_name.setText("Name : "+repliesData.getName());
        myViewHolder.txt_number.setText("Phone : "+repliesData.getNumber());

        myViewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList2.add(repliesData);
                }
                else {
                    arrayList2.remove(repliesData);
                }

                addReply(arrayList2);

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
