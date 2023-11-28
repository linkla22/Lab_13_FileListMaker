import javax.swing.JFileChooser;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

public class FileListMaker {
    static List<String> list = new ArrayList<>();
    static Scanner console = new Scanner(System.in);
    static Scanner inFile;
    static PrintWriter outFile;
    static String curFileName;
    static boolean running = true;
    static boolean hasAFile = false;
    static boolean newUnsavedFile = false;
    static boolean fileSaveFlag = false;

    public static void main(String[] args) {
        final String menuPrompt = "0 - Open list A - add item C - Clear the list D - Delete item V - View S - Save list Q - Quit";
        final String quitPrompt = "Are you sure you want to quit?";
        String rec = "";
        String userCmd = "";
        String quitYNResp = "";
        int userSelectedDex = -1;
        int convertedDex = -1;

        System.out.println("Welcome to ListMaker 0.1");
        System.out.println("========================");

        do {
            displayMenu();
            userCmd = SafeInput.getRegExString(console, menuPrompt, "[OoAaDdCcVvSsQq]");
            userCmd = userCmd.toUpperCase();

            try {
                switch (userCmd) {
                    case "0":
                        if (newUnsavedFile || fileSaveFlag) {
                            String prompt = "Are you sure? you have an unsaved file!\n Opening a new file will replace it and you will lose your data!";
                            boolean burnFileYN = SafeInput.getYNConfirm(console, prompt);

                            if (burnFileYN) {
                                openFile();
                            }
                        } else {
                            openFile();
                        }
                        break;

                    case "A":
                        if (!hasAFile) {
                            newUnsavedFile = true;
                        }
                        displaySelectorList();
                        rec = SafeInput.getNonZeroLengthString(console, "Enter your new list item: ");
                        list.add(rec);
                        System.out.println("Added record: " + rec);
                        fileSaveFlag = true;
                        break;

                    case "C":
                        if (!fileSaveFlag && !newUnsavedFile) {
                            list.clear();
                            hasAFile = newUnsavedFile = fileSaveFlag = false;
                        } else {
                            boolean clearFileYN = SafeInput.getYNConfirm(console, "Sure you want to clear this list without saving it?");
                            if (clearFileYN) {
                                list.clear();
                                hasAFile = newUnsavedFile = fileSaveFlag = false;
                            }
                        }
                        break;

                    case "D":
                        if (list.size() > 0) {
                            displaySelectorList();
                            userSelectedDex = SafeInput.getRangedInt(console, "\n"
                                    + "Enter the # for the item to delete", 1, list.size());
                            convertedDex = userSelectedDex - 1;
                            String itemDeleted = list.get(convertedDex);
                            list.remove(convertedDex);
                            fileSaveFlag = true;
                            System.out.println("Item deleted: " + itemDeleted);
                        } else {
                            System.out.println("No items in list to delete!");
                        }
                        break;

                    case "V":
                        displaySelectorList();
                        break;

                    case "S":
                        saveFile();
                        fileSaveFlag = false;
                        newUnsavedFile = false;
                        hasAFile = true;
                        break;

                    case "Q":
                        if (fileSaveFlag || newUnsavedFile) {
                            if (SafeInput.getYNConfirm(console, "Save your list before quitting? you will loose it!")) {
                                saveFile();
                                System.out.println("Thank you for running Listmaker!");
                                System.exit(0);
                            }
                        }
                        if (SafeInput.getYNConfirm(console, quitPrompt)) {
                            System.out.println("Thank you for running listmaker!");
                            System.exit(0);
                        }
                        break;

                    default:
                        break;
                }
            } catch (FileNotFoundException ex) {
                System.out.println("There was an error and the file could not be found");
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("There was a major IO error!\nExiting...");
                ex.printStackTrace();
                System.exit(1);
            }

        } while (running);
    }

    private static void displayMenu() {
        String menuBar = "++";
        displaySelectorList();
        System.out.println("");
        System.out.println(menuBar);
    }

    private static void displayList() {
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%-35s\n", list.get(i));
        }
    }

    private static void displaySelectorList() {
        if (list.size() == 0) {
            System.out.println("NO ITEMS! LIST IS EMPTY!");
        } else {
            for (int i = 1; i <= list.size(); i++) {
                System.out.printf("%3d\t%-35s\n", i, list.get(i - 1));
            }
        }
        if (hasAFile) {
            if (fileSaveFlag) {
                System.out.println("List File: " + curFileName + " (NOT SAVED!)");
            } else {
                System.out.println("List File: " + curFileName + " (saved.)");
            }
        }
    }

    private static void openFile() throws FileNotFoundException, IOException {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inFile = new Scanner(chooser.getSelectedFile());

            list.clear();
            while (inFile.hasNextLine()) {
                list.add(inFile.nextLine());
            }
            inFile.close();

            curFileName = chooser.getSelectedFile().getName();

            hasAFile = true;
            newUnsavedFile = false;
            fileSaveFlag = false;
            System.out.println("List file opened: " + curFileName);
        } else {
            System.out.println("Warning: you did not choose a file!\nContinue with Add if you want to create a new list.\nTry again if you want an existing file!");
        }
    }

    private static void saveFile() throws FileNotFoundException, IOException {
        if (!hasAFile) {
            curFileName = SafeInput.getNonZeroLengthString(console, "Enter name of file (.txt extension will be added): ");
        }

        outFile = new PrintWriter(new File(curFileName));
        for (String ln : list)
            outFile.println(ln);

        outFile.close();
        System.out.println("List saved to file: " + curFileName);
    }
}

