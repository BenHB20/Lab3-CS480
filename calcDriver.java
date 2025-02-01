import javax.swing.*;

public class calcDriver {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Start the application with the login GUI
            Mainpage calcPage = new Mainpage();
            calcPage.buildGuiPanel();
        });
    }
}

