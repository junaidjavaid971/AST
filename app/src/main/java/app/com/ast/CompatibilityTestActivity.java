package app.com.ast;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class CompatibilityTestActivity extends AppCompatActivity {

    TextView txtmodel, txtboard, txtbrand, txthardware, txtscreenSize, txtscreenResolution, txtscreenDimension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compatibility_test);
        initViews();
    }

    private void initViews() {
        txtmodel = findViewById(R.id.model_detail);
        txtboard = findViewById(R.id.board_detail);
        txtbrand = findViewById(R.id.brand_detail);
        txthardware = findViewById(R.id.hardware_detail);
        txtscreenSize = findViewById(R.id.screen_size_detail);
        txtscreenResolution = findViewById(R.id.screen_resolution_detail);
        txtscreenDimension = findViewById(R.id.screen_dimension_detail);

        txtmodel.setText(Build.MODEL);
        txtbrand.setText(Build.BRAND);
        txtboard.setText(Build.BOARD);
        txthardware.setText(Build.HARDWARE);
        int screenSize = getResources().getConfiguration().screenLayout;
        String[] screenData = getScreenDimension();
        txtscreenSize.setText(screenData[2]);
        txtscreenResolution.setText(screenData[0] + "x" + screenData[1]);
        txtscreenDimension.setText(screenData[3] + "In");
    }

    private String[] getScreenDimension() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        int densityDpi = (int) (dm.density * 160f);
        int widthInInches = (int) (width * 0.010416666666819);
        int heightInInches = (int) (height * 0.010416666666819);

        String[] screenInformation = new String[5];
        screenInformation[0] = String.valueOf(width) + "";
        screenInformation[1] = String.valueOf(height) + "";
        screenInformation[2] = String.format("%.2f", screenInches) + " inches";
        screenInformation[3] = String.valueOf(widthInInches + " x " + heightInInches) + "";
        screenInformation[4] = String.valueOf(densityDpi) + "";

        return screenInformation;
    }

    public int[] getMemorySizeInBytes() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);
        double availableMegs = memoryInfo.availMem / 0x100000L;

        double percentAvail = memoryInfo.availMem / (double) memoryInfo.totalMem * 100.0;

        int totalMemory = (int) (memoryInfo.totalMem / 1048576.0);
        int[] memory = new int[2];
        memory[0] = totalMemory;
        memory[1] = (int) availableMegs;
        return memory;
    }

    public String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long BlockSize = stat.getBlockSize();
        long TotalBlocks = stat.getBlockCount();
        return String.valueOf(((TotalBlocks * BlockSize) / 1048576));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return String.valueOf(((availableBlocks * blockSize) / 1048576));
    }
}