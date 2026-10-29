package com.panpuntos.dao;

import com.panpuntos.modelo.Recompensa;

import java.sql.SQLException;
import java.util.List;

public interface RecompensaDAO {

    List<Recompensa> listarActivas() throws SQLException;

    Recompensa buscarPorId(int id) throws SQLException;
}
