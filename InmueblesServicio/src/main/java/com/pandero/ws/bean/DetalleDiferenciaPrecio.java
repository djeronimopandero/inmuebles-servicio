package com.pandero.ws.bean;

public class DetalleDiferenciaPrecio implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
		
	private Integer pedidoId;
	private String diferenciaPrecio; 
	private String importeFinanciado; 
	private String saldoDiferencia;
	private String montoDifPrecioPagado;
	private String tipoInversion;
	private double montoDisponible;
	
	public Integer getPedidoId() {
		return pedidoId;
	}
	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}
	/**
	  * @return	: Double
	  * @date	: 14 de dic. de 2016
	  * @time	: 9:25:29 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Suma de los certificados disponibles del pedido menos el importe total de las inversiones
	 */
	public String getDiferenciaPrecio() {
		return diferenciaPrecio;
	}
	public void setDiferenciaPrecio(String diferenciaPrecio) {
		this.diferenciaPrecio = diferenciaPrecio;
	}
	
	 /**
	  * @return	: Double
	  * @date	: 14 de dic. de 2016
	  * @time	: 9:28:31 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : 	Suma de la diferencia de precio de cada contrato incluido en el pedido (SAF)
	 */
	public String getImporteFinanciado() {
		return importeFinanciado;
	}
	public void setImporteFinanciado(String importeFinanciado) {
		this.importeFinanciado = importeFinanciado;
	}
	
	 /**
	  * @return	: Double
	  * @date	: 14 de dic. de 2016
	  * @time	: 9:34:00 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : La resta de la diferencia de precio y el importe financiado
	 */
	public String getSaldoDiferencia() {
		return saldoDiferencia;
	}
	public void setSaldoDiferencia(String saldoDiferencia) {
		this.saldoDiferencia = saldoDiferencia;
	}
	
	public String getMontoDifPrecioPagado() {
		return montoDifPrecioPagado;
	}
	public void setMontoDifPrecioPagado(String montoDifPrecioPagado) {
		this.montoDifPrecioPagado = montoDifPrecioPagado;
	}
	public String getTipoInversion() {
		return tipoInversion;
	}
	public void setTipoInversion(String tipoInversion) {
		this.tipoInversion = tipoInversion;
	}
	public double getMontoDisponible() {
		return montoDisponible;
	}
	public void setMontoDisponible(double montoDisponible) {
		this.montoDisponible = montoDisponible;
	} 
	
}
