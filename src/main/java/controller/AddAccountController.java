package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.AddAccountModel;

/**
 * Controller for the add account form
 */
public class AddAccountController extends SceneController {
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField confirmField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField surnameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private CheckBox adminBox;

    private final AddAccountModel model = new AddAccountModel();

    private boolean firstAccount = false;

    public Button submitButton;

    @Override
    public void refresh() {
        if (model.noAccounts()){
            firstAccount = true;
            adminMenuBar.setVisible(false);
            normalMenuBar.setVisible(false);
            //First account must be an administrator
            adminBox.setSelected(true);
            adminBox.setDisable(true);
        }
        else {
            firstAccount = false;
            displayMenuBar();
        }
        resetErrorLabels();
    }

    @FXML
    public void submit() {
        displayErrors();
        if (error()){
            return;
        }
        model.addAccount(firstNameField.getText(), surnameField.getText(), emailField.getText(),
                passwordField.getText(), adminBox.isSelected());
        if (firstAccount) {
            logout();
        }
        else {
            goToAccounts();
        }
    }

    private void displayErrors() {
        resetErrorLabels();
        if (firstNameField.getText().isEmpty()){
            firstNameLabel.setText("First name required");
        }
        if (surnameField.getText().isEmpty()){
            surnameLabel.setText("Surname required");
        }
        if (emailField.getText().isEmpty()){
            emailLabel.setText("Email required");
        }
        else if (!model.isEmail(emailField.getText())){
            emailLabel.setText("Email is not a valid format");
        }
        else if (model.emailExists(emailField.getText())){
            emailLabel.setText("Already an account with this email");
        }
        if (passwordField.getText().isEmpty()){
            passwordLabel.setText("Password required");
        }
        else if (passwordField.getText().length() < 6){
            passwordLabel.setText("Password must be at least 6 characters");
        }
        else if (!passwordField.getText().equals(confirmField.getText())){
            passwordLabel.setText("Passwords do not match");
        }
    }

    private boolean error() {
        if (!firstNameLabel.getText().equals("")) {
            return true;
        }
        if (!surnameLabel.getText().equals("")) {
            return true;
        }
        if (!emailLabel.getText().equals("")) {
            return true;
        }
        return !passwordLabel.getText().equals("");
    }


    private void resetErrorLabels(){
        firstNameLabel.setText("");
        surnameLabel.setText("");
        emailLabel.setText("");
        passwordLabel.setText("");
    }
}
