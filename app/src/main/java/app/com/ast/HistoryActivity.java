package app.com.ast;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import app.com.ast.adapters.HistoryAdapter;
import app.com.ast.database.RoomDB;
import app.com.ast.database.SpeedTest;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<SpeedTest> dataList = new ArrayList<>();
    RoomDB database;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LottieAnimationView animationView = findViewById(R.id.animation);

        recyclerView = findViewById(R.id.recyclerView);
        database = RoomDB.getInstance(this);
        dataList = database.mainDao().getAll();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (dataList.isEmpty()) {
            animationView.setVisibility(View.VISIBLE);
        } else {
            adapter = new HistoryAdapter(HistoryActivity.this, dataList);
            recyclerView.setAdapter(adapter);
        }
    }
}