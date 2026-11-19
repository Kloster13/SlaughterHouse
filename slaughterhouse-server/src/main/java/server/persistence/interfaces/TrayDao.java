package server.persistence.interfaces;

import domain.Tray;

import java.util.List;
import java.util.UUID;

public interface TrayDao
{
  void createTray(Tray tray);
  void update(Tray updatedTray);
  boolean existsById(UUID id);
  Tray getTrayById(UUID id);
  List<Tray> getAllTrays();
  void deleteTray(UUID id);
}
