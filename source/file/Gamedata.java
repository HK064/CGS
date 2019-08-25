package source.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import source.CGPlayer;
import source.CGServer;
import source.display.PlayPanel;

public class Gamedata {
    private static List<Gamedata> gamedata = new ArrayList<>();
    private static final String NAME = "Name";
    private static final String SERVER_CLASS = "Server_Class";
    private static final String PLAYER_CLASS = "Player_Class";
    private static final String PANEL_CLASS = "Panel_Class";
    private static int gamedataSize = 0;
    private String folderName;
    private Properties property;

    static {
        Path dir = Paths.get("gamedata");
        try {
            for (Path path : Files.newDirectoryStream(dir)) {
                if(Files.isDirectory(path)){
                    Path configPath = Paths.get(path.toString(), path.getFileName() + ".config");
                    if(Files.exists(configPath)){
                        Properties property = new Properties();
                        try {
                            property.load(Files.newBufferedReader(configPath, StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new Gamedata(path.getFileName().toString(), property);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Gamedata(String folderName, Properties property){
        this.folderName = folderName;
        this.property = property;
        gamedataSize++;
        gamedata.add(this);
    }

    public String getFolderName(){
        return folderName;
    }

    public String getProperty(String key){
        return property.getProperty(key);
    }

    /**
     * コンボボックスでの表示用。
     */
    @Override
    public String toString() {
        return property.getProperty(NAME);
    }

    public CGServer newServerInstance(){
        Class<? extends CGServer> clazz;
        CGServer obj = null;
        try {
            clazz = Class.forName("gamedata." + folderName + "." + property.getProperty(SERVER_CLASS)).asSubclass(source.CGServer.class);
            try {
                obj = clazz.newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public CGPlayer newPlayerInstance(){
        Class<? extends CGPlayer> clazz;
        CGPlayer obj = null;
        try {
            clazz = Class.forName("gamedata." + folderName + "." + property.getProperty(PLAYER_CLASS)).asSubclass(source.CGPlayer.class);
            try {
                obj = clazz.newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public PlayPanel newPanelInstance(){
        Class<? extends PlayPanel> clazz;
        PlayPanel obj = null;
        try {
            clazz = Class.forName("gamedata." + folderName + "." + property.getProperty(PANEL_CLASS)).asSubclass(source.display.PlayPanel.class);
            try {
                obj = clazz.newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static List<Gamedata> getGamedataList(){
        return gamedata;
    }

    public static Gamedata getGamedata(String folderName){
        for(Gamedata gamedatum : gamedata){
            if(gamedatum.getFolderName().equals(folderName)){
                return gamedatum;
            }
        }
        return null;
    }

    /**
     * 読み込んだゲームデータの数を返す。
     * @return
     */
    public static int getGamedataSize(){
        return gamedataSize; 
    }

    
}
