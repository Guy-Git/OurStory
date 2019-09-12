package org.tsofen.ourstory.EditCreateMemory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.tsofen.ourstory.FirebaseImageWrapper;
import org.tsofen.ourstory.R;
import org.tsofen.ourstory.model.Feeling;
import org.tsofen.ourstory.model.Memory;
import org.tsofen.ourstory.model.Picture;
import org.tsofen.ourstory.model.api.Story;
import org.tsofen.ourstory.model.api.User;
import org.tsofen.ourstory.web.OurStoryService;
import org.tsofen.ourstory.web.WebFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateEditMemoryActivity extends AppCompatActivity implements View.OnClickListener {

    boolean dateFlag = false;
    AddMemoryImageAdapter imageAdapter;
    AddMemoryVideoAdapter videoAdapter;
    AddMemoryTagAdapter tagAdapter;
    Feeling SelectedEmoji;
    String currentDate;
    Date MemDate = new Date();
    Date BirthDate = new Date();
    Date DeathDate = new Date();
    Calendar cal = Calendar.getInstance();
    Date today = cal.getTime();
    private EditText editTextDescription;
    private EditText editTextLocation;
    private ImageButton smileb;
    private ImageButton sadb;
    private ImageButton loveb;
    private Button svbtn;
    private Button cnslbtn;
    private EditText DescriptionText;
    private EditText locationText;
    public static final String KEY_EDIT = "CEMemoryEdit";
    public static final String KEY_CREATE = "CEMemoryCreate";
    public static final String KEY_MEMID = "CEMemoryMemoryID";
    public static final String KEY_USER = "CEMemoryUser";
    private Memory memory;
    private boolean create = true;
    private TextView MemError;
    private LinearLayout imageLiner;
    private ScrollView ourScroller;
    private User user;
    TextView AddPicTxV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_memory);

        imageAdapter = new AddMemoryImageAdapter(this);
        videoAdapter = new AddMemoryVideoAdapter(this);
        RecyclerView tagsRV = findViewById(R.id.tagsLayout_cememory);
        tagAdapter = new AddMemoryTagAdapter(new LinkedList<>(), tagsRV);

        Intent intent = getIntent();
        memory = (Memory) intent.getSerializableExtra(KEY_EDIT);
        TextView pageTitle = findViewById(R.id.text_cememory);
        editTextDescription = findViewById(R.id.memDescription_cememory);
        editTextLocation = findViewById(R.id.memLocation_cememory);
        smileb = findViewById(R.id.smilebtn_cememory);
        sadb = findViewById(R.id.sadbtn_cememory);
        loveb = findViewById(R.id.lovebtn_cememory);
        svbtn = findViewById(R.id.Savebtn_cememory);
        cnslbtn = findViewById(R.id.Cancelbtn_cememory);
        TextView dayDate = findViewById(R.id.day_text_cememory);
        TextView monthDate = findViewById(R.id.month_text_cememory);
        TextView yearDate = findViewById(R.id.year_text_cememory);
        MemError = findViewById(R.id.error_cememory);
        imageLiner = findViewById(R.id.LinerForImage);
        ourScroller = findViewById(R.id.scrollView_cememory);
        AddPicTxV = findViewById(R.id.AddPicTV_cememory);
        if (memory == null) {
            pageTitle.setText("Add Memory");
            memory = new Memory();
            user = (User) intent.getSerializableExtra(KEY_USER);
        } else {
            create = false;
            pageTitle.setText("Edit Memory");
            user = memory.getUser();
            editTextDescription.setText(memory.getDescription());
            editTextLocation.setText(memory.getLocation());
            if (memory.getMemoryDate() != null) {
                dayDate.setText(memory.getMemoryDate().get(Calendar.DAY_OF_MONTH));
                monthDate.setText(memory.getMemoryDate().get(Calendar.DAY_OF_MONTH));
                yearDate.setText(memory.getMemoryDate().get(Calendar.YEAR));
            }

            if (memory.getFeeling() != null)
                selectEmoji(memory.getFeeling());

            List<String> uris = new ArrayList<>();
            if (memory.getPictures() != null) {
                for (Picture p : memory.getPictures()) {
                    uris.add(p.getLink());
                    ++imageAdapter.upload_start;
                }
            }

            imageAdapter.data.addAll(uris);
            imageAdapter.notifyDataSetChanged();
            videoAdapter.data.addAll(memory.getVideos());
            videoAdapter.notifyDataSetChanged();
            tagAdapter.tags.addAll(memory.getTags());
            tagAdapter.notifyDataSetChanged();
        }

        Story story = (Story) intent.getSerializableExtra(KEY_CREATE);
        if (story != null) {
            memory.setStory(story);
        }

        smileb.setOnClickListener(this);
        sadb.setOnClickListener(this);
        loveb.setOnClickListener(this);

        svbtn.setOnClickListener(this);
        cnslbtn.setOnClickListener(this);

        RecyclerView rvp = findViewById(R.id.add_pictures_rv_cememory);

        rvp.setAdapter(imageAdapter);
        rvp.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));

        RecyclerView rvv = findViewById(R.id.add_videos_rv_cememory);

        rvv.setAdapter(videoAdapter);
        rvv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));

        //   editTextDescription.addTextChangedListener(SaveTextWatcher);
        // editTextLocation.addTextChangedListener(SaveTextWatcher);


        tagsRV.setAdapter(tagAdapter);
        tagsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smilebtn_cememory:
                SelectedEmoji = Feeling.HAPPY;
                selectEmoji(SelectedEmoji);

                break;
            case R.id.sadbtn_cememory:
                SelectedEmoji = Feeling.SAD;
                selectEmoji(SelectedEmoji);
                break;

            case R.id.lovebtn_cememory:
                SelectedEmoji = Feeling.LOVE;
                selectEmoji(SelectedEmoji);
                break;

            case R.id.Savebtn_cememory:
                if (CheckValidation(v)) {
                    // imageLiner.setBackground(getResources().getDrawable(R.drawable.error_image_background));
                    //  imageLiner.removeAllViews();
                    this.svbtn.setEnabled(true);
                    saveMemory(v);
                } else {

                    displayToast("Error , Please try filling out the fields again");
                }
                break;
            case R.id.Cancelbtn_cememory:
                ShowAlertDialog(this, "", "Are you sure you want to cancel ?");
                // finish();
                break;
            case R.id.back_button_cememory:
                ShowAlertDialog(this, "", "Are you sure you want to leave ?");
                // finish();
                break;

        }
    }

    public void ShowAlertDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * AlertDialog.Builder builder = new AlertDialog.Builder(this);
     * builder.setMessage("Are you sure you want to exit?")
     * .setCancelable(false)
     * .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
     * public void onClick(DialogInterface dialog, int id) {
     * MyActivity.this.finish();
     * }
     * })
     * .setNegativeButton("No", new DialogInterface.OnClickListener() {
     * public void onClick(DialogInterface dialog, int id) {
     * dialog.cancel();
     * }
     * });
     * AlertDialog alert = builder.create();
     * alert.show();
     **/
    public boolean CheckValidation(View v) {        //(Memory m) {
        if ((editTextDescription.getText().toString().equals("")) && (imageAdapter.data.isEmpty()) && (videoAdapter.data.isEmpty())) {
            MemError.setText("Enter at Least one of The above!");
            MemError.setVisibility(View.VISIBLE);
            ourScroller.fullScroll(ScrollView.FOCUS_UP);// .fullScroll(ScrollView.FOCUS_UP);
            //return false;
            // GradientDrawable gradientDrawable=new GradientDrawable();
            //gradientDrawable.setStroke(4,getResources().getColor(R.color.colorError));
            //Drawable d = getResources().getDrawable(R.drawable.error_image_background);
            //imageLiner.setBackground(gradientDrawable);
            // imageLiner.setBackground(getResources().getDrawable(R.drawable.error_image_background));
            return false;
        }
        /**displayToast("You should either enter an image or a video or description for your memory!");
         return false;
         }
         }
         /* if (today.before(MemDate)) {
         displayToast("You have selected invalid date , please choose valid date again ");
         return false;
         }           RecycleImage.setBackground(d);*/
        //  editTextDescription.setHintTextColor(@);

        /**   if (MemDate.before(BirthDate)) {
         displayToast("You have selected invalid date ,Memory can't occur before birth date, please choose valid date again ");
         return false;
         }
         if (MemDate.after(DeathDate)) {
         displayToast("You have selected invalid date ,Memory can't occur after Death date, please choose valid date again ");
         return false;
         } else
         dateFlag = true;*/
        return true;
    }


    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == AddMemoryImageAdapter.ADDMEMORY_IMAGE) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri currentUri = data.getClipData().getItemAt(i).getUri();
                    imageAdapter.data.add(currentUri.toString());
                    // imageLiner.removeView(getResources().getDrawable(R.drawable.error_image_background));

                    /****/

                }
            } else if (data.getData() != null) {
                imageAdapter.data.add(data.getData().toString());


            }
            imageAdapter.notifyDataSetChanged();
        } else if (requestCode == AddMemoryVideoAdapter.ADDMEMORY_VIDEO) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri currentUri = data.getClipData().getItemAt(i).getUri();
                    videoAdapter.data.add(currentUri.toString());
                }
            } else if (data.getData() != null) {
                videoAdapter.data.add(data.getData().toString());
            }
            videoAdapter.notifyDataSetChanged();
        }
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentCEMemory();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {

        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
//

        currentDate = day_string + "/" + month_string + "/" + year_string;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        MemDate = c.getTime();

        TextView dayDate = findViewById(R.id.day_text_cememory);
        TextView monthDate = findViewById(R.id.month_text_cememory);
        TextView yearDate = findViewById(R.id.year_text_cememory);

        dayDate.setText(day_string);
        monthDate.setText(month_string);
        yearDate.setText(year_string);
    }

    public void closeActivity(View view) {
        finish();
    }


    public void saveMemory(View view) {
        locationText = findViewById(R.id.memLocation_cememory);
        memory.setLocation(locationText.getText().toString());

        memory.setDescription(editTextDescription.getText().toString());
        memory.setFeeling(SelectedEmoji);
        memory.setMemoryDate(MemDate);
        displayToast("Data saved.");
        OurStoryService service = WebFactory.getService();
        Intent intent = new Intent();

        FirebaseImageWrapper wrapper = new FirebaseImageWrapper();
        List<StorageTask<UploadTask.TaskSnapshot>> tasks = new LinkedList<>();
        for (int i = imageAdapter.upload_start; i < imageAdapter.data.size(); i++) {
            String uri = imageAdapter.data.get(i);
            int finalI = i;
            tasks.add(wrapper.uploadImg(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
                imageAdapter.data.set(finalI, taskSnapshot.getDownloadUrl().toString());
            }));
        }


        Tasks.whenAll(tasks).addOnSuccessListener(aVoid -> {
            ArrayList<String> pictures = new ArrayList<>();
            pictures.addAll(imageAdapter.data);
            memory.setPictures(pictures);
            if (create) {
                service.CreateMemory(memory).enqueue(new Callback<Memory>() {
                    @Override
                    public void onResponse(Call<Memory> call, Response<Memory> response) {
                        if (response.code() != 200) {
                            displayToast("Error " + response.code() + " : " + response.message());
                            return;
                        }
                        Memory responseMem = response.body();
                        long memId = responseMem.getId();
                        intent.putExtra(KEY_MEMID, memId);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Memory> call, Throwable t) {

                    }
                });
            } else {
                service.EditMemory(memory).enqueue(new Callback<Memory>() {
                    @Override
                    public void onResponse(Call<Memory> call, Response<Memory> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Memory> call, Throwable t) {

                    }
                });
            }
        });


    }

    public void selectEmoji(Feeling selected) {
        switch (selected) {
            case HAPPY:
                findViewById(R.id.smiley_back2).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back3).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back1).setVisibility(View.VISIBLE);
                break;

            case SAD:
                findViewById(R.id.smiley_back1).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back3).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back2).setVisibility(View.VISIBLE);
                break;

            case LOVE:
                findViewById(R.id.smiley_back1).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back2).setVisibility(View.INVISIBLE);
                findViewById(R.id.smiley_back3).setVisibility(View.VISIBLE);
                break;
        }
    }
}


