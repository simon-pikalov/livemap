package com.example.livemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.livemap.objects.User;
import com.example.livemap.objects.UserListAdapter;
import com.example.livemap.utils.Iso2Phone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    ArrayList<User> contactList;
    ArrayList<User> usertListMatched;
    ArrayList<User> usertList;
    private  String isoPrefix ;
    Button mCreatChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        contactList = new ArrayList<User>();
        usertListMatched = new ArrayList<User>();
        usertList = new ArrayList<User>();
        isoPrefix = getCountryIso();
        mCreatChat = findViewById(R.id.creat);
        mCreatChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });
        initializeRecyclerView();

        getContactList();
        getUsersFromFireBase();
        Log.i("usertList", usertList.toString());
        Log.i("contactList", contactList.toString());

    }


    private void createGroup(){
        Boolean validChat = false;
        ArrayList<String> userIds = new ArrayList<>();
        for(User mUser : usertListMatched){
            if(mUser.isSelected()){
                validChat = true;
                userIds.add(mUser.getId());
            }
        }
//        Log.w("JonReturnToMain", "got user list: "+userIds);
        Intent intent = new Intent();
        intent.putExtra("userList", (Serializable) userIds);
        setResult(RESULT_OK, intent);
        finish();

        if(validChat){

        }

    }

    private void getContactList(){
        String isoPrefix = getCountryIso();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replace(" ","");
            phone = phone.replace("-","");
            phone = phone.replace("(","");
            phone = phone.replace(")","");
            if (!String.valueOf(phone.charAt(0)).equals("+"))  phone= isoPrefix+phone;
            User mContact = new User(name,"",phone);
            contactList.add(mContact);
        }
    }


    private void getUsersFromFireBase(){

        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("/users/");
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> fireList = new ArrayList<>();
               // if (snapshot.exists()){
                Log.i("my snapshot ! ",snapshot.toString());
                    for( DataSnapshot d : snapshot.getChildren()){
                        String name = d.child("name").getValue(String.class);
                        String phone = d.child("phone").getValue(String.class);
                        if (!String.valueOf(phone.charAt(0)).equals("+"))  phone= isoPrefix+phone.substring(1);
                        String id = d.child("id").getValue(String.class);
                        User u = new User(name,id,phone);
                        Log.i("User",u.toString());
                        fireList.add(u);
                    }
                Log.i("fireList",fireList.toString());
                Log.i("usertList",fireList.toString());
                usertList = fireList;
                findMatches();
                }

           // }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void findMatches() {
        boolean exist = false;
        for (User user : usertList) {
            for (User conatct : contactList) {
                exist = false;
                if (user.getPhone().equals(conatct.getPhone())) {
                    Log.i("equals", user.getPhone() + "==" + conatct.getPhone());
                    for (User matched : usertListMatched){
                        if (matched.getPhone().equals(user.getPhone())){
                            exist = true;
                        }
                    }

                    if(!exist) {
                        usertListMatched.add(user);
                        mUserListAdapter.notifyDataSetChanged();
                    }
                }
            }

        }
        return;
    }



    private String getCountryIso(){
     String iso = null;

       TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null && (!telephonyManager.getNetworkCountryIso().toString().equals("")) ){
            iso = telephonyManager.getNetworkCountryIso().toString();
        }
        //Iso2Phone convert iso to phone prefix
     return Iso2Phone.getPhone(iso);
    }


    private void initializeRecyclerView() {
        mUserList =findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(usertListMatched);
        mUserList.setAdapter(mUserListAdapter);
    }




}