package com.pandero.ws.bean;

public class PedidoContratoCaspio {
	
	private Integer PedidoId;
	
	private Integer ContratoId;
	
	private String Estado;
	
	private String FechaCreacion;

	public Integer getPedidoId() {
		return PedidoId;
	}

	public void setPedidoId(Integer pedidoId) {
		PedidoId = pedidoId;
	}

	public Integer getContratoId() {
		return ContratoId;
	}

	public void setContratoId(Integer contratoId) {
		ContratoId = contratoId;
	}

	public String getEstado() {
		return Estado;
	}

	public void setEstado(String estado) {
		Estado = estado;
	}

	public String getFechaCreacion() {
		return FechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		FechaCreacion = fechaCreacion;
	}
	
}
