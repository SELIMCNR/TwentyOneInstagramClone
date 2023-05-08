package com.selimcinar.instagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.selimcinar.instagramclone.R;
import com.selimcinar.instagramclone.adapter.PostAdapter;
import com.selimcinar.instagramclone.databinding.ActivityFeedActivitiyBinding;
import com.selimcinar.instagramclone.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivitiy extends AppCompatActivity {

    //Global Firebase tanımlandI
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;
    private ActivityFeedActivitiyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedActivitiyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();
        //Firebase initilaize edildi .
        auth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        GetData();
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.RecyclerView.setAdapter(postAdapter);
    }

    private void GetData(){

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivitiy.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            if (value != null){
                for (DocumentSnapshot snapshot :value.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String userEmail = (String) data.get("usermail");
                    String comment = (String) data.get("comment");
                    String downloadUrl = (String) data.get("downloadurl");

                    Post post = new Post(userEmail,comment,downloadUrl);
                    postArrayList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }
            }
        });
    }
    //Menu bağlama işlemleri
    //Menu oluşunca ne olacak
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menuyu bağladık
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menude elemana tıklanınca ne olacak
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.add_post){
            //Upload Activity
            //Diğer aktiviteye geçiş kodları
            Intent intentToUpload = new Intent(FeedActivitiy.this,UploadActivity.class);
            startActivity(intentToUpload);

        }
        else if (item.getItemId() == R.id.signout){
            //Signout
            //Hesaptan çıkış yap
            auth.signOut();

            //Diğer aktiviteye geçiş kodları
            Intent intentToMain = new Intent(FeedActivitiy.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}