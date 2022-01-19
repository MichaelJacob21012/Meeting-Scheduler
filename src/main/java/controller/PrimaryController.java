package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * This class was originally created for the  major project for 5CCS2SEG
 *
 * PrimaryController is the main views controller, it works by storing a HashMap
 * (Nodes [VBox'] in this case) that can be set as the active content in a
 * StackPane. Each view contoller (that is - any files which have a .fxml in the
 * resouorces folder) will need to inject *this* PrimaryController in order to
 * change windows.
 */
public class PrimaryController extends StackPane {

  private Map<String, Node> panes = new HashMap<>();
  private Map<String, ControllerInterface> data = new HashMap<>();
  private Stage stage;

  public PrimaryController(Stage stage) {
    super();
    this.stage = stage;
  }

  /**
   * Add an array of views from the views folder to the controller
   * 
   * @param toAdd array of names of views to add
   */
  public void addViews(String[] toAdd) {
    for (String s : toAdd) {
      addPane(s, loadView(s));
    }
  }

  /**
   * Add a view from the views folder to the controller
   * 
   * @param toAdd view to add
   */
  public void addView(String toAdd) {
    Node n = loadView(toAdd);
    addPane(toAdd, n);
  }

  /**
   * Loads a view from the views resource folder
   * 
   * @param toLoad array of names of views to add
   * @return the loadedNode
   */
  private Node loadView(String toLoad) {
    try {
      System.out.print("view/" + toLoad + ".fxml");
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/" + toLoad + ".fxml"));
      Parent toReturn = loader.load();
      ControllerInterface ci = loader.getController();
      System.out.println("Initialised " + toLoad);
      ci.setScreenParent(this);
      data.put(toLoad, ci);
      return toReturn;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Adds a pane to the controller
   * 
   * @param name name of the pane to add
   * @param pane the pane to add
   */
  private void addPane(String name, Node pane) {
    panes.put(name, pane);
  }

  /**
   * Adds a pane to the controller
   * 
   * @param name name of the pane to set
   * @return was the operation successful?
   */
  public void setPane(String name) {
    Node n = panes.get(name);
    ControllerInterface ci = data.get(name);
    if (n != null && ci != null) {
      ci.refresh();
      if (!getChildren().isEmpty()) {
        getChildren().remove(0);
        getChildren().add(0, n);
      } else {
        getChildren().add(n);
      }
      stage.sizeToScene();
    } else {
      System.out.println("screen hasn't been loaded!!! \n");
    }
  }

  /**
   * Send data to another pane controller
   * 
   * @param toReceive the receiver pane
   * @param toSetKey  the key of the data to set
   * @param toSetVal  the value of the data to get
   */
  public void sendTo(String toReceive, String toSetKey, Object toSetVal) {
    ControllerInterface ci = data.get(toReceive);
    if (ci != null) {
      ci.addData(toSetKey, toSetVal);
    }
  }

  public void closeStage(){
    stage.close();
  }
  public Stage getStage(){
    return stage;
  }

}