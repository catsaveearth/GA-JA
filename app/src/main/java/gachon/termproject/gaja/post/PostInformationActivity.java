package gachon.termproject.gaja.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gachon.termproject.gaja.Info.MemberInfo;
import gachon.termproject.gaja.Info.PostInfo;

import gachon.termproject.gaja.R;
import gachon.termproject.gaja.adapter.postAdapter;
import gachon.termproject.gaja.adapter.post_mypage_Adapter;
import gachon.termproject.gaja.listener.OnPostListener;
import gachon.termproject.gaja.login.RandomNumberGenerator;
import gachon.termproject.gaja.login.Seed;
import gachon.termproject.gaja.login.SendMessage;

import static gachon.termproject.gaja.Util.isStorageUrl;
import static gachon.termproject.gaja.Util.showToast;

public class PostInformationActivity extends AppCompatActivity {

    private final String TAG = "????????? ??????";
    //?????????????????? ??????
    private FirebaseFirestore firebaseFirestore;
    //??????????????? ??? ??????
    private RecyclerView another_Post;
    //????????? ?????? ???????????? ??????
    private PostInfo postInfo;
    //?????? ??????
    private Button reportBtn;
    //?????? ??????
    private Button enrollmentBtn;
    private LinearLayout doneLayout;
    private LinearLayout publisher_timeOutLayout;
    private LinearLayout another_timeOutLayout;
    private LinearLayout makingLinkLayout;

    private TextView link_maker;
    private TextView title_maker;

    private DocumentReference dr;

    private String cklink;
    private String seetitle;

    //???????????????????????? ?????? ?????? ?????????????????? ??????.
    FirebaseUser firebaseUser;
    //?????? ?????????
    String user;
    //????????? ?????????
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        doneLayout = findViewById(R.id.doneLayout);
        doneLayout.setVisibility(View.GONE);

        publisher_timeOutLayout = findViewById(R.id.timeOutLayout_publisher);
        publisher_timeOutLayout.setVisibility(View.GONE);

        another_timeOutLayout = findViewById(R.id.timeOutLayout_another);
        another_timeOutLayout.setVisibility(View.GONE);

        makingLinkLayout = findViewById(R.id.makingLinkLayout);
        makingLinkLayout.setVisibility(View.GONE);


        findViewById(R.id.goBackBtn_doneLayout).setOnClickListener(onClickListener);
        findViewById(R.id.goBackBtn_timeOut).setOnClickListener(onClickListener);
        findViewById(R.id.deleteBtn_timeOut).setOnClickListener(onClickListener);
        findViewById(R.id.goBackBtn_makingLinkLayout).setOnClickListener(onClickListener);
        findViewById(R.id.Declaration).setOnClickListener(onClickListener);

        link_maker = findViewById(R.id.openchat_link_textview_maker);
        title_maker = findViewById(R.id.seetitle_makinglink);

