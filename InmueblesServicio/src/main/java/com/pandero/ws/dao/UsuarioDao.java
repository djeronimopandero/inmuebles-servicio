package com.pandero.ws.dao;

import com.pandero.ws.bean.Usuario;

public interface UsuarioDao {

	public Usuario obtenerCorreoUsuarioCelula(String usuarioId) throws Exception;
}
