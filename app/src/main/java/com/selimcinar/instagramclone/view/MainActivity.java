package com.selimcinar.instagramclone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.selimcinar.instagramclone.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //Firebase auth tanımlandı Firebase auth giriş çıkış işlemleri yaptırır.
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //FirebaseAuth ile yeni bir obje oluştu
        auth = FirebaseAuth.getInstance();

        /*
        Firebase user ile daha önce giriş yapılmışmı kontrol et
        giriş yapıldıysa diğer aktiviteye geçiş yap

         */
        FirebaseUser  user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(MainActivity.this,FeedActivitiy.class);
            startActivity(intent);
            finish();
        }
    }


    //Kayıtlı kullanıcıyla giriş yap
    public  void signIn(View view){
        //Parola ve şifre ile giriş yap
        //auth.signInWithEmailAndPassword();

        //Dışardan girilen email ve parola değerleri
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if (email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
        }


        else{
            //Email ve parola ile giriş yap
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(
                    new OnSuccessListener<AuthResult>() {
                        //İşlem başarılı ise
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this,FeedActivitiy.class);
                            startActivity(intent); //aktiviteyi başlat diğer aktiviteye geç
                            finish();   //işlem bitince aktiviteyi bitir.

                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                //İşlem başarısız ise
                @Override
                public void onFailure(@NonNull Exception e) {
                    //getLocalizedMessage : kullanıcının anlayabileceği şekilde bir mesaj ver.
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    //Kullanıcı kaydı oluştur
    public void signUp(View view){
        //Dışarıdan kullanıcıdan email ve şifre değerleri alma
        String  email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if (email.equals("") || password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }
        else {
            //Bir kullanıcı oluştur Email ve parola ile giriş için
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(
                    //addOnSuccesListener:Asenkron bir işlem arka planda çalışır.
                    new OnSuccessListener<AuthResult>() {
                        //onSucces : Başarılı olunca ne yapayım
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this, FeedActivitiy.class);
                            startActivity(intent); //aktiviteyi başlat
                            finish(); //bu ekranı aktiviteyi widgetı bitir.
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                //onFailure : Hata verirse ne olacak
                @Override
                public void onFailure(@NonNull Exception e) {
                    //getLocalizedMessage : Kullanıcının anlayacağı dilden hata mesaı ver
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}