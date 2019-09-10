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

    public static final String EXTRA_MESSAGE = "org.tsofen.ourstory.extra.MESSAGE";
    AppHomePage parent;
    RecyclerView rv;
    ArrayList<MemoryA> memories;
    OurStoryService MemoryAService;
    MyMemoriesAdapter adapter;
    TextView storyName;
    Long user_id;
    public MyMemories() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        parent = (AppHomePage) getActivity();
        return inflater.inflate(R.layout.fragment_my_memories, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity a = getActivity();
        Intent i = a.getIntent();
        user_id =  i.getLongExtra("userId",0);

       Toast.makeText(getActivity(),user_id+"h",Toast.LENGTH_LONG).show();
        rv = view.findViewById(R.id.recycler);
        MemoryAService = WebFactory.getService();
        MemoryAService.GetMemoriesByUser(user_id).enqueue(new Callback<ArrayList<MemoryA>>() {
            @Override
            public void onResponse(Call<ArrayList<MemoryA>> call, Response<ArrayList<MemoryA>> response) {
                memories = response.body();
                adapter = new MyMemoriesAdapter(memories);
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onFailure(Call<ArrayList<MemoryA>> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });

      /*  final Button sharebtn = view.findViewById(R.id.sharebtn);
       sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mimeType = "text/plain"; // For the share func to know which type is the sharing
                // content so it can offer the right apps
                ShareCompat.IntentBuilder
                        .from(getActivity())
                        .setType(mimeType)
                        .setChooserTitle("Share this MemoryA with: ")
                        .setText("This is a filler until we can integrate a MemoryA object")
                        .startChooser();
            }
        });*/
    }

}
