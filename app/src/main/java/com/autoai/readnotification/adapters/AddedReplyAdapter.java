package com.autoai.readnotification.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autoai.readnotification.ObjectSerializer;
import com.autoai.readnotification.R;
import com.autoai.readnotification.models.RepliesData;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AddedReplyAdapter extends RecyclerView.Adapter<AddedReplyAdapter.MyViewHolder>{

    ArrayList<RepliesData> arrayList;
    ArrayList<RepliesData> contactsList;
    Context context;

    public AddedReplyAdapter(ArrayList<RepliesData> arrayList,ArrayList<RepliesData> contactsList, Context context) {
        this.arrayList = arrayList;
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.added_layout,viewGroup,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final RepliesData repliesData=arrayList.get(i);

        myViewHolder.txt_name.setText(""+repliesData.getName());
        myViewHolder.txt_number.setText(""+repliesData.getNumber());

        myViewHolder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.dialog);

                final EditText txt_msg=dialog.findViewById(R.id.txt_msg);
                Button btn_add=dialog.findViewById(R.id.btn_add);

                txt_msg.append(""+repliesData.getMessage());

                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.get(i).setMessage(txt_msg.getText().toString());
                        for(int j=0;j<contactsList.size();j++){
                            if(contactsList.get(j).getNumber().equalsIgnoreCase(arrayList.get(i).getNumber())){
                                contactsList.get(j).setMessage(txt_msg.getText().toString());
                            }
                        }
                        addReply(contactsList);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name,txt_msg,txt_number;
        CardView main;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_number=itemView.findViewById(R.id.txt_number);
            main=itemView.findViewById(R.id.main);

        }
    }

    public void addReply(ArrayList<RepliesData> arrayList){
/*

        if (null == arrayList) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(repliesData);
*/

        // save the task list to preference
        SharedPreferences prefs = context.getSharedPreferences("AutoReply", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("added_list", ObjectSerializer.serialize(arrayList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
        notifyDataSetChanged();

    }

}
