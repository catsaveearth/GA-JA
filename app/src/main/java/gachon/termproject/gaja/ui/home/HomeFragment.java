package gachon.termproject.gaja.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import gachon.termproject.gaja.MainActivity;
import gachon.termproject.gaja.R;
import gachon.termproject.gaja.adapter.FragmentAdapter;
import gachon.termproject.gaja.searchResult.searchResultActivity;
import gachon.termproject.gaja.ui.writingPost.WritingPostActivity;

public class HomeFragment extends Fragment{
    private ViewPager vp;
    private SearchView mSearchView;

    MainActivity activity;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activity=(MainActivity)getActivity();

    }
    @Override
    public void onDetach(){
        super.onDetach();
        activity=null;
    }

    FragmentAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        adapter =  new FragmentAdapter(getChildFragmentManager(), 1);
        root.findViewById(R.id.writingPostBtn).setOnClickListener(onClickListener);
        vp=(ViewPager)root.findViewById(R.id.share_need_container);
        setupViewPager(vp);

        TabLayout tab=root.findViewById(R.id.tabLayout);
        tab.setupWithViewPager(vp);

        mSearchView=root.findViewById(R.id.searchView);
        mSearchView.setMaxWidth( Integer.MAX_VALUE );

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myStartActivity(searchResultActivity.class,query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return root;
    }

    //?????? ????????? ?????? ????????? ????????? ?????? ???????????? ???????????? ?????? ?????? ?????????
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writingPostBtn:
                    myStartActivity(WritingPostActivity.class);
                    break;
            }
        }
    };

    //activity??? ???????????? ?????? ??????.
    private void myStartActivity(Class c){
        Intent intent=new Intent(getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void myStartActivity(Class c, String query){
        Intent intent=new Intent(getActivity(), c);
        intent.putExtra("Query", query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void setupViewPager(ViewPager vp) {
        adapter.addFragment(new TotalFragment(), "??????");
        adapter.addFragment(new BuyFragment(), "?????? ??????");
        adapter.addFragment(new EatFragment(), "?????? ?????????");
        vp.setAdapter(adapter);
    }

}