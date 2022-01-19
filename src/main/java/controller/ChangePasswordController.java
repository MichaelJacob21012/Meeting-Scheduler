package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import model.ChangePasswordModel;

public class ChangePasswordController extends SceneController{
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private MenuBar normalMenuBar;
    @FXML
    private MenuBar adminMenuBar;
    @FXML
    private Label errorLabel;

    private final ChangePasswordModel model = new ChangePasswordModel();
    private int accountID;

    @Override
    public void refresh() {
        adminMenuBar.setVisible(false);
        normalMenuBar.setVisible(false);
        accountID = (Integer)data.get("Edit AccountID");
    }
    
    public void confirm(MouseEvent mouseEvent) {
        displayErrors();
        if (error()){
            return;
        }
        model.changePassword(accountID,passwordField.getText());
        goToEditAccount(accountID);

    }

    private void displayErrors() {
        errorLabel.setText("");
        if (passwordField.getText().length() < 6){
            errorLabel.setText("Password must be at least 6 characters");
        }
        else if (!passwordField.getText().equals(confirmField.getText())){
            errorLabel.setText("Passwords do not match");
        }
    }

    private boolean error() {
        return !errorLabel.getText().isEmpty();
    }

    public void cancel(MouseEvent mouseEvent) {
        goToEditAccount(accountID);
    }
}
