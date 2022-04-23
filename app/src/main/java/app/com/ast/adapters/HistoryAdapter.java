package app.com.ast.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.com.ast.R;
import app.com.ast.database.SpeedTest;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    List<SpeedTest> dataList;
    Activity context;

    public HistoryAdapter(Activity context, List<SpeedTest> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            holder.date.setText("Date");
            holder.ping.setText("Ping \n ms");
            holder.download.setText("Download \n Mbps");
            holder.upload.setText("Upload \n Mbps");
            return;
        }
        SpeedTest user = dataList.get(position - 1);
        if (user.pingTest.replace(" ms", "").equals("0")) {
            return;
        }
        holder.ping.setText(user.getPingTest().replace(" ms", ""));
        holder.download.setText(user.getDownloadSpeed());
        holder.upload.setText(user.getUploadSpeed());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        holder.date.setText(formattedDate);
//        holder.testedOn.setText("Tested On: " + user.getTestingMode());
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ping, upload, download, /*testedOn,*/
                date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ping = itemView.findViewById(R.id.ping);
            upload = itemView.findViewById(R.id.upload);
            download = itemView.findViewById(R.id.download);
            date = itemView.findViewById(R.id.date);
//            testedOn = itemView.findViewById(R.id.testedOn);

        }
    }
}
