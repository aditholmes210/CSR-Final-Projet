package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aditas.bigproj.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    ImageView close, imgProf;
    TextView save, change;
    MaterialEditText full, user, bio;
    FirebaseUser fUser;
    private Uri imgUri;
    private StorageTask upTask;
    StorageReference upRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit_profile);

        close = findViewById(R.id.close);
        imgProf = findViewById(R.id.img_prof);
        save = findViewById(R.id.save);
        change = findViewById(R.id.tv_change);
        full = findViewById(R.id.fullname);
        user = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        upRef = FirebaseStorage.getInstance().getReference("uploads");
        DocumentReference ref = FirebaseFirestore.getInstance().collection("users")
                .document(fUser.getUid());
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User usr = value.toObject(User.class);
                full.setText(usr.getFname());
                user.setText(usr.getUname());
                bio.setText(usr.getBio());
                Glide.with(getApplicationContext()).load(usr.getImgurl()).into(imgProf);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });

        imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(full.getText().toString(),
                        user.getText().toString(),
                        bio.getText().toString()
                );
            }
        });
    }

    private void update(String full, String user, String bio){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", full);
        map.put("username", user);
        map.put("bio", bio);

        ref.updateChildren(map);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cResolv = getContentResolver();
        MimeTypeMap mimeMap = MimeTypeMap.getSingleton();
        return mimeMap.getExtensionFromMimeType(cResolv.getType(uri));
    }

    private void upImg(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imgUri != null){
            final StorageReference fileRef = upRef.child(System.currentTimeMillis()
            +"."+ getFileExtension(imgUri));

            upTask = fileRef.putFile(imgUri);
            upTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        String myUrl = downUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                .child(fUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageurl", ""+myUrl);
                        ref.updateChildren(map);
                        pd.dismiss();
                    } else {
                        Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    //ctrl+o

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();

            upImg();
        } else {
            Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
