package app.com.ast.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SpeedTestDao {
    @Insert(onConflict = REPLACE)
    void insert(SpeedTest speedTest);

    @Query("SELECT * FROM speedTestTable")
    List<SpeedTest> getAll();
}
