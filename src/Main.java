import gui.GUIMain;
import cli.CLIMain;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        // Ask user which version to run
        String[] options = {"GUI Version", "CLI Version", "Exit"};
        
        int choice = JOptionPane.showOptionDialog(
            null,
            "Welcome to Expense Tracker!\nWhich version would you like to use?",
            "Expense Tracker",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 0:
                // Run GUI version
                GUIMain.main(args);
                break;
            case 1:
                // Run CLI version
                CLIMain.main(args);
                break;
            case 2:
            default:
                System.exit(0);
        }
    }
}