import source.display.MainWindow;

public class Main{
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        Thread thread = new Thread(mainWindow);
        thread.start();
    }
}
