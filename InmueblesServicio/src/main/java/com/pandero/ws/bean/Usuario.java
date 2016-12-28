package com.pandero.ws.bean;

public class Usuario implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer UsuarioId;
	private String UsuarioNombre;
	private String EmpleadoCorreo;
	private String CelulaId;
	private String CelulaNombre;
	private String CelulaCorreo;
	
	public Integer getUsuarioId() {
		return UsuarioId;
	}
	public void setUsuarioId(Integer usuarioId) {
		UsuarioId = usuarioId;
	}
	public String getUsuarioNombre() {
		return UsuarioNombre;
	}
	public void setUsuarioNombre(String usuarioNombre) {
		UsuarioNombre = usuarioNombre;
	}
	public String getEmpleadoCorreo() {
		return EmpleadoCorreo;
	}
	public void setEmpleadoCorreo(String empleadoCorreo) {
		EmpleadoCorreo = empleadoCorreo;
	}
	public String getCelulaId() {
		return CelulaId;
	}
	public void setCelulaId(String celulaId) {
		CelulaId = celulaId;
	}
	public String getCelulaNombre() {
		return CelulaNombre;
	}
	public void setCelulaNombre(String celulaNombre) {
		CelulaNombre = celulaNombre;
	}
	public String getCelulaCorreo() {
		return CelulaCorreo;
	}
	public void setCelulaCorreo(String celulaCorreo) {
		CelulaCorreo = celulaCorreo;
	}
	
}
