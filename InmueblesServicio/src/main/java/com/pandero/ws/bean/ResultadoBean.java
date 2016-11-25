package com.pandero.ws.bean;

import java.util.List;

public class ResultadoBean {

	String mensajeError;
	Object resultado;
	List<Object> listaResultado;
	public String getMensajeError() {
		return mensajeError;
	}
	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	public Object getResultado() {
		return resultado;
	}
	public void setResultado(Object resultado) {
		this.resultado = resultado;
	}
	public List<Object> getListaResultado() {
		return listaResultado;
	}
	public void setListaResultado(List<Object> listaResultado) {
		this.listaResultado = listaResultado;
	}
	
}
