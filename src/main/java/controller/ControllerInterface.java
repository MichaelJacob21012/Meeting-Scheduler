package controller;

import java.util.Map;

/**
 * This class was originally created for the  major project for 5CCS2SEG
 *
 * Interface to allow all view controllers to share the PrimaryController
 */
interface ControllerInterface {
  void setScreenParent(PrimaryController primaryController);

  void setData(Map<String, Object> toInject);

  void addData(String toAddKey, Object toAddVal);

    void refresh();
}