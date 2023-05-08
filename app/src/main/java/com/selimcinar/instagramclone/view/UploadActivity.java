package com.selimcinar.instagramclone.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.selimcinar.instagramclone.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    private StorageReference storageReference;
    Bitmap selectedImage;
    //Aktivetede göstermek için global tanımlar yapıldı
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;

    private  ActivityUploadBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public  void  uploadBtn(View view){

        if (imageData != null){
        //universal unique id
            UUID uuid = UUID.randomUUID();
            String imageName = "images /"+uuid+".jpg";
            //Verileri fireStore'a ekleme işlemi
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Download url
                    StorageReference newReference = firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            String comment = binding.commentText.getText().toString();
                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();

                            HashMap<String,Object> postData = new HashMap<>();
                            postData.put("useremail",email);
                            postData.put("downloadurl",downloadUrl);
                            postData.put("comment",comment);
                            postData.put("date", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                   Intent intent = new Intent(UploadActivity.this,FeedActivitiy.class);
                                   //FlagActivityClearTop diğer aktiviteleri siler bu aktiviteyi çalıştırır.
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast mesajı ver hata olursa
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }
    }
    public  void  selectImage(View view) {
        //Resim seçme
        //Eğer izin verilmemişse
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Snackbar ile izin al
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ask permissiın
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            //Galeriye git izin verildi
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

        //android 13 ve üzeri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //Snackbar ile izin al
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //ask permissiın
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    //ask permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                //Galeriye git izin verildi
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }
    }
    //Aktivede gösterme işleminden önce Activity result için kayıt metotları
    private  void  registerLauncher(){
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    //Aktivite oluşunca şu olsun
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            //Herşey oluşunca intentle veriyi al
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                //imagedata adlı değişkene verileri ekle
                                imageData = intentFromResult.getData();


                            try {
                                if (Build.VERSION.SDK_INT>=28){
                                    ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                                    selectedImage = ImageDecoder.decodeBitmap(source);
                                    binding.imageView.setImageBitmap(selectedImage);
                                }
                                else {
                                    selectedImage = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);
                                    binding.imageView.setImageBitmap(selectedImage);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }


                            }
                        }
                    }
                }
        );
        //Izın almak için launcher yazıldı
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),new ActivityResultCallback<Boolean>(){
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    //permission denied
                    Toast.makeText(UploadActivity.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                }
            }

        });
    }


}
