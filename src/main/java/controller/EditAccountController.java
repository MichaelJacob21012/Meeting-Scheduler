package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import structure.Account;
import connect.AccountConnection;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EditAccountController extends SceneController{
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
    @FXML
    private CheckBox adminBox;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label firstNameErrorLabel;
    @FXML
    private Label surnameErrorLabel;
    private Account account;

    @Override
    public void refresh() {
        displayMenuBar();
        account = AccountConnection.findAccount((Integer)data.get("Edit AccountID"));
        firstNameField.setText(account.getFirstName());
        surnameField.setText(account.getSurname());
        emailField.setText(account.getEmail());
        adminBox.setSelected(account.isAdmin());
        if (isMyAccount()){
            adminBox.setDisable(true);
        }
        else {
            adminBox.setDisable(false);
        }
    }

    private boolean isMyAccount() {
        return account.equals(data.get("account"));
    }

    public void submit(MouseEvent mouseEvent) {
        displayErrors();
        if (error()){
            return;
        }
        account.setFirstName(firstNameField.getText());
        account.setSurname(surnameField.getText());
        account.setEmail(emailField.getText());
        account.setAdmin(adminBox.isSelected());
        if(isMyAccount()){
            data.put("account", account);
            AccountConnection.editAccount(account);
            refresh();
        }
        else {
            AccountConnection.editAccount(account);
            goToAccounts();
        }
    }

    private void displayErrors() {
        resetErrorLabels();
        if (firstNameField.getText().isEmpty()){
            firstNameErrorLabel.setText("First name required");
        }
        if (surnameField.getText().isEmpty()){
            surnameErrorLabel.setText("Surname required");
        }
        if (emailField.getText().isEmpty()){
            emailErrorLabel.setText("Email required");
        }
        else if (!isEmail(emailField.getText())){
            emailErrorLabel.setText("Email is not a valid format");
        }
    }
    private boolean error() {
        if (!firstNameErrorLabel.getText().equals("")) {
            return true;
        }
        if (!surnameErrorLabel.getText().equals("")) {
            return true;
        }
        return !emailErrorLabel.getText().equals("");
    }

    private boolean isEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    private void resetErrorLabels(){
        firstNameErrorLabel.setText("");
        surnameErrorLabel.setText("");
        emailErrorLabel.setText("");
    }

}
