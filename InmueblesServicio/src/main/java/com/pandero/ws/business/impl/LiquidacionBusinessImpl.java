package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.ConceptoLiquidacion;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Desembolso;
import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.service.DesembolsoService;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Component
public class LiquidacionBusinessImpl implements LiquidacionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(LiquidacionBusinessImpl.class);

	@Autowired
	InversionService inversionService;	
	@Autowired
	PedidoService pedidoService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	ContratoService contratoService;
	@Autowired
	GarantiaService garantiaService;
	@Autowired
	DesembolsoService desembolsoService;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	LiquidacionDao liquidacionDao;
	@Autowired
	ContratoBusiness contratoBusiness;
	
	public List<Contrato> obtenerTablaContratosPedidoActualizado(String nroPedido) throws Exception{		
		// Obtener contratos del pedido
		System.out.println("NRO PEDIDO: "+nroPedido);
		List<Contrato> listaContratosPedidoSAF = pedidoDao.obtenerContratosxPedidoSAF(nroPedido);		
		if(listaContratosPedidoSAF==null){
			LOG.info("listaContratosPedidoSAF:: NULL");
		}else{
			LOG.info("listaContratosPedidoSAF: "+listaContratosPedidoSAF.size());
		}
		
		// Obtener diferencia precio x contrato
		Double diferenciaPrecio = 0.00;
		for(Contrato contrato : listaContratosPedidoSAF){
			diferenciaPrecio = contratoDao.obtenerDiferenciaPrecioPorContrato(contrato.getNroContrato());
			contrato.setDiferenciaPrecio(diferenciaPrecio);		
			// Inicializar los montos disponibles				
			contrato.setDiferenciaPrecioDisponible(0.00);
			contrato.setMontoDisponible(0.00);
			contrato.setTotalDisponible(0.00);
			contrato.setMontoLiquidacionContrato(0.00);
			contrato.setMontoLiquidacionDifPrecio(0.00);
		}		
				
		// Obtener liquidaciones del pedido
		List<LiquidacionSAF> liquidacionPedido = liquidacionDao.obtenerLiquidacionPorPedidoSAF(nroPedido);
		if(liquidacionPedido==null){
			LOG.info("liquidacionPedido:: NULL");
		}else{
			LOG.info("liquidacionPedido: "+liquidacionPedido.size());
		}
		
		// Obtener monto liquidado x contrato
		if(liquidacionPedido!=null && liquidacionPedido.size()>0){			
			for(LiquidacionSAF liquidacion : liquidacionPedido){
				for(Contrato contrato : listaContratosPedidoSAF){
					System.out.println("liquidacion.getContratoID():: "+liquidacion.getContratoID()+" - contrato.getContratoId():: "+contrato.getContratoId());
					if(liquidacion.getContratoID().intValue()==contrato.getContratoId().intValue()){
						if(Constantes.Liquidacion.TIPO_DOCU_CONTRATO.equals(liquidacion.getLiquidacionTipoDocumento())){
							contrato.setMontoLiquidacionContrato(contrato.getMontoLiquidacionContrato().doubleValue()+liquidacion.getLiquidacionImporte());
						}
						if(Constantes.Liquidacion.TIPO_DOCU_DIF_PRECIO.equals(liquidacion.getLiquidacionTipoDocumento())){
							contrato.setMontoLiquidacionDifPrecio(contrato.getMontoLiquidacionDifPrecio().doubleValue()+liquidacion.getLiquidacionImporte());
						}
					}
				}
			}		
		}
		LOG.info("LUEGO - Obtener monto liquidado x contrato");
		
		// Obtener montos diponibles
		for(Contrato contrato : listaContratosPedidoSAF){
			contrato.setMontoDisponible(contrato.getMontoCertificado().doubleValue()-contrato.getMontoLiquidacionContrato().doubleValue());
			contrato.setDiferenciaPrecioDisponible(contrato.getDiferenciaPrecio().doubleValue()-contrato.getMontoLiquidacionDifPrecio().doubleValue());
			contrato.setTotalDisponible(contrato.getMontoDisponible().doubleValue()+contrato.getDiferenciaPrecioDisponible());
		}
		LOG.info("LUEGO - Obtener montos diponibles");
		
		return listaContratosPedidoSAF;
	}
	
	@Override
	public String generarLiquidacionPorInversion(String nroInversion, String nroArmada, String usuarioId)
			throws Exception {
		System.out.println("EN generarLiquidacionPorInversion");
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "", nroArmadaId = "1";
		boolean validacionLiquidacion = true;
		
		// Obtener datos pedido-inversion SAF
		PedidoInversionSAF pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
		
		// Obtener valores pedido-contrato actualizado
		System.out.println("pedidoInversionSAF.getNroPedido(): "+pedidoInversionSAF.getNroPedido());
		List<Contrato> listaPedidoContrato = obtenerTablaContratosPedidoActualizado(pedidoInversionSAF.getNroPedido());
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		
		// Obtener nroArmadaId		
		if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())
				&& inversion.getServicioConstructora().booleanValue()==false){	
			nroArmadaId = String.valueOf(Integer.parseInt(nroArmada)+1);
		}
		LOG.info("nroArmadaId: "+nroArmadaId);
					
		// Validar montos por cobrar
		boolean validacionConceptos = validacionConceptosLiquidacion(inversion);
		if(validacionConceptos==false){
			validacionLiquidacion = false;
			resultado = Constantes.Service.RESULTADO_PENDIENTE_COBROS;
		}
		
		// Validar garantias
		if(validacionLiquidacion){			
			// Obtener garantias del pedido
			List<Garantia> listaGarantias = garantiaService.obtenerGarantiasPorPedido(String.valueOf(inversion.getPedidoId().intValue()));
			if(listaGarantias!=null && listaGarantias.size()>0){
				for(Garantia garantia : listaGarantias){
					System.out.println("garantia: "+garantia.getIdGarantia()+" - "+garantia.getFichaConstitucion()+" - "+garantia.getFechaConstitucion());
					if(Util.esVacio(garantia.getFichaConstitucion()) || Util.esVacio(garantia.getFechaConstitucion())){
						validacionLiquidacion=false;
					}
				}
			}else{
				validacionLiquidacion=false;
			}
			// Verificar si existen garantias
			if(validacionLiquidacion==false) resultado = Constantes.Service.RESULTADO_NO_GARANTIAS;
		}
					
		// Validar el estado de la liquidacion
		if(validacionLiquidacion){
			// Obtener liquidaciones del pedido
			List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(nroInversion);						
			String estadoLiquidacion = Util.obtenerEstadoLiquidacionPorNroArmada(liquidacionInversion, nroArmadaId);
			LOG.info("ESTADO LIQUIDACION:: "+estadoLiquidacion);
			if(!estadoLiquidacion.equals("")){
				resultado = Constantes.Service.RESULTADO_EXISTE_LIQUIDACION;
				validacionLiquidacion = false;
			}
		}
		
		// Verificar si existe monto disponible para la inversion
		double totalDisponible=0;
		// Obtener total disponible en contratos
		double totalDisponibleContratos = obtenerTotalDisponibleEnPedido(listaPedidoContrato);
		// Obtener diferencia precio pagada
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
		double montoDifPrecio = pedido.getCancelacionDiferenciaPrecioMonto()==null?0.00:pedido.getCancelacionDiferenciaPrecioMonto();
		// Total disponible
		totalDisponible=totalDisponibleContratos+montoDifPrecio;
		System.out.println("totalDisponible:: "+totalDisponibleContratos+"+"+montoDifPrecio);
		
		if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())
				&& !inversion.getServicioConstructora()){
		}else{
			if(validacionLiquidacion){				
				// Verfivar si hay monto para liquidar
				if(totalDisponible<inversion.getImporteInversion().doubleValue()){
					resultado = Constantes.Service.NO_MONTO_DISPONIBLE_LIQUIDAR;
					validacionLiquidacion = false;
				}
			}
		}
						
		// Obtener liquidacion de la inversion
		if(validacionLiquidacion){
			// Obtener Armada x tipo de inversion
			double montoALiquidar = 0.00;
			if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){				
				// Obtener monto a liquidar
				List<Constante> listaArmadasDesemb = constanteService.obtenerListaArmadasDesembolso();
				double porcentajeArmada = Util.obtenerPorcentajeArmada(listaArmadasDesemb, nroArmadaId);
				System.out.println("inversion.getImporteInversion(): "+inversion.getImporteInversion()+" - porcentajeArmada: "+porcentajeArmada);
				
				double montoUsadoLiquidacion = inversion.getImporteInversion().doubleValue();
				if(!inversion.getServicioConstructora()){
					if(totalDisponibleContratos<inversion.getImporteInversion().doubleValue()){
						montoUsadoLiquidacion = totalDisponibleContratos;
					}
				}
				
				double porcentajeDecimal = porcentajeArmada/100;
				System.out.println("montoUsadoLiquidacion:: "+montoUsadoLiquidacion+" - porcentajeDecimal:: "+porcentajeDecimal);
				montoALiquidar = montoUsadoLiquidacion*porcentajeDecimal;					
			}else{
				montoALiquidar = inversion.getImporteInversion();
			}
			System.out.println("montoALiquidar: "+montoALiquidar);
			
			// Generar liquidacion por monto
			generarLiquidacionPorMonto(pedidoInversionSAF, montoALiquidar, nroArmadaId, listaPedidoContrato, usuarioId);
						
			// Actualizar montos de los contratos del pedido
			actualizarTablaContratosPedido(pedidoInversionSAF.getNroPedido());
		}
		LOG.info("TERMINO LIQUIDACION");		
		
		return resultado;
	}
	
	private double obtenerTotalDisponibleEnPedido(List<Contrato> listaPedidoContrato){
		double totalDisponible = 0.00;
		if(listaPedidoContrato!=null && listaPedidoContrato.size()>0){
			for(Contrato contrato : listaPedidoContrato){
				totalDisponible += contrato.getMontoDisponible()+contrato.getDiferenciaPrecioDisponible();
			}
		}
		return totalDisponible;
	}
	
	private String generarLiquidacionPorMonto(PedidoInversionSAF pedidoInversionSAF, double montoALiquidar, String nroArmadaId,
			List<Contrato> listaPedidoContrato, String usuarioId) throws Exception{
		LiquidacionSAF liquidacionSAF = null;
		List<LiquidacionSAF> listaLiquidaciones = new ArrayList<>();
		double montoALiquidarRestante = montoALiquidar, montoTotalLiquidado = 0.00;
		
		// Obtener el monto de los contratos
		for(Contrato contrato : listaPedidoContrato){
			double montoUsado = 0.00;
			if(montoALiquidarRestante>0 && contrato.getMontoDisponible().doubleValue()>0){				
				montoALiquidarRestante -= contrato.getMontoDisponible().doubleValue();
				if(montoALiquidarRestante>0){
					// usa todo el monto disponible
					montoUsado = contrato.getMontoDisponible();					
				}else{
					// usa todo el monto a liquidar
					montoUsado = contrato.getMontoDisponible()+montoALiquidarRestante;
				}	
				montoTotalLiquidado += montoUsado;
				liquidacionSAF = crearRegistroLiquidacion(pedidoInversionSAF, contrato, 
						Constantes.Liquidacion.TIPO_DOCU_CONTRATO, montoUsado);
				listaLiquidaciones.add(liquidacionSAF);								
			}
		}
		
		// Obtener el monto de las diferencias de precio
		for(Contrato contrato : listaPedidoContrato){
			double montoUsado = 0.00;
			if(montoALiquidarRestante>0 && contrato.getDiferenciaPrecioDisponible().doubleValue()>0){				
				montoALiquidarRestante -= contrato.getDiferenciaPrecioDisponible().doubleValue();
				if(montoALiquidarRestante>0){
					// usa todo el monto disponible
					montoUsado = contrato.getDiferenciaPrecioDisponible();					
				}else{
					// usa todo el monto a liquidar
					montoUsado = contrato.getDiferenciaPrecioDisponible()+montoALiquidarRestante;
				}	
				montoTotalLiquidado += montoUsado;
				liquidacionSAF = crearRegistroLiquidacion(pedidoInversionSAF, contrato, 
						Constantes.Liquidacion.TIPO_DOCU_DIF_PRECIO, montoUsado);
				listaLiquidaciones.add(liquidacionSAF);								
			}
		}
		
		// Grabar liquidacion
		if(listaLiquidaciones.size()>0){
			// Generar numero de liquidacion
			String numeroLiquidacion = liquidacionDao.obtenerCorrelativoLiquidacionSAF(pedidoInversionSAF.getPedidoID());
			// Insertar las liquidaciones
			for(LiquidacionSAF liquidacion : listaLiquidaciones){
				liquidacion.setLiquidacionNumero(numeroLiquidacion);
				liquidacion.setNroArmada(Integer.parseInt(nroArmadaId));
				liquidacionDao.registrarLiquidacionInversionSAF(liquidacion,usuarioId);
			}
			// Actualizar nro liquidacion en inversion
			inversionService.actualizarEstadoInversionLiquidadoPorNro(pedidoInversionSAF.getPedidoInversionNumero(), numeroLiquidacion, Constantes.Inversion.ESTADO_LIQUIDADO);
		}		
				
		return null;
	}
	
	private LiquidacionSAF crearRegistroLiquidacion(PedidoInversionSAF pedidoInversionSAF, Contrato contrato, String tipoDocuLiqu, double montoLiquidacion){
		LiquidacionSAF liquidacionSAF = new LiquidacionSAF();
		liquidacionSAF.setPedidoID(contrato.getPedidoId().intValue());
		liquidacionSAF.setPedidoInversionID(Integer.parseInt(pedidoInversionSAF.getPedidoInversionID()));
		liquidacionSAF.setLiquidacionTipo(Constantes.Liquidacion.LIQUI_TIPO_COMPRAS);
		liquidacionSAF.setProveedorID(Integer.parseInt(pedidoInversionSAF.getProveedorID()));
		liquidacionSAF.setLiquidacionTipoDocumento(tipoDocuLiqu);
		liquidacionSAF.setContratoID(contrato.getContratoId());
		liquidacionSAF.setMonedaID(Constantes.Liquidacion.MONEDA_DORALES_ID);
		liquidacionSAF.setLiquidacionImporte(montoLiquidacion);
		if(Constantes.Liquidacion.TIPO_DOCU_CONTRATO.equals(tipoDocuLiqu)){
			liquidacionSAF.setLiquidacionOrigen(Constantes.Liquidacion.LIQUI_ORIGEN_SISTEMA);
		}else{
			liquidacionSAF.setLiquidacionOrigen(Constantes.Liquidacion.LIQUI_ORIGEN_EMPRESA);
		}
		liquidacionSAF.setLiquidacionDestino(Constantes.Liquidacion.LIQUI_DESTINO_PROVEEDOR);
		liquidacionSAF.setLiquidacionEstado(Constantes.Liquidacion.LIQUI_ESTADO_CREADO);
		
		return liquidacionSAF;
	}

	@Override
	public String actualizarTablaContratosPedido(String nroPedido) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		
		List<Contrato> contratosPedidoActualizado = obtenerTablaContratosPedidoActualizado(nroPedido);
		
		if(contratosPedidoActualizado!=null && contratosPedidoActualizado.size()>0){
			for(Contrato contrato : contratosPedidoActualizado){
				contratoService.actualizarMontosDisponiblesCaspio(contrato.getNroContrato(), contrato.getMontoDisponible(),
						contrato.getDiferenciaPrecio(), contrato.getDiferenciaPrecioDisponible());
			}
		}
		
		return null;
	}

	@Override
	public String eliminarLiquidacionInversion(String nroInversion,
			String nroArmada, String usuarioId) throws Exception {
		String resultado = "";
		boolean existeLiquidacionArmada = false;
		// Obtener liquidaciones
		List<LiquidacionSAF> liquidacionesInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(nroInversion);
		
		// Obtener el estado de la liquidacion
		String nroArmadaId = "1";
		PedidoInversionSAF pedidoInversionSAF = null;
		if(liquidacionesInversion!=null && liquidacionesInversion.size()>0){
			// Obtener datos pedido-inversion SAF
			pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
			
			// Obtener datos de la inversion
			Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
			
			// Obtener nroArmadaId			
			if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())
					&& inversion.getServicioConstructora().booleanValue()==false){	
				nroArmadaId = String.valueOf(Integer.parseInt(nroArmada)+1);
			}
			
			for(LiquidacionSAF liquidacion : liquidacionesInversion){
				if(liquidacion.getNroArmada()==Integer.parseInt(nroArmadaId)){
					existeLiquidacionArmada = true;
					if(liquidacion.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_VB_CONTB)){
						resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
						break;
					}else if(liquidacion.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
						resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
						break;
					}
				}
			}
		}
		
		if(existeLiquidacionArmada==false){
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
		}
		
		if(resultado.equals("")){
			// Eliminar liquidacion
			liquidacionDao.eliminarLiquidacionInversionSAF(nroInversion, nroArmadaId, usuarioId);
			
			// Actualizar nro liquidacion en inversion
			inversionService.actualizarEstadoInversionLiquidadoPorNro(pedidoInversionSAF.getPedidoInversionNumero(), "", Constantes.Inversion.ESTADO_EN_PROCESO);
						
			// Actualizar montos de los contratos del pedido
			actualizarTablaContratosPedido(pedidoInversionSAF.getNroPedido());
		}
		
		return resultado;
	}

	@Override
	public String confirmarLiquidacionInversion(
			String nroInversion, String usuarioId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Obtener liquidaciones de la inversion
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(nroInversion);
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			System.out.println("liquidacionInversion: "+liquidacionInversion.size());
			String estadoLiquidacion="";
			for(LiquidacionSAF liquidacion : liquidacionInversion){
				estadoLiquidacion=liquidacion.getLiquidacionEstado();
			}
			System.out.println("ESTADO LIQUIDA: "+estadoLiquidacion);
			if(Util.esVacio(estadoLiquidacion)){
				resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
			}else if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_VB_CONTB)){
				resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
			}else if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
				resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
			}
		}else{
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
			System.out.println("liquidacionInversion:. NULL");
		}
		
		if(resultado.equals("")){
			// Confirmar liquidacion es SAF
			liquidacionDao.confirmarLiquidacionInversion(nroInversion, usuarioId);
			
			// Actualizar estado liquidacion Caspio
			inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, Constantes.Inversion.ESTADO_VB_CONTABLE);
		}
		
		return resultado;
	}
	
	public DetalleDiferenciaPrecio obtenerMontosDifPrecioInversion(String nroInversion) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos de inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		
		// Obtener datos del pedido
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
		
		// Obtener saldo diferencia precio
		DetalleDiferenciaPrecio ddp = contratoBusiness.obtenerMontoDiferenciaPrecio(pedido.getPedidoId());
		Double montoDiferenciaPrecio = Double.parseDouble(ddp.getDiferenciaPrecio());
		if(montoDiferenciaPrecio<0){		
			ddp.setDiferenciaPrecio(Util.getMontoFormateado(Double.parseDouble(ddp.getDiferenciaPrecio())));
			ddp.setImporteFinanciado(Util.getMontoFormateado(Double.parseDouble(ddp.getImporteFinanciado())));
			ddp.setSaldoDiferencia(Util.getMontoFormateado(Double.parseDouble(ddp.getSaldoDiferencia())));
			// Obtener monto pagado dif. precio			
			ddp.setMontoDifPrecioPagado(Util.getMontoFormateado(pedido.getCancelacionDiferenciaPrecioMonto()));
			ddp.setTipoInversion(inversion.getTipoInversion()+"-"+Util.obtenerBooleanString(inversion.getServicioConstructora()));
		}else{
			ddp = null;
		}
		return ddp;
	}

	@Override
	public String actualizarDesembolsoCaspio(String nroInversion,
			String nroArmada, String nroDesembolso) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		desembolsoService.setTokenCaspio(tokenCaspio);
		
		// Actualizar estado de la inversion
		inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, Constantes.Inversion.ESTADO_DESEMBOLSADO);
		
		// Obtener inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		String inversionId = String.valueOf(inversion.getInversionId().intValue());
		
		// Obtener desembolso
		Desembolso desembolso = desembolsoService.obtenerDesembolsoPorInversionArmada(inversionId, nroArmada);
		
		// Registrar desembolso
		if(desembolso==null){
			desembolsoService.crearDesembolsoInversion(inversionId, nroArmada, nroDesembolso);
		}
		
		return null;
	}
	
	
	private boolean validacionConceptosLiquidacion(Inversion inversion) throws Exception{
		boolean resultado = false, conceptoNoPagado=false;
		
		// Obtener conceptos de liquidacion
		List<ConceptoLiquidacion> listaConceptos = liquidacionDao.obtenerConceptosLiquidacion(inversion.getNroInversion());
		if(listaConceptos!=null && listaConceptos.size()>0){
			for(ConceptoLiquidacion concepto : listaConceptos){
				if(Constantes.Liquidacion.CONCEPTO_SITUACION_PENDIENTE.equals(concepto.getSituacionID())){
					System.out.println("concepto no pagado: "+concepto.getConceptoNombre()+"-"+concepto.getSituacionID());
					conceptoNoPagado=true;
					break;
				}
			}
		}
		
		// Obtener cancelacion diferencia precio
		DetalleDiferenciaPrecio ddp = contratoBusiness.obtenerMontoDiferenciaPrecio(inversion.getPedidoId());
		if(ddp!=null){
			if(!ddp.getTipoInversion().equals("CONSTRUCCION-0")){
				Double montoDiferenciaPrecio = Double.parseDouble(ddp.getDiferenciaPrecio());
				System.out.println("montoDiferenciaPrecio:: "+montoDiferenciaPrecio);
				if(montoDiferenciaPrecio<0){
					double montoPagado = Double.parseDouble(ddp.getMontoDifPrecioPagado());
					if(montoPagado<montoDiferenciaPrecio){
						conceptoNoPagado=true;
					}
				}
			}
		}
		
		if(conceptoNoPagado==false){
			resultado=true;
		}
		
		return resultado;
	}
}
