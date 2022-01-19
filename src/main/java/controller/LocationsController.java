package controller;

import connect.LocationConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.LocationsModel;
import structure.Location;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;


public class LocationsController extends SceneController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableColumn<Location, Integer> locationID;
    @FXML
    private TableColumn<Location, String> name;
    @FXML
    private TableColumn<Location, String> address;
    @FXML
    private TableColumn<Location, Void> action;
    @FXML
    private TableView<Location> table;
    private final LocationsModel model = new LocationsModel();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setPlaceholder(new Label("No locations in database"));
        setupColumns();
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
    }



    private void fillTable() {
        table.getItems().clear();
        for (Location location : model.getFilteredLocations()){
            table.getItems().add(location);
        }
    }

    public void search(ActionEvent actionEvent) {
        model.search(searchField.getText());
        fillTable();
    }

    private void setupColumns() {
        locationID.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<Location, Void>, TableCell<Location, Void>> cellFactory = new Callback<TableColumn<Location, Void>, TableCell<Location, Void>>() {
            @Override
            public TableCell<Location, Void> call(final TableColumn<Location, Void> param) {
                return new TableCell<Location, Void>() {

                    private final Button edit = new Button("Edit");
                    {
                        edit.setOnAction((ActionEvent event) -> {
                            Location location = getTableView().getItems().get(getIndex());
                            data.put("Edit locationID", location.getLocationID());
                            goToEditLocation();
                        });
                    }

                    private final Button delete = new Button("Delete");
                    {
                        delete.setOnAction((ActionEvent event) -> {
                            Location location = getTableView().getItems().get(getIndex());
                                LocationConnection.deleteLocation(location.getLocationID());
                            refresh();
                        });
                    }
                    private final HBox hBox = new HBox(edit, delete);
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