        link_maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cklink)));
            }
        });

    }

    //????????? ?????????????????? ??????????????? ??????????????????, ?????? ???????????? ??????????????? ?????? ???????????? ???????????? ????????? ?????? resume????????? ?????? ??????.
    @Override
    protected void onResume() {
        super.onResume();

        firebaseFirestore= FirebaseFirestore.getInstance();//?????????????????? ??????

        //????????? ?????? ????????? ????????? ?????????????????? ???????????? ??????.
        enrollmentBtn = findViewById(R.id.btn_enrollment);
        enrollmentBtn.setOnClickListener(onClickListener);
        findViewById(R.id.btn_report).setOnClickListener(onClickListener);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //?????????????????? ?????? ??????
        user = firebaseUser.getUid();
        enrollmentBtn.setVisibility(View.VISIBLE);
        //????????? data ?????????
        postInfo = (PostInfo) getIntent().getSerializableExtra("PostInfo");
        //????????? ?????? ??????
        ArrayList<String> participatingUser = postInfo.getParticipatingUserId();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        RandomNumberGenerator rng = new RandomNumberGenerator();
        manager.cancel((int) rng.MT19937_long(Seed.MakeSeed(postInfo.getPostId())));

        long gap = postInfo.getFinishTime().getTime() - new Date().getTime();
        if(gap < 0){
            if(user.equals(postInfo.getPublisher())){
                publisher_timeOutLayout.setVisibility(View.VISIBLE);
            }
            else{
                enrollmentBtn.setText("");
                enrollmentBtn.setVisibility(View.GONE);
                another_timeOutLayout.setVisibility(View.VISIBLE);
            }
        }

        //??????????????? ??? ??? ?????? => ??????
        if(postInfo.getPeopleNeed() == postInfo.getCurrentNumOfPeople()) {
            enrollmentBtn.setText("");
            enrollmentBtn.setVisibility(View.GONE);

            cklink = postInfo.getTalkLink();
            seetitle = postInfo.getTitle();

            title_maker.setText(seetitle + " ???????????? ?????????????????????.");

            if(user.equals(postInfo.getPublisher())){
                makingLinkLayout.setVisibility(View.VISIBLE);
            }
            else if(postInfo.getParticipatingUserId().contains(user)){
                makingLinkLayout.setVisibility(View.VISIBLE);
            }
            else{
                doneLayout.setVisibility(View.VISIBLE);
            }

        }
        else if(user.equals(postInfo.getPublisher())){
            enrollmentBtn.setText("");
            enrollmentBtn.setVisibility(View.GONE);
        }
        else if(participatingUser.contains(user))
        {
            enrollmentBtn.setText("?????? ??????");
        }
        //????????? ????????????
        else{
            enrollmentBtn.setText("??????");
        }


        //????????? ?????? ????????? ?????? ??????
        //??????
        String id = getIntent().getStringExtra("id");
        Log.d("??????: ", "" + getIntent().getStringExtra("id"));

        ImageView postInfoTitleImage = findViewById(R.id.post_image);

        String infoTitleImagePath = postInfo.getTitleImage();
        if(isStorageUrl(infoTitleImagePath)){
            Glide.with(this).load(infoTitleImagePath).override(1000).thumbnail(0.1f).into(postInfoTitleImage);
        }
        Log.d("??????","" + postInfo.getTitleImage());

        TextView postInfoTitle = findViewById(R.id.post_title);
        postInfoTitle.setText(postInfo.getTitle());
        Log.d("??????","" + postInfo.getTitle());

        TextView postPeople = findViewById(R.id.post_enrollment);
        postPeople.setText(Integer.toString((int) postInfo.getCurrentNumOfPeople()) + " / " + Integer.toString((int) postInfo.getPeopleNeed()));
        Log.d("??????","" + postInfo.getCurrentNumOfPeople() + " / " + postInfo.getPeopleNeed());

        TextView postCreatedAt = findViewById(R.id.post_uploadtime);
        postCreatedAt.setText(new SimpleDateFormat("MM-dd hh:mm ??????", Locale.KOREA).format(postInfo.getFinishTime()));
        Log.d("??????","" + postInfo.getCreatedAt());

        TextView postContent = findViewById(R.id.post_content);
        postContent.setText(postInfo.getContent());



        //???

        //?????? ??????????????? ????????? ?????? ??????
        //??????

        another_Post = findViewById(R.id.another_post);
        another_Post.setHasFixedSize(true);
        another_Post.setLayoutManager(new LinearLayoutManager(PostInformationActivity.this, LinearLayoutManager.HORIZONTAL, false));
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference
                .whereEqualTo("category", postInfo.getCategory())
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(4)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<PostInfo> postList = new ArrayList<>();
                            PostInfo anotherPostInfo;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("??????: ", document.getId() + " => " + document.getData());
                                anotherPostInfo = new PostInfo(
                                        document.getData().get("titleImage").toString(),
                                        document.getData().get("title").toString(),
                                        document.getData().get("content").toString(),
                                        document.getData().get("publisher").toString(),
                                        document.getData().get("userName").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        (Long) document.getData().get("peopleNeed"),
                                        (Long) document.getData().get("currentNumOfPeople"),
                                        document.getData().get("postId").toString(),
                                        (ArrayList<String>) document.getData().get("participatingUserId"),
                                        document.getData().get("category").toString(),
                                        new Date(document.getDate("finishTime").getTime()),
                                        document.getData().get("talkLink").toString()
                                );

                                if(!(anotherPostInfo.getPostId().equals(postInfo.getPostId()))){
                                    long gap = anotherPostInfo.getFinishTime().getTime() - new Date().getTime();
                                    if(gap < 0){
                                        //??????
                                    }
                                    else if(anotherPostInfo.getCurrentNumOfPeople() == anotherPostInfo.getPeopleNeed()){
                                        //?????? ??????
                                    }
                                    else{
                                        postList.add(anotherPostInfo);
                                    }
                                }
                            }

                            RecyclerView.Adapter mAdapter = new postAdapter(PostInformationActivity.this, postList);
                            another_Post.setAdapter(mAdapter);
                        } else {
                            Log.d("??????: ", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //???
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_enrollment:
                    ArrayList<String> newParticipatingUserId = new ArrayList<>();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //?????????????????? ?????? ??????
                    user = firebaseUser.getUid();
                    id = postInfo.getPostId();
                    firebaseFirestore.collection("users").document(user).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Log.d(TAG, "???????????? ??????");
                                    if (task.isSuccessful()) {
                                        ArrayList<String> participatingPost = new ArrayList<>();
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            MemberInfo memberInfo = new MemberInfo(
                                                    document.getData().get("id").toString(),
                                                    document.getData().get("nickName").toString(),
                                                    (ArrayList<String>) document.getData().get("participatingPost"),
                                                    document.getData().get("fcmtoken").toString(),
                                                    (ArrayList<String>) document.getData().get("alarmPost")
                                                    );
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            if(memberInfo.getParticipatingPost()==null){
                                                participatingPost.add(id);
                                                memberInfo.setParticipatingPost(participatingPost);
                                                if(user == null){
                                                    dr = firebaseFirestore.collection("users").document();

                                                }else{
                                                    dr =firebaseFirestore.collection("users").document(user);
                                                }
                                                Log.d(TAG, "?????? ????????? : " + user);
                                                dbUploader(dr, memberInfo, 0);
                                            }

                                            else if(memberInfo.getParticipatingPost().contains(id))
                                            {
                                                participatingPost = memberInfo.getParticipatingPost();
                                                participatingPost.remove(id);
                                                memberInfo.setParticipatingPost(participatingPost);
                                                if(user == null){
                                                    dr = firebaseFirestore.collection("users").document();

                                                }else{
                                                    dr =firebaseFirestore.collection("users").document(user);

                                                }
                                                Log.d(TAG, "?????? ????????? : " + user);
                                                dbUploader(dr, memberInfo, 1);
                                            }
                                            else{
                                                participatingPost = memberInfo.getParticipatingPost();
                                                participatingPost.add(id);
                                                memberInfo.setParticipatingPost(participatingPost);
                                                if(user == null){
                                                    dr = firebaseFirestore.collection("users").document();

                                                }else{
                                                    dr =firebaseFirestore.collection("users").document(user);

                                                }
                                                Log.d(TAG, "?????? ????????? : " + user);
                                                dbUploader(dr, memberInfo, 0);
                                            }
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                    if(postInfo.getParticipatingUserId().contains(user))
                    {
                        postInfo.setCurrentNumOfPeople(postInfo.getCurrentNumOfPeople() - 1);
                        newParticipatingUserId = postInfo.getParticipatingUserId();
                        newParticipatingUserId.remove(user);
                        postInfo.setParticipatingUserId(newParticipatingUserId);
                        if(id == null){
                            dr = firebaseFirestore.collection("posts").document();

                        }else{
                            dr =firebaseFirestore.collection("posts").document(id);

                        }
                        dbUploader(dr, postInfo, 0);
                        break;
                    }
                    else{
                        postInfo.setCurrentNumOfPeople(postInfo.getCurrentNumOfPeople() + 1);
                        newParticipatingUserId = postInfo.getParticipatingUserId();
                        newParticipatingUserId.add(user);
                        postInfo.setParticipatingUserId(newParticipatingUserId);
                        if(id == null){
                            dr = firebaseFirestore.collection("posts").document();

                        }else{
                            dr =firebaseFirestore.collection("posts").document(id);

                        }
                        dbUploader(dr, postInfo, 1);
                        break;
                    }

                case R.id.btn_report:
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostInformationActivity.this);
                    final EditText editText = new EditText(PostInformationActivity.this);
                    builder.setTitle("?????? ????????? ?????????????????? ");
                    builder.setView(editText);

                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    break;

                case R.id.goBackBtn_doneLayout:

                case R.id. goBackBtn_timeOut:

                case R.id.goBackBtn_makingLinkLayout:
                    finish();
                    break;

                case R.id. deleteBtn_timeOut:
                    onPostListener.onDelete(postInfo);
                    finish();
                    break;

                case R.id.Declaration:
                    //????????? ?????????
                    break;

            }
        }
    };


    OnPostListener onPostListener = new OnPostListener() {

        @Override
        public void onDelete(PostInfo postInfo) {
            ArrayList<String> enrollmentUser = postInfo.getParticipatingUserId();

            for(int i = 0 ; i < enrollmentUser.size() ; i ++){
                String userid = enrollmentUser.get(i);
                firebaseFirestore.collection("users").document(userid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d(TAG, "???????????? ??????");
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        MemberInfo memberInfo = new MemberInfo(
                                                document.getData().get("id").toString(),
                                                document.getData().get("nickName").toString(),
                                                (ArrayList<String>) document.getData().get("participatingPost"),
                                                document.getData().get("fcmtoken").toString(),
                                                (ArrayList<String>) document.getData().get("alarmPost")
                                        );
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        //recipepostinfo ???????????? ??????.
                                        ArrayList<String> participatingPost = memberInfo.getParticipatingPost();
                                        participatingPost.remove(postInfo.getPostId());

                                        firebaseFirestore.collection("users").document(userid)
                                                .update("participatingPost", participatingPost)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
            }


            Log.d(TAG, "??????" + id);
            firebaseFirestore.collection("posts").document(postInfo.getPostId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            publisher_timeOutLayout.setVisibility(View.GONE);
                            finish();
                            showToast(PostInformationActivity.this ,"????????? ????????? ???????????????!");
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(PostInformationActivity.this ,"????????? ????????? ???????????????!");
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    };

    //??????, ?????? ????????? ???????????? ????????????????????? ??????????????? ????????????.(?????????)
    private void dbUploader(DocumentReference documentReference , PostInfo postInfo, int requestCode) {
        //???????????? ?????? ??????
        if (requestCode == 0) {
            documentReference.set(postInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(PostInformationActivity.this, "????????? ???????????????!");
                            Log.w(TAG, "Success writing document" + documentReference.getId());
                            onResume();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(PostInformationActivity.this, "??????????????? ???????????????!");
                    Log.w(TAG, "Error writing document", e);
                }
            });
        }
        //????????? ??????
        else {
            documentReference.set(postInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(PostInformationActivity.this, "???????????? ???????????????!");
                            Log.w(TAG, "Success writing document" + documentReference.getId());
                            if(postInfo.getCurrentNumOfPeople()==postInfo.getPeopleNeed()){
                                SendMessage sendMessage = new SendMessage();
                                sendMessage.SendFull(postInfo);
                            }
                            onResume();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(PostInformationActivity.this, "????????? ????????? ???????????????!");
                    Log.w(TAG, "Error writing document", e);
                }
            });
        }
    }

        //??????, ?????? ????????? ???????????? ????????????????????? ??????????????? ????????????.(??????)
    private void dbUploader(DocumentReference documentReference , MemberInfo memberInfo, int requestCode){
            //???????????? ?????? ??????
            if(requestCode == 0){
                documentReference.set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(PostInformationActivity.this ,"????????? ???????????????!");
                                Log.w(TAG,"Success writing document" + documentReference.getId());
                                onResume();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(PostInformationActivity.this ,"??????????????? ???????????????!");
                        Log.w(TAG,"Error writing document", e);
                    }
                });
            }
            //????????? ??????
            else{
                documentReference.set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(PostInformationActivity.this ,"???????????? ???????????????!");
                                Log.w(TAG,"Success writing document" + documentReference.getId());
                                onResume();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(PostInformationActivity.this ,"????????? ????????? ???????????????!");
                        Log.w(TAG,"Error writing document", e);
                    }
                });
            }

    }
}