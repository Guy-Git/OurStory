package org.tsofen.ourstory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tsofen.ourstory.UserModel.AppHomePage;
import org.tsofen.ourstory.model.api.MemoryA;
import org.tsofen.ourstory.web.OurStoryService;
import org.tsofen.ourstory.web.WebFactory;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyMemories extends Fragment {
    private static final String LOG_TAG = CommentActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "org.tsofen.ourstory.extra.MESSAGE";
    AppHomePage parent;
    RecyclerView rv;
    Long user_id;
    ArrayList<MemoryA> memories;
    OurStoryService MemoryAService;
    MyMemoriesAdapter adapter;
    TextView storyName;
    public MyMemories() {
        super();

    }

    @Nullable
    @Override
    public View onCreateView( @Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        parent = (AppHomePage) getActivity();
        return inflater.inflate(R.layout.fragment_my_memories, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity a = getActivity();
        Intent i = a.getIntent();
        user_id=i.getLongExtra("userId",0);
        rv = view.findViewById(R.id.recycler);
        MemoryAService = WebFactory.getService();
        MemoryAService.GetMemoriesByUser(user_id).enqueue(new Callback<ArrayList<MemoryA>>() {
            @Override
            public void onResponse(Call<ArrayList<MemoryA>> call, Response<ArrayList<MemoryA>> response) {
                memories = response.body();
                Toast.makeText(getActivity(), "Fadi", Toast.LENGTH_LONG).show();
                adapter = new MyMemoriesAdapter(getActivity(),memories);
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ArrayList<MemoryA>> call, Throwable t) {
                Log.d("Error", t.toString());
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
            }
        });




    }

}
