package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import structure.*;
import connect.AccountConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.AccountsModel;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller class for the accounts list screen
 */
public class AccountsController extends SceneController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Account> table;
    @FXML
    private TableColumn<Account,Integer> accountID;
    @FXML
    private TableColumn<Account,String> name;
    @FXML
    private TableColumn<Account,String> email;
    @FXML
    private TableColumn<Account,Void> action;


    private final AccountsModel model = new AccountsModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setPlaceholder(new Label("No accounts in database"));
        setupColumns();
        setUpDynamicSearchField();
    }

    private void setUpDynamicSearchField(){
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchButton.fire());
    }

    public void refresh(){
        displayMenuBar();
        model.fetchData();
        fillTable();
    }

    private void fillTable() {
        table.getItems().clear();
        for (Account account : model.getFilteredAccounts()){
            table.getItems().add(account);
        }
    }

    public void search(ActionEvent actionEvent) {
        model.search(searchField.getText());
        fillTable();
    }

    private void setupColumns() {
        accountID.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        name.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFullName()));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        setupActionColumn();
    }

    /**
     * Create the receivedAction column which contains three buttons - edit, delete and change password
     */
    private void setupActionColumn() {
        Callback<TableColumn<Account, Void>, TableCell<Account, Void>> cellFactory = new Callback<TableColumn<Account, Void>, TableCell<Account, Void>>() {
            @Override
            public TableCell<Account, Void> call(final TableColumn<Account, Void> param) {
                return new TableCell<Account, Void>() {

                    private final Button edit = new Button("Edit");
                    {
                        edit.setOnAction((ActionEvent event) -> {
                            Account account = getTableView().getItems().get(getIndex());
                            goToEditAccount(account.getAccountID());
                        });
                    }

                    private final Button delete = new Button("Delete");
                    {
                        delete.setOnAction((ActionEvent event) -> {
                            Account account = getTableView().getItems().get(getIndex());
                            Account currentAccount = (Account)data.get("account");
                            if (account.getAccountID() == currentAccount.getAccountID()){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setContentText("Cannot delete yourself");
                                alert.show();
                            }
                            else {
                                AccountConnection.deleteAccount(account.getAccountID());
                            }
                            refresh();
                        });
                    }

                    private final Button changePassword = new Button("Change Password");
                    {
                        changePassword.setOnAction((ActionEvent event) -> {
                            Account account = getTableView().getItems().get(getIndex());
                            goToChangePassword(account.getAccountID());
                        });
                    }
                    private final HBox hBox = new HBox(edit, delete, changePassword);
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : hBox);
                    }
                };
            }
        };
        action.setCellFactory(cellFactory);

    }

}
