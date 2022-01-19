package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.BatchModel;
import structure.Meeting;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the batch scheduler screen
 */
public class BatchController extends SceneController implements Initializable {
    @FXML
    private TextArea batchDetails;
    public Button scheduleButton;
    @FXML
    private TableView<Meeting> table;
    @FXML
    private TableColumn<Meeting, String> titleColumn;
    @FXML
    private TableColumn<Meeting, String> organiserColumn;
    @FXML
    private TableColumn<Meeting, Integer> attendeesColumn;

    private final BatchModel model = new BatchModel();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setPlaceholder(new Label("No unscheduled meetings"));
        batchDetails.setEditable(false);
        setupColumns();
    }

    private void setupColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        organiserColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getAccount().getFullName()));
        attendeesColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    @Override
    public void refresh() {
        model.fetchData();
        table.getItems().clear();
        table.getItems().addAll(model.getOriginal().keySet());
        displayMenuBar();
        batchDetails.setText(model.batchDetails());
    }
    public void schedule(ActionEvent actionEvent) {
        schedule(new SimpleListProperty<>());
    }

    private void schedule(ObservableList<Meeting> toExclude) {
        model.setToExclude(new ArrayList<>(toExclude));
        model.schedule();
        if (displayConfirmation()){
            model.updateDatabase();
            refresh();
        }
        else {
            if (displayTryAgain()){
                ObservableList<Meeting> nextExclude = displayExclude();
                refresh();
                schedule(nextExclude);
            }
            refresh();
        }
    }

    private ObservableList<Meeting> displayExclude() {
        TableView<Meeting> tableView = createMeetingTable();
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        tableView.getItems().addAll(model.getOriginal().keySet());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText("Choose meetings that will not be scheduled. \nMultiple selection with CTRL + Click");
        alert.setGraphic(tableView);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return tableView.getSelectionModel().getSelectedItems();
        }
        return null;

    }

    private boolean displayTryAgain() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText("Try again?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean displayConfirmation() {

        TableView<Meeting> tableView = createMeetingTable();

        tableView.setRowFactory(param ->
                new TableRow<Meeting>() {
                    @Override
                    public void updateItem(Meeting meeting, boolean empty) {
                        super.updateItem(meeting, empty);
                        if (meeting == null) {
                            setStyle("");
                        } else if (meeting.isBooked()) {
                            setStyle("-fx-background-color: chartreuse;");
                        } else if (meeting.isBookingFailed()) {
                            setStyle("-fx-background-color: coral;");
                        } else {
                            setStyle("");
                        }
                    }
                }
        );

        tableView.getItems().addAll(model.getBest().keySet());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setGraphic(tableView);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private TableView<Meeting> createMeetingTable() {
        TableView<Meeting> tableView = new TableView<>();
        tableView.setPrefWidth(550);
        TableColumn<Meeting, String> titleColumn = new TableColumn<>();
        TableColumn<Meeting, String> locationColumn = new TableColumn<>();
        TableColumn<Meeting, String> roomColumn = new TableColumn<>();
        TableColumn<Meeting, LocalDate> dateColumn = new TableColumn<>();
        TableColumn<Meeting, LocalTime> timeColumn = new TableColumn<>();
        TableColumn<Meeting, Double> scoreColumn = new TableColumn<>();

        titleColumn.setText("Title");
        locationColumn.setText("Location");
        roomColumn.setText("Room");
        dateColumn.setText("Date");
        timeColumn.setText("Time");
        scoreColumn.setText("Score");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLocation().details()));
        roomColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getRoom().getName()));
        dateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getTimestamp().toLocalDateTime().toLocalDate()));
        timeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getTimestamp().toLocalDateTime().toLocalTime()));
        scoreColumn.setCellValueFactory(param ->
                new SimpleDoubleProperty(model.score(param.getValue()) * 100).asObject());

        tableView.getColumns().add(titleColumn);
        tableView.getColumns().add(locationColumn);
        tableView.getColumns().add(roomColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(timeColumn);
        tableView.getColumns().add(scoreColumn);

        return tableView;
    }

}
