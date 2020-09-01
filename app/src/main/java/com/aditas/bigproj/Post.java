package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

public class Post extends AppCompatActivity {

    Uri imgUri;
    String myUrl = "";
    StorageTask upTask;
    StorageReference strRef;

    ImageView close, imgAdd;
    TextView post;
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        imgAdd = findViewById(R.id.img_add);
        post = findViewById(R.id.tv_post);
        desc = findViewById(R.id.et_desc);

        strRef = FirebaseStorage.getInstance().getReference("posts");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Post.this, Home.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upImg();
                Toast.makeText(Post.this, "Upload Success", Toast.LENGTH_SHORT).show();
            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(Post.this);
    }

    private String getFileExtension(Context con, Uri uri){
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(con.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    private void upImg(){
//        ProgressDialog progDial = new ProgressDialog(this);
//        ProgressDialog.setMessage("Posting");
//        ProgressDialog.show();

        if(imgUri != null){
            final StorageReference fileref = strRef.child(System.currentTimeMillis()
            +"."+ getFileExtension(getApplicationContext(), imgUri));

            upTask = fileref.putFile(imgUri);
            upTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete()){
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        String postId = ref.push().getKey();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("postId", postId);
                        map.put("postImage", myUrl);
                        map.put("description", desc.getText().toString());
                        map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        ref.child(postId).setValue(map);
                        //progDial.dismiss();
                        startActivity(new Intent(Post.this, Home.class));
                        finish();
                    } else{
                        Toast.makeText(Post.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Post.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(Post.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Post.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Post.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    //ctrl+o
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();
            imgAdd.setImageURI((imgUri));
        } else {
            Toast.makeText(this, "Something went wrong!?", Toast.LENGTH_SHORT).show();
            startActivity(new Intent (Post.this, Home.class));
            finish();
        }
    }
}
