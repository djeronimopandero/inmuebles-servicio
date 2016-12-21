package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.ContratoService;
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
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	LiquidacionDao liquidacionDao;
	
	public List<Contrato> obtenerTablaContratosPedidoActualizado(String nroPedido) throws Exception{		
		// Obtener contratos del pedido
		System.out.println("NRO PEDIDO: "+nroPedido);
		List<Contrato> listaContratosPedidoSAF = pedidoDao.obtenerContratosxPedidoSAF(nroPedido);		
		if(listaContratosPedidoSAF==null){
			System.out.println("listaContratosPedidoSAF:: NULL");
		}else{
			System.out.println("listaContratosPedidoSAF: "+listaContratosPedidoSAF.size());
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
		
		// Obtener montos diponibles
		for(Contrato contrato : listaContratosPedidoSAF){
			contrato.setMontoDisponible(contrato.getMontoCertificado().doubleValue()-contrato.getMontoLiquidacionContrato().doubleValue());
			contrato.setDiferenciaPrecioDisponible(contrato.getDiferenciaPrecio().doubleValue()-contrato.getMontoLiquidacionDifPrecio().doubleValue());
			contrato.setTotalDisponible(contrato.getMontoDisponible().doubleValue()+contrato.getDiferenciaPrecioDisponible());
		}
		
		return listaContratosPedidoSAF;
	}
	
	@Override
	public String generarLiquidacionPorInversion(String nroInversion, String nroArmada, String usuarioId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		boolean validacionLiquidacion = true;
		
		// Obtener datos pedido-inversion SAF
		PedidoInversionSAF pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
		
		// Obtener valores pedido-contrato actualizado
		List<Contrato> listaPedidoContrato = obtenerTablaContratosPedidoActualizado(pedidoInversionSAF.getNroPedido());
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		
		// Verificar si existe monto disponible para la inversion
		double totalDisponible = obtenerTotalDisponibleEnPedido(listaPedidoContrato);
		
		// Obtener nroArmadaId
		String nroArmadaId = "1";
		if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){	
			nroArmada +=1;
		}
				
		if(validacionLiquidacion){
			// Obtener liquidaciones del pedido
			List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(nroInversion);
			String estadoLiquidacion = Util.obtenerEstadoLiquidacionPorNroArmada(liquidacionInversion, nroArmadaId);
			if(!estadoLiquidacion.equals("")){
				resultado = Constantes.Service.RESULTADO_EXISTE_LIQUIDACION;
				validacionLiquidacion = false;
			}
		}
		
		if(totalDisponible<inversion.getImporteInversion().doubleValue()){
			resultado = Constantes.Service.NO_MONTO_DISPONIBLE_LIQUIDAR;
			validacionLiquidacion = false;
		}
				
		if(validacionLiquidacion){
			// Obtener Armada x tipo de inversion
			double montoALiquidar = 0.00;
			if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){				
				// Obtener monto a liquidar
				List<Constante> listaArmadasDesemb = constanteService.obtenerListaArmadasDesembolso();
				double porcentajeArmada = Util.obtenerPorcentajeArmada(listaArmadasDesemb, nroArmadaId);
				System.out.println("inversion.getImporteInversion(): "+inversion.getImporteInversion()+" - porcentajeArmada: "+porcentajeArmada);
				montoALiquidar = inversion.getImporteInversion().doubleValue()*(porcentajeArmada/100);	
			}else{
				montoALiquidar = inversion.getImporteInversion();
			}
			System.out.println("montoALiquidar: "+montoALiquidar);
			
			// Generar liquidacion por monto
			generarLiquidacionPorMonto(pedidoInversionSAF, montoALiquidar, nroArmada, listaPedidoContrato, usuarioId);
		}
		
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
	
	private String generarLiquidacionPorMonto(PedidoInversionSAF pedidoInversionSAF, double montoALiquidar, String nroArmada,
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
				liquidacion.setNroArmada(Integer.parseInt(nroArmada));
				liquidacionDao.registrarLiquidacionInversionSAF(liquidacion,usuarioId);
			}
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
		if(liquidacionesInversion!=null && liquidacionesInversion.size()>0){
			// Obtener datos pedido-inversion SAF
			PedidoInversionSAF pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
			
			// Obtener nroArmadaId
			String nroArmadaId = "1";
			if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){	
				nroArmada +=1;
			}
			
			for(LiquidacionSAF liquidacion : liquidacionesInversion){
				if(liquidacion.getNroArmada()==Integer.parseInt(nroArmadaId)){
					existeLiquidacionArmada = true;
					if(!liquidacion.getLiquidacionEstado().equals("1")){
						resultado = Constantes.Service.RESULTADO_PASO_LIQUIDACION;
						break;
					}
				}
			}
		}
		
		if(existeLiquidacionArmada==false){
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
		}
		
		if(!resultado.equals("")){
			// Eliminar liquidacion
			liquidacionDao.eliminarLiquidacionInversionSAF(nroInversion, nroArmada, usuarioId);
		}
		
		return resultado;
	}
}
