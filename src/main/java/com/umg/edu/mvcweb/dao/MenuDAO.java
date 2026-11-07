package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.Menu;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MenuDAO {

    List<Menu> listarActivos() throws SQLException;

    Menu buscarPorId(int id) throws SQLException;

    Menu buscarPorId(Connection con, int id) throws SQLException;
}
