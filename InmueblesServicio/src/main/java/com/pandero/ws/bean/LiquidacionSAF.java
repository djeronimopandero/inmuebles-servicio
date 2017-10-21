package com.pandero.ws.bean;

public class LiquidacionSAF implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer PedidoID;
	private Integer PedidoInversionID;
	private String LiquidacionNumero;
	private String LiquidacionPagoTesoreria;
	private String LiquidacionFecha;
	private String LiquidacionTipo;
	private Integer ProveedorID;
	private String LiquidacionTipoDocumento;
	private Integer ContratoID;
	private String MonedaID;
	private Double LiquidacionImporte;
	private String LiquidacionOrigen;
	private String LiquidacionDestino;
	private String LiquidacionEstado;
	private String LiquidacionFechaEstado;
	private int NroArmada;
	private Integer usuarioIdCreacion;	
	private String usuarioLogin;
	private String nroReferenciaLiquidacionTesoreria;
	
	public Integer getPedidoID() {
		return PedidoID;
	}
	public void setPedidoID(Integer pedidoID) {
		PedidoID = pedidoID;
	}	
	public Integer getPedidoInversionID() {
		return PedidoInversionID;
	}
	public void setPedidoInversionID(Integer pedidoInversionID) {
		PedidoInversionID = pedidoInversionID;
	}
	public String getLiquidacionNumero() {
		return LiquidacionNumero;
	}
	public void setLiquidacionNumero(String liquidacionNumero) {
		LiquidacionNumero = liquidacionNumero;
	}
	public String getLiquidacionPagoTesoreria() {
		return LiquidacionPagoTesoreria;
	}
	public void setLiquidacionPagoTesoreria(String liquidacionPagoTesoreria) {
		LiquidacionPagoTesoreria = liquidacionPagoTesoreria;
	}
	public String getLiquidacionFecha() {
		return LiquidacionFecha;
	}
	public void setLiquidacionFecha(String liquidacionFecha) {
		LiquidacionFecha = liquidacionFecha;
	}
	public String getLiquidacionTipo() {
		return LiquidacionTipo;
	}
	public void setLiquidacionTipo(String liquidacionTipo) {
		LiquidacionTipo = liquidacionTipo;
	}
	public Integer getProveedorID() {
		return ProveedorID;
	}
	public void setProveedorID(Integer proveedorID) {
		ProveedorID = proveedorID;
	}
	public String getLiquidacionTipoDocumento() {
		return LiquidacionTipoDocumento;
	}
	public void setLiquidacionTipoDocumento(String liquidacionTipoDocumento) {
		LiquidacionTipoDocumento = liquidacionTipoDocumento;
	}
	public Integer getContratoID() {
		return ContratoID;
	}
	public void setContratoID(Integer contratoID) {
		ContratoID = contratoID;
	}
	public String getMonedaID() {
		return MonedaID;
	}
	public void setMonedaID(String monedaID) {
		MonedaID = monedaID;
	}
	public Double getLiquidacionImporte() {
		return LiquidacionImporte;
	}
	public void setLiquidacionImporte(Double liquidacionImporte) {
		LiquidacionImporte = liquidacionImporte;
	}
	public String getLiquidacionOrigen() {
		return LiquidacionOrigen;
	}
	public void setLiquidacionOrigen(String liquidacionOrigen) {
		LiquidacionOrigen = liquidacionOrigen;
	}
	public String getLiquidacionDestino() {
		return LiquidacionDestino;
	}
	public void setLiquidacionDestino(String liquidacionDestino) {
		LiquidacionDestino = liquidacionDestino;
	}
	public String getLiquidacionEstado() {
		return LiquidacionEstado;
	}
	public void setLiquidacionEstado(String liquidacionEstado) {
		LiquidacionEstado = liquidacionEstado;
	}
	public String getLiquidacionFechaEstado() {
		return LiquidacionFechaEstado;
	}
	public void setLiquidacionFechaEstado(String liquidacionFechaEstado) {
		LiquidacionFechaEstado = liquidacionFechaEstado;
	}
	public int getNroArmada() {
		return NroArmada;
	}
	public void setNroArmada(int nroArmada) {
		NroArmada = nroArmada;
	}
	public Integer getUsuarioIdCreacion() {
		return usuarioIdCreacion;
	}
	public void setUsuarioIdCreacion(Integer usuarioIdCreacion) {
		this.usuarioIdCreacion = usuarioIdCreacion;
	}
	public String getUsuarioLogin() {
		return usuarioLogin;
	}
	public void setUsuarioLogin(String usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}
	public String getNroReferenciaLiquidacionTesoreria() {
		return nroReferenciaLiquidacionTesoreria;
	}
	public void setNroReferenciaLiquidacionTesoreria(String nroReferenciaLiquidacionTesoreria) {
		this.nroReferenciaLiquidacionTesoreria = nroReferenciaLiquidacionTesoreria;
	}
		
}
