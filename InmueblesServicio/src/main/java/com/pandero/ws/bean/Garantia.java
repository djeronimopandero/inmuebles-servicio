package com.pandero.ws.bean;

public class Garantia {
	
	private Integer IdGarantia;
	private Integer garantiaSAFId;
	private Integer PedidoID;
	private String PartidaRegistral;
	private String FichaConstitucion;
	private String FechaConstitucion;
	private String MontoPrima;
	private String EstadoGarantia;
	private String PedidoNumero;
						
	public Integer getIdGarantia() {
		return IdGarantia;
	}
	public void setIdGarantia(Integer idGarantia) {
		IdGarantia = idGarantia;
	}
	
	public Integer getGarantiaSAFId() {
		return garantiaSAFId;
	}
	public void setGarantiaSAFId(Integer garantiaSAFId) {
		this.garantiaSAFId = garantiaSAFId;
	}
	public Integer getPedidoID() {
		return PedidoID;
	}
	public void setPedidoID(Integer pedidoID) {
		PedidoID = pedidoID;
	}
	public String getPartidaRegistral() {
		return PartidaRegistral;
	}
	public void setPartidaRegistral(String partidaRegistral) {
		PartidaRegistral = partidaRegistral;
	}
	public String getFichaConstitucion() {
		return FichaConstitucion;
	}
	public void setFichaConstitucion(String fichaConstitucion) {
		FichaConstitucion = fichaConstitucion;
	}
	public String getFechaConstitucion() {
		return FechaConstitucion;
	}
	public void setFechaConstitucion(String fechaConstitucion) {
		FechaConstitucion = fechaConstitucion;
	}
	public String getMontoPrima() {
		return MontoPrima;
	}
	public void setMontoPrima(String montoPrima) {
		MontoPrima = montoPrima;
	}
	public String getEstadoGarantia() {
		return EstadoGarantia;
	}
	public void setEstadoGarantia(String estadoGarantia) {
		EstadoGarantia = estadoGarantia;
	}
	public String getPedidoNumero() {
		return PedidoNumero;
	}
	public void setPedidoNumero(String pedidoNumero) {
		PedidoNumero = pedidoNumero;
	}
		
}
