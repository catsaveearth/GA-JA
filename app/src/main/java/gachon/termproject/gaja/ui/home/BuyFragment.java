package gachon.termproject.gaja.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import gachon.termproject.gaja.Info.PostInfo;
import gachon.termproject.gaja.R;
import gachon.termproject.gaja.adapter.postAdapter;

public class BuyFragment extends Fragment {
    //파이어베이스 선언
    private FirebaseFirestore firebaseFirestore;
    //레시피 글을 카드뷰로 띄워주기 위한 리사이클러 뷰 선언
    private RecyclerView buyRecyclerView;
    View rootView;

    public static BuyFragment newInstance(){
        BuyFragment BuyFragment=new BuyFragment();
        return BuyFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        firebaseFirestore= FirebaseFirestore.getInstance();

        //리사이클러뷰 작성
        buyRecyclerView = (RecyclerView)rootView.findViewById(R.id.buy_List);
        buyRecyclerView.setHasFixedSize(true);
        buyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //recipePost에 있는 data를 가져오기 위함.
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference
                //레시피 중 음료류 카테고리만 가져오기 위함.
                .whereEqualTo("category", "같이 사요")
                //추천 높은 순으로 정렬
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //각 게시글의 정보를 가져와 arrayList에 저장.
                            ArrayList<PostInfo> postList = new ArrayList<>();
                            PostInfo postInfo;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("로그: ", document.getId() + " => " + document.getData());
                                postInfo = new PostInfo(
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
                                long gap = postInfo.getFinishTime().getTime() - new Date().getTime();
                                if(gap < 0){
                                    //마감
                                }
                                else if(postInfo.getCurrentNumOfPeople() == postInfo.getPeopleNeed()){
                                    //인원 꽉참
                                }
                                else{
                                    postList.add(postInfo);
                                }
                            }

                            //recipeAdapter를 이용하여 리사이클러 뷰로 내용 띄움.
                            RecyclerView.Adapter mAdapter = new postAdapter(getActivity(), postList);
                            buyRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("로그: ", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }
}