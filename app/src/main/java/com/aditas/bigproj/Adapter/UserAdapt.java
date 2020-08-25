package com.aditas.bigproj.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aditas.bigproj.Model.User;
import com.aditas.bigproj.R;
import com.aditas.bigproj.fragment.AccFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapt extends RecyclerView.Adapter<UserAdapt.ViewHolder> {
    private Context mCon;
    private List<User> mUser;
    private FirebaseUser fUser;

    public UserAdapt(Context mCon, List<User> mUser){
        this.mCon = mCon;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viG, int i){
        View v = LayoutInflater.from(mCon).inflate(R.layout.user_item, viG, false);
        return new UserAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i){

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUser.get(i);

        holder.btnFlw.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUname());
        holder.fullname.setText(user.getFname());
        Glide.with(mCon).load(user.getImgurl()).into(holder.imgAcc);
        isFollowing(user.getId(), holder.btnFlw);

        if(user.getId().equals(fUser.getUid())){
            holder.btnFlw.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor etr = mCon.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                etr.putString("profileid", user.getId());
                etr.apply();

                ((FragmentActivity)mCon).getSupportFragmentManager().beginTransaction().replace(R.id.frag_con,
                        new AccFragment()).commit();
            }
        });

        holder.btnFlw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFlw.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(fUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(fUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView imgAcc;
        public Button btnFlw;

        public ViewHolder(@NonNull View v){
            super(v);

            username = itemView.findViewById(R.id.tvUname);
            fullname = itemView.findViewById(R.id.tvFname);
            imgAcc = itemView.findViewById(R.id.img_acc);
            btnFlw = itemView.findViewById(R.id.btn_fol);
        }
    }

    private void isFollowing(final String userid, final Button btn){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(fUser.getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    btn.setText("following");
                } else {
                    btn.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
