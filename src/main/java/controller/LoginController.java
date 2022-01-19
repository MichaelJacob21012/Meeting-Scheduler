package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.LoginModel;
import structure.Account;

/**
 * Controller class for the login screen
 */
public class LoginController extends SceneController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;


    private final LoginModel model = new LoginModel();

    @Override
    public void refresh() {
        errorLabel.setVisible(false);
    }

    public void login(ActionEvent actionEvent) {
        Account account = model.login(emailField.getText(),passwordField.getText());
        if (account != null){
           loginSuccess(account);
        }
        else {
           loginFail();
        }
    }

    private void loginSuccess(Account account) {
        data.put("account", account);
        goToMyCalendar();
    }

    private void loginFail() {
        errorLabel.setVisible(true);
    }
}
