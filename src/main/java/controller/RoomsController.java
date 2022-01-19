package controller;

import connect.RoomConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.RoomsModel;
import structure.Account;
import structure.Room;

import java.net.URL;
import java.util.ResourceBundle;

public class RoomsController extends SceneController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Room> table;
    @FXML
    private TableColumn<Room, Integer> roomID;
    @FXML
    private TableColumn<Room, String> name;
    @FXML
    private TableColumn<Room, String> locationName;
    @FXML
    private TableColumn<Room, Void> action;
    private final RoomsModel model = new RoomsModel();
    private Account account;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setPlaceholder(new Label("No rooms in database"));
        setUpDynamicSearchField();
    }

    private void setUpDynamicSearchField(){
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchButton.fire());
    }

    @Override
    public void refresh(){
        displayMenuBar();
        model.fetchData();
        fillTable();
        account = (Account) data.get("account");
        setupColumns();
    }


    private void fillTable() {
        table.getItems().clear();
        for (Room room : model.getFilteredRooms()){
            table.getItems().add(room);
        }
    }

    public void search(ActionEvent actionEvent) {
        model.search(searchField.getText());
        fillTable();
    }

    private void setupColumns() {
        roomID.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationName.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLocation().getName()));
        setupActionColumn();
    }

    private void setupActionColumn() {
        if (account.isAdmin()){
            setupAdminActions();
        }
        else {
            setupNormalActions();
        }

    }

    private void setupNormalActions() {
        Callback<TableColumn<Room, Void>, TableCell<Room, Void>> cellFactory = new Callback<TableColumn<Room, Void>, TableCell<Room, Void>>() {
            @Override
            public TableCell<Room, Void> call(final TableColumn<Room, Void> param) {
                return new TableCell<Room, Void>() {

                    private final Button view = new Button("View");
                    {
                        view.setOnAction((ActionEvent event) -> {
                            Room room = getTableView().getItems().get(getIndex());
                            goToEditRoom(room);
                        });
                    }

                    private final Button calendar = new Button("Calendar");
                    {
                        calendar.setOnAction((ActionEvent event) -> {
                            Room room = getTableView().getItems().get(getIndex());
                            data.put("Calendar roomID", room.getRoomID());
                            goToRoomCalendar();
                        });
                    }
                    private final HBox pane = new HBox(view, calendar);
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        action.setCellFactory(cellFactory);
    }

    private void setupAdminActions() {
        Callback<TableColumn<Room, Void>, TableCell<Room, Void>> cellFactory = new Callback<TableColumn<Room, Void>, TableCell<Room, Void>>() {
            @Override
            public TableCell<Room, Void> call(final TableColumn<Room, Void> param) {
                return new TableCell<Room, Void>() {

                    private final Button edit = new Button("Edit");
                    {
                        edit.setOnAction((ActionEvent event) -> {
                            Room room = getTableView().getItems().get(getIndex());
                            goToEditRoom(room);
                        });
                    }

                    private final Button delete = new Button("Delete");
                    {
                        delete.setOnAction((ActionEvent event) -> {
                            Room room = getTableView().getItems().get(getIndex());
                            RoomConnection.deleteRoom(room.getRoomID());
                            refresh();
                        });
                    }

                    private final Button calendar = new Button("Calendar");
                    {
                        calendar.setOnAction((ActionEvent event) -> {
                            Room room = getTableView().getItems().get(getIndex());
                            data.put("Calendar roomID", room.getRoomID());
                            goToRoomCalendar();
                        });
                    }
                    private final HBox hBox = new HBox(edit, delete, calendar);
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
