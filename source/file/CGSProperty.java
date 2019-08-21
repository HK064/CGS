package source.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CGSProperty {
    private static Properties property;
    private static Properties defaultProperty;
    private static Path path = Paths.get("./config/config.config");
    private static Path defaultPath = Paths.get("./config/default.config");
    public static final String mainWindowXPosKey = "Main_Window_X_Position";
    public static final String mainWindowYPosKey = "Main_Window_Y_Position";
    public static final String mainWindowWidthKey = "Main_Window_Width";
    public static final String mainWindowHeightKey = "Main_Window_Height";
    public static final String usernameKey = "Username";
    public static final String serverPortKey = "Server_Port";
    public static final String serverAddressKey = "Server_Address";

    static {
        defaultProperty = new Properties();
        if(Files.exists(defaultPath)) {
            try {
                defaultProperty.load(Files.newBufferedReader(defaultPath, StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO デフォルトプロパティファイルが無かった場合
        }

        property = new Properties(defaultProperty);
        if (Files.exists(path)) {
            try {
                property.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getValue(String key) {
        return property.getProperty(key);
    }

    public static void setProperty(String key, String value){
        property.setProperty(key, value);
    }

    public static void storeProperty() {
        try {
            property.store(Files.newBufferedWriter(path, StandardCharsets.UTF_8), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
