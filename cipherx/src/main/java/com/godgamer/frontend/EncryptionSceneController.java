package com.godgamer.frontend;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EncryptionSceneController implements Initializable{

    @FXML
    private RadioButton darkRB;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;
    @FXML
    private ChoiceBox<String> algTypeCombo;
    @FXML
    private CheckBox verifiedAlgCB;
    @FXML
    private TextField inputTB, passwordShowTB, outputTB;
    @FXML
    private PasswordField passwordTB;
    @FXML
    private Button browseBtn, showBtn;

    private String selectedExt = null;

    // this will be used to store the algorithm type and its advanced options
    private class Algorithms 
    {
        public String algName;
        public Map<String, Node> algOptions = new HashMap<>(); // this will be used to store the advanced options for the algorithm
        public Algorithms(String algName) {
            this.algName = algName;
        }
        // public Algorithms addOption(String key, Node node) {
        //     algOptions.put(key, node);
        //     return this;
        // }
        public Algorithms addOptions(HashMap<String, Node> nodes) {
            algOptions.putAll(nodes);
            return this;
        }
    }

    // filter to allow only integer values in the text field
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // allows optional negative sign and digits
            return change;
        }
        return null;
    };
    // to store the algorithm type and its advanced options
    private final Algorithms[] verifiedAlgType = {
        new Algorithms("CBC").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("ECB").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("CFB").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("OFB").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("CTR").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("GCM").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("ChaCha20").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("Poly1305").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField(null);
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                put("Degree of Security", dos);
                put("Off Limits", new CheckBox());
            }
        }),
        new Algorithms("RSA").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField("1"), publicKeyTB = new TextField(null), publicKeyNameTB = new TextField(null);
                publicKeyNameTB.setPromptText("Enter Name of Key file");
                CheckBox offLimits = new CheckBox();
                publicKeyTB.setPromptText("Enter location of public key file...");
                Button generateKeysBtn = new Button("Generate Keys"), browse = new Button("Browse");
                generateKeysBtn.setOnAction(e -> {
                    generateKeys(publicKeyTB, publicKeyNameTB, dos, offLimits, true);
                });
                browse.setOnAction(e ->{
                    browsePublicKey(publicKeyTB, true);
                });
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                HBox publicKeyHbox = new HBox(10, publicKeyTB, browse), generateHbox = new HBox(10, publicKeyNameTB, generateKeysBtn);
                publicKeyHbox.setAlignment(Pos.CENTER_LEFT); // aligns children to the left
                put("Public Key", publicKeyHbox);
                put("Generate Keys (If not already done.)", generateHbox);
                put("Degree of Security", dos);
                put("Off Limits", offLimits);
            }
        }),
        new Algorithms("ECC").addOptions(new HashMap<String, Node>() {
            {
                TextField dos = new TextField("1"), publicKeyTB = new TextField(null), publicKeyNameTB = new TextField(null);
                publicKeyNameTB.setPromptText("Enter Name of Key file");
                CheckBox offLimits = new CheckBox();
                publicKeyTB.setPromptText("Enter location of public key file...");
                Button generateKeysBtn = new Button("Generate Keys"), browse = new Button("Browse");
                generateKeysBtn.setOnAction(e -> {
                    generateKeys(publicKeyTB, publicKeyNameTB, dos, offLimits, false);
                });
                browse.setOnAction(e -> {
                    browsePublicKey(publicKeyTB, false);
                });
                dos.setTextFormatter(new TextFormatter<>(integerFilter));
                HBox publicKeyHbox = new HBox(10, publicKeyTB, browse), generateHbox = new HBox(10, publicKeyNameTB, generateKeysBtn);
                publicKeyHbox.setAlignment(Pos.CENTER_LEFT); // aligns children to the left
                put("Public Key", publicKeyHbox);
                put("Generate Keys (If not already done.)", generateHbox);
                put("Degree of Security", dos);
                put("Off Limits", offLimits);
            }
        }),
    }, unverifiedAlgType = {
        // new Algorithms("Shift Value").addOptions(new HashMap<String, Node>() {
        //     {
        //         put("Label", new Label("Shift Value"));
        //         put("Shift Value", new TextField("0"));
        //     }
        // })
    };

    @SuppressWarnings("UseSpecificCatch")
    private void generateKeys(TextField publicKeyTB, TextField publicKeyNameTB, TextField dos, CheckBox offLimits, boolean isRSA) {
        if (passwordTB.getText().isEmpty()) {
            Logger.showWarning("Kindly enter a password to encrypt the file!", "Password not specified", "Password is empty!");
            return;
        }
        if (publicKeyNameTB.getText() == null || publicKeyNameTB.getText().isEmpty()) {
            Logger.showWarning("Kindly enter a name for the public key file!", "Public Key Name not specified", "Public Key Name is empty!");
            return;
        }
        // Generate RSA keys and set them to the text fields
        try {
            String publickeyName = publicKeyNameTB.getText() + "_public.key";
            String privatekeyName = publicKeyNameTB.getText() + "_private.key";
            if (isRSA)
            {
                App.rsa.generateKeys(Integer.parseInt(dos.getText()), offLimits.isSelected());
                App.rsa.KEY_SAVE_SECURE(publickeyName, privatekeyName, true, passwordTB.getText(), Integer.parseInt(dos.getText()), offLimits.isSelected());
            }
            else
            {
                App.ecc.generateKeys();//Integer.parseInt(dos.getText()), offLimits.isSelected()
                App.ecc.KEY_SAVE_SECURE(publickeyName, privatekeyName, passwordTB.getText(), Integer.parseInt(dos.getText()), offLimits.isSelected());
            }
            publicKeyTB.setText(System.getProperty("user.dir") + "\\ " + publickeyName);
            Logger.showInfo("Keys Stored in " + System.getProperty("user.dir"), "RSA Keys Successfully generated", "Keys Generated Successfully!");
        } catch (Exception e) {
            Logger.showError("An error occurred while generating RSA keys!", "RSA Key Generation Error", "Error: " + e.getMessage());
        }    
    }

    private void browsePublicKey(TextField publicKeyTB, boolean isRSA)
    {
        if (passwordTB.getText().isEmpty() && isRSA) {
            Logger.showWarning("Kindly enter a password to encrypt the file!", "Password not specified", "Password is empty!");
            return;
        }
        try{
            // Create a FileChooser instance
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a Public Key File");

            // Set filters (Optional: restrict file types)
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Key Files", "*.key")
            );

            // Open file dialog and get the selected file
            File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());

            // Check if a file was selected and print the path
            if (selectedFile != null) {
                Logger.printMessage("Selected public key file: " + selectedFile.getAbsolutePath());
                publicKeyTB.setText(selectedFile.getAbsolutePath());
                try {
                    if (isRSA)
                        App.rsa.KEY_LOAD_PUBLIC(selectedFile.getAbsolutePath(), true, passwordTB.getText());
                    else
                        App.ecc.KEY_LOAD_PUBLIC(selectedFile.getAbsolutePath());
                    Logger.showInfo("Public Key Loaded Successfully!", "Public Key Loaded", "Public Key Loaded Successfully!");
                } catch (Exception e) {
                    Logger.showError("An error occurred while loading the Public key!", "Public Key Load Error", "Error: " + e.getMessage());
                }    
            } else {
                Logger.printMessage("No public key file selected.");
            }
        } catch (Exception e) {
            Logger.showError("An error occurred while browsing the public key!", "Public Key Error", "Error: " + e.getMessage());
        }
    }

    private static Algorithms curAlg = null; // this will be used to store the current algorithm selected

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        browseBtn.setVisible(true);
        darkRB.setSelected(App.isDarkMode);

        // setting up algorithm type combo box
        for (Algorithms alg : verifiedAlgType) {
            algTypeCombo.getItems().add(alg.algName);
        }
        for (Algorithms alg : unverifiedAlgType) {
            algTypeCombo.getItems().add(alg.algName);
        }
        algTypeCombo.setValue(verifiedAlgType[0].algName);

        // setting up advanced options
        algTypeCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);
        scrollPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // setting up password fields
        passwordShowTB.setVisible(false); // Initially hidden
        // Make sure both fields have the same text
        passwordShowTB.textProperty().bindBidirectional(passwordTB.textProperty());
        ImageView showBtnImg = (ImageView)showBtn.getGraphic();
        showBtnImg.setImage(App.images.get("show pass")); // Set the image to show password icon
    }

    private void resetWindow()
    {
        inputTB.setText("");
        passwordTB.setText("");
        passwordShowTB.setText("");
        outputTB.setText("");
        verifiedAlgCB.setSelected(false);
        scrollContentBox.getChildren().clear();
        updateAdvancedOptions(null);
    }

    // accept ActionEvent e as parameter to get the source of the event such as stage, scene, etc.
    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    // changes dark or light mode
    public void changeMode()
    {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS(((App.isDarkMode) ? "EncDecDark" : "EncDecLight"));     
    }

    // called whenever the algorithm type is changed
    private void updateAdvancedOptions(ActionEvent event)
    {
        String myChoice = algTypeCombo.getValue();
        scrollContentBox.getChildren().clear();

        Algorithms selectedAlg = null;

        for (Algorithms alg : verifiedAlgType) {
            if (alg.algName.equals(myChoice)) {
                selectedAlg = alg;
                break;
            }
        }
        if (selectedAlg == null) {
            for (Algorithms alg : unverifiedAlgType) {
                if (alg.algName.equals(myChoice)) {
                    selectedAlg = alg;
                    break;
                }
            }
        }

        if (selectedAlg != null) {
            curAlg = selectedAlg; // Store the current algorithm
            for (Map.Entry<String, Node> entry : selectedAlg.algOptions.entrySet()) {
                String key = entry.getKey();
                Node valueNode = entry.getValue();

                // Create a row with key label and input node
                HBox row = new HBox(10); // spacing between label and input
                Label keyLabel = new Label(key);
                row.getChildren().addAll(keyLabel, valueNode);
                scrollContentBox.setSpacing(4); // adds vertical space between HBox rows
                row.setAlignment(Pos.CENTER_LEFT); // aligns children to the left
                keyLabel.setMinWidth(120); // set consistent width for labels
                scrollContentBox.getChildren().add(row);
            }
        }

        if (event != null)
            event.consume();
    }

    // To get the required element from the advanced options
    private  Node getOptionValue(Algorithms alg, String optionKey) 
    {    
        if (alg != null && alg.algOptions.containsKey(optionKey)) 
            return alg.algOptions.get(optionKey);

        return null;
    }

    // this method is used to show or hide the password field
    public void togglePasswordVisibility()
    {
        passwordShowTB.setVisible(!passwordShowTB.isVisible());
        passwordTB.setVisible(!passwordTB.isVisible());
        ImageView showBtnImg = (ImageView) showBtn.getGraphic();
        if (passwordShowTB.isVisible())
            showBtnImg.setImage(App.images.get("hide pass")); // Set the image to hide password icon
        else
            showBtnImg.setImage(App.images.get("show pass")); // Set the image to show password icon
    }

    public void getFilePath()
    {
        // Create a FileChooser instance
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Open file dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());

        // Check if a file was selected and print the path
        if (selectedFile != null) {
            selectedExt = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf("."));
            Logger.printMessage("Selected file: " + selectedFile.getAbsolutePath());
            inputTB.setText(selectedFile.getAbsolutePath());
        } else {
            Logger.printMessage("No file selected.");
        }
    }

    /**
     * * This method is used to get the output file path from the user using a FileChooser dialog.
     * * * It sets the selected file path to the outputTB TextField.
     * * * If the user cancels the operation, it prints a message to the console.
     */
    public void getOutputFilePath()
    {
        if(selectedExt == null && browseBtn.isVisible())
        {
            Logger.showWarning("Kindly select an input file first!", "Input File not specified", "Input is empty!");
            return;
        }
        // Create a FileChooser instance
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a file");

        // Set filters (Optional: restrict file types)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File", (browseBtn.isVisible())? "*" + selectedExt : "*.txt")
        );

        // Show the save file dialog
        File fileToSave = fileChooser.showSaveDialog(App.scene.getWindow());

        if (fileToSave != null) {
            Logger.printMessage("File to save: " + fileToSave.getAbsolutePath());
            outputTB.setText(fileToSave.getAbsolutePath());
        } else {
            Logger.printMessage("Save operation cancelled.");
        }
    }

    /**
     * * This method is used to change the algorithm list based on the verifiedAlgCB checkbox.
     * * If the checkbox is selected, it shows only the verified algorithms.
     * * If the checkbox is not selected, it shows all algorithms.
     * * It is called whenever the checkbox is selected or deselected.
     */
    public void changeAlgorithmList()
    {
        if(verifiedAlgCB.isSelected())
        {
            algTypeCombo.getItems().clear();
            for (Algorithms alg : verifiedAlgType) {
                algTypeCombo.getItems().add(alg.algName);
            }
        }
        else
        {
            algTypeCombo.getItems().clear();
            for (Algorithms alg : verifiedAlgType) {
                algTypeCombo.getItems().add(alg.algName);
            }
            for (Algorithms alg : unverifiedAlgType) {
                algTypeCombo.getItems().add(alg.algName);
            }        
        }
        algTypeCombo.setValue(verifiedAlgType[0].algName);
    }

    public void toggleBrowseBtn()
    {
        inputTB.setText("");
        browseBtn.setVisible(!browseBtn.isVisible());
        if (browseBtn.isVisible())
            inputTB.setPromptText("Browse for file input...");
        else
            inputTB.setPromptText("Enter the string to encrypt...");
    }

    public void execute()
    {
        byte[] inputData;
        String password = passwordTB.getText();

        if(inputTB.getText().isEmpty())
        {    
            Logger.showWarning("Kindly browse for an input file or enter the message to be encrypted!", "Input File not specified", "Input is empty!");
            return;
        }
        if(outputTB.getText().isEmpty())
        {     
            Logger.showWarning("Kindly specify the location of output file!", "Output File not specified", "Output is empty!");
            return;
        }
        if (browseBtn.isVisible())
        {
            File inputFile = new File(inputTB.getText());
            if (!inputFile.exists()) {
                Logger.showWarning("Kindly browse for an existing input file", "Input file does not exist", "Input file does not exist!");
                return;
            }
            if (!inputFile.canRead()) {
                Logger.showWarning("Kindly browse for a readable input file", "Input file is not readable", "Input file is not readable!");
                return;
            }
            // Read the file content into a byte array
            try {
                inputData = java.nio.file.Files.readAllBytes(inputFile.toPath());
                if (inputData.length == 0) {
                    Logger.showWarning("Kindly browse for a non-empty input file", "Input file is empty", "Input file is empty!");
                    return;
                }
                Logger.printMessage("Input file read successfully!");
            } catch (java.io.IOException e) {
                Logger.showError("An error encountered while reading input file!", "Input File Error", "Error reading input file: " + e.getMessage());
                return;
            }
        }
        else
        {
            String inputString = inputTB.getText();
            if (inputString.isEmpty()) {
                Logger.showWarning("Kindly enter a string to encrypt!", "Input String not specified", "Input string is empty!");
                return;
            }
            inputData = inputString.getBytes(); // Convert the string to bytes
        }
        if (curAlg.algName.equals("RSA"))
        {
            if (!App.rsa.isPublicKeyLoaded()) {
                Logger.showWarning("Kindly generate the keys first!", "Keys not generated", "Keys are not generated!");
                return;
            }       
        }
        else if (curAlg.algName.equals("ECC"))
        {
            if (!App.ecc.isPublicKeyLoaded()) {
                Logger.showWarning("Kindly generate the keys first!", "Keys not generated", "Keys are not generated!");
                return;
            }       
        }
        LocalTime now = LocalTime.now();
        System.out.println("Input: " + inputTB.getText());

        // Create progress dialog
        Stage progressDialog = new Stage();
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.initOwner(App.scene.getWindow());
        progressDialog.setTitle("Encryption in Progress");
        progressDialog.setResizable(false);
        // progressDialog.setAlwaysOnTop(true);
        VBox vbox = new VBox(10, new Label("Encrypting... Please wait."), new ProgressIndicator());
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(vbox, 300, 100);
        progressDialog.setScene(scene);

        // Create encryption task
        Task<byte[]> encryptionTask = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                // Perform encryption here
                switch (curAlg.algName) {
                    case "CBC":
                        if (password.isEmpty())
                            return App.aes.encrypt_CBC(inputData); // Ensure this method returns encrypted bytes
                        else
                        {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if(degreeOfSecurity != null && !degreeOfSecurity.isEmpty())
                                return App.aes.encrypt_CBC(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            else
                                return App.aes.encrypt_CBC(inputData, password);
                        }
                    case "ECB": 
                        if (password.isEmpty())
                            return App.aes.encrypt_ECB(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.aes.encrypt_ECB(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected()); 
                            }else {
                                return App.aes.encrypt_ECB(inputData, password);
                            }
                        }
                    case "CFB":
                        if (password.isEmpty())
                            return App.aes.encrypt_CFB(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.aes.encrypt_CFB(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.aes.encrypt_CFB(inputData, password);
                            }
                        }
                    case "OFB":
                        if (password.isEmpty())
                            return App.aes.encrypt_OFB(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.aes.encrypt_OFB(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.aes.encrypt_OFB(inputData, password);
                            }
                        }
                    case "CTR":
                        if (password.isEmpty())
                            return App.aes.encrypt_CTR(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.aes.encrypt_CTR(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.aes.encrypt_CTR(inputData, password);
                            }
                        }
                    case "GCM":
                        if (password.isEmpty())
                            return App.aes.encrypt_GCM(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.aes.encrypt_GCM(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.aes.encrypt_GCM(inputData, password);
                            }
                        }
                    case "ChaCha20":
                        if (password.isEmpty())
                            return App.chacha20.encrypt(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.chacha20.encrypt(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.chacha20.encrypt(inputData, password);
                            }
                        }
                    case "Poly1305":
                        if (password.isEmpty())
                            return App.chacha20.encrypt_poly1305(inputData); // Ensure this method returns encrypted bytes
                        else {
                            String degreeOfSecurity = ((TextField) getOptionValue(curAlg, "Degree of Security")).getText();
                            if (degreeOfSecurity != null && !degreeOfSecurity.isEmpty()) {
                                return App.chacha20.encrypt_poly1305(inputData, password, Integer.parseInt(degreeOfSecurity), ((CheckBox) getOptionValue(curAlg, "Off Limits")).isSelected());
                            } else {
                                return App.chacha20.encrypt_poly1305(inputData, password);
                            }
                        }
                    case "RSA":
                        return App.rsa.encrypt(inputData);
                    case "ECC":
                        return App.ecc.encrypt(inputData);
                    default:
                        throw new AssertionError(curAlg.algName + "not yet implemented");
                }
            }
        };

        // Handle task completion
        encryptionTask.setOnSucceeded(workerStateEvent -> {
            progressDialog.close();
            System.out.println(Duration.between(now, LocalTime.now()).toMillis() + " ms");
            byte[] encryptedData = encryptionTask.getValue();
            if (encryptedData != null) {  
                Logger.showInfo("Key file stored in " + System.getProperty("user.dir") + (App.aes.get_active_encrypted_filename() != null ? App.aes.get_active_encrypted_filename() : "") + "\nEncrypted File Stored in " + outputTB.getText(), "Encryption Success", "Encrypted datakey: " + App.aes.get_active_encrypted_filename());
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputTB.getText()))) {
                    bos.write(encryptedData);
                    bos.flush();
                    Logger.printMessage("File saved successfully!");
                    resetWindow();
                } catch (IOException e) {
                    Logger.showError("An error occurred while writing the output file!", "Output File Error", "Error: " + e.getMessage());
                }        
            }
        });

        encryptionTask.setOnFailed(workerStateEvent -> {
            progressDialog.close();
            Logger.showError("An error occurred during encryption!: " + encryptionTask.getException().getMessage(), "Encryption Error", "Error: " + encryptionTask.getException().getMessage());  
        });

        // Show dialog and start task
        progressDialog.show();
        new Thread(encryptionTask).start();

    }
}
