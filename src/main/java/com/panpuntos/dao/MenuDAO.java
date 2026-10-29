package com.panpuntos.dao;

import com.panpuntos.modelo.Menu;

import java.sql.SQLException;
import java.util.List;

public interface MenuDAO {

    List<Menu> listarActivos() throws SQLException;

    Menu buscarPorId(int id) throws SQLException;
}
