import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.database.Subject;
import com.gabrielezanelli.schoolendar.database.Task;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * OrmliteConfigManager is a separate program from the actual android app,
 * that is used to generate the database structure configuration before runtime.
 * It uses the models provided via a list of class objects,
 * and also the annotations (e.g. @DatabaseField) on the models to generate the configuration accordingly.
 */
public class OrmliteConfigManager extends OrmLiteConfigUtil {

    /**
     * classes represents the models to use for generating the ormlite_config.txt file
     */
    private static final Class<?>[] classes = new Class[] {Event.class,Subject.class,Task.class};

    /**
     * Given that this is a separate program from the android app, we have to use
     * a static main java method to create the configuration file.
     * @param args
     * @throws IOException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {

        String currDirectory = "user.dir";
        String configPath = "/app/src/main/res/raw/ormlite_config.txt";

        /** Gets the project root director */
        String projectRoot = System.getProperty(currDirectory);

        /** Full configuration path made by root project path and config file path appended to it. */
        String fullConfigPath = projectRoot + configPath;
        File configFile = new File(fullConfigPath);

        /** Create the configuration file if doesn't exist. */
        if(configFile.exists()) {
            configFile.delete();
            configFile = new File(fullConfigPath);
        }

        /** Write the necessary configuration to the file. */
        writeConfigFile(configFile, classes);
    }
}