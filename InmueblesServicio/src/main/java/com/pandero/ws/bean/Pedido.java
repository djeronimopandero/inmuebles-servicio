package com.pandero.ws.bean;

public class Pedido {

	private Integer PedidoId;
	private Integer AsociadoId;
	private String Fecha;
	private String Estado;
	private String NroPedido;
	private String Producto;
	private Double CancelacionDiferenciaPrecioMonto;
		
	public Integer getPedidoId() {
		return PedidoId;
	}
	public void setPedidoId(Integer pedidoId) {
		PedidoId = pedidoId;
	}
	public Integer getAsociadoId() {
		return AsociadoId;
	}
	public void setAsociadoId(Integer asociadoId) {
		AsociadoId = asociadoId;
	}
	public String getFecha() {
		return Fecha;
	}
	public void setFecha(String fecha) {
		Fecha = fecha;
	}
	public String getEstado() {
		return Estado;
	}
	public void setEstado(String estado) {
		Estado = estado;
	}
	public String getNroPedido() {
		return NroPedido;
	}
	public void setNroPedido(String nroPedido) {
		NroPedido = nroPedido;
	}
	public String getProducto() {
		return Producto;
	}
	public void setProducto(String producto) {
		Producto = producto;
	}
	public Double getCancelacionDiferenciaPrecioMonto() {
		return CancelacionDiferenciaPrecioMonto;
	}
	public void setCancelacionDiferenciaPrecioMonto(
			Double cancelacionDiferenciaPrecioMonto) {
		CancelacionDiferenciaPrecioMonto = cancelacionDiferenciaPrecioMonto;
	}
	
}
