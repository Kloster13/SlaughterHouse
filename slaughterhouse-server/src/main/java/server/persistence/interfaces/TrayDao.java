package server.persistence.interfaces;

import domain.Tray;

import java.util.List;

public interface TrayDao
{
  void createTray(Tray tray);
  void update(Tray updatedTray);
  Tray getTrayById(long id);
  List<Tray> getAllTrays();
  void deleteTray(long id);
}
