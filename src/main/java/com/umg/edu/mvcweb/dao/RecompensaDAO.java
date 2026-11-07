package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.Recompensa;

import java.sql.SQLException;
import java.util.List;

public interface RecompensaDAO {

    List<Recompensa> listarActivas() throws SQLException;

    Recompensa buscarPorId(int id) throws SQLException;
}
