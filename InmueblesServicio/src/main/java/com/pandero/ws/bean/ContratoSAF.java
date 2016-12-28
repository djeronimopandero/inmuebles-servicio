package com.pandero.ws.bean;

public class ContratoSAF implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer contratoId;
	private Integer pedidoId;
	private String nroContrato;
	private Double montoCertificado;
	private Double montoDisponible;
	private Double diferenciaPrecio;
	private Double diferenciaPrecioDisponible;
	private Double otrosIngresos;
	private Double otrosDisponibles;
	private Double totalDisponible;
	private String estado;
	private String fechaAdjudicacion;
	private String fechaVenta;
	private Integer personaId;
	private Integer asociadoId;
	private Integer esAdjudicado;
	
	private Integer situacionContratoID; //SAF
	private String situacionContrato; //SAF
	private String situacionContratoCASPIO; //CASPIO
	
	private String funcionarioServicioyVentas;
	private String correoCelula;
	
	public Integer getContratoId() {
		return contratoId;
	}
	public void setContratoId(Integer contratoId) {
		this.contratoId = contratoId;
	}
	public Integer getPedidoId() {
		return pedidoId;
	}
	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}
	public String getNroContrato() {
		return nroContrato;
	}
	public void setNroContrato(String nroContrato) {
		this.nroContrato = nroContrato;
	}
	public Double getMontoCertificado() {
		return montoCertificado;
	}
	public void setMontoCertificado(Double montoCertificado) {
		this.montoCertificado = montoCertificado;
	}
	public Double getMontoDisponible() {
		return montoDisponible;
	}
	public void setMontoDisponible(Double montoDisponible) {
		this.montoDisponible = montoDisponible;
	}
	public Double getDiferenciaPrecio() {
		return diferenciaPrecio;
	}
	public void setDiferenciaPrecio(Double diferenciaPrecio) {
		this.diferenciaPrecio = diferenciaPrecio;
	}
	public Double getDiferenciaPrecioDisponible() {
		return diferenciaPrecioDisponible;
	}
	public void setDiferenciaPrecioDisponible(Double diferenciaPrecioDisponible) {
		this.diferenciaPrecioDisponible = diferenciaPrecioDisponible;
	}
	public Double getOtrosIngresos() {
		return otrosIngresos;
	}
	public void setOtrosIngresos(Double otrosIngresos) {
		this.otrosIngresos = otrosIngresos;
	}
	public Double getOtrosDisponibles() {
		return otrosDisponibles;
	}
	public void setOtrosDisponibles(Double otrosDisponibles) {
		this.otrosDisponibles = otrosDisponibles;
	}
	public Double getTotalDisponible() {
		return totalDisponible;
	}
	public void setTotalDisponible(Double totalDisponible) {
		this.totalDisponible = totalDisponible;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getFechaAdjudicacion() {
		return fechaAdjudicacion;
	}
	public void setFechaAdjudicacion(String fechaAdjudicacion) {
		this.fechaAdjudicacion = fechaAdjudicacion;
	}
	public String getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(String fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public Integer getPersonaId() {
		return personaId;
	}
	public void setPersonaId(Integer personaId) {
		this.personaId = personaId;
	}
	public Integer getAsociadoId() {
		return asociadoId;
	}
	public void setAsociadoId(Integer asociadoId) {
		this.asociadoId = asociadoId;
	}
	public Integer getSituacionContratoID() {
		return situacionContratoID;
	}
	public void setSituacionContratoID(Integer situacionContratoID) {
		this.situacionContratoID = situacionContratoID;
	}
	public String getSituacionContrato() {
		return situacionContrato;
	}
	public void setSituacionContrato(String situacionContrato) {
		this.situacionContrato = situacionContrato;
	}
	public String getSituacionContratoCASPIO() {
		return situacionContratoCASPIO;
	}
	public void setSituacionContratoCASPIO(String situacionContratoCASPIO) {
		this.situacionContratoCASPIO = situacionContratoCASPIO;
	}
	public Integer getEsAdjudicado() {
		return esAdjudicado;
	}
	public void setEsAdjudicado(Integer esAdjudicado) {
		this.esAdjudicado = esAdjudicado;
	}
	public String getFuncionarioServicioyVentas() {
		return funcionarioServicioyVentas;
	}
	public void setFuncionarioServicioyVentas(String funcionarioServicioyVentas) {
		this.funcionarioServicioyVentas = funcionarioServicioyVentas;
	}
	public String getCorreoCelula() {
		return correoCelula;
	}
	public void setCorreoCelula(String correoCelula) {
		this.correoCelula = correoCelula;
	}
	
}
