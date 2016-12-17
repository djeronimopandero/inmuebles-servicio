package com.pandero.ws.business.impl;

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
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	LiquidacionDao liquidacionDao;
	
	public List<Contrato> obtenerContratosPorPedidoActualizado(String nroPedido) throws Exception{		
		// Obtener contratos del pedido
		List<Contrato> listaContratosPedidoSAF = pedidoDao.obtenerContratosxPedidoSAF(nroPedido);
		
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
			contrato.setDiferenciaPrecioDisponible(contrato.getDiferenciaPrecioDisponible().doubleValue()-contrato.getMontoLiquidacionDifPrecio().doubleValue());
			contrato.setTotalDisponible(contrato.getMontoDisponible().doubleValue()+contrato.getDiferenciaPrecioDisponible());
		}
		
		return listaContratosPedidoSAF;
	}
	
	@Override
	public String generarLiquidacionPorInversion(String nroInversion)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos pedido-inversion SAF
		PedidoInversionSAF pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
		
		// Obtener valores pedido-contrato actualizado
		List<Contrato> listaPedidoContrato = obtenerContratosPorPedidoActualizado(pedidoInversionSAF.getNroPedido());
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(nroInversion);
				
		// Obtener NroArmada
		int nroArmada = 0;
		if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){
			// Obtener liquidaciones del pedido
			List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionSAF(nroInversion);
			if(liquidacionInversion==null || liquidacionInversion.size()==0){
				nroArmada = 2;
			}else{
				int cantLiquidacion = liquidacionInversion.size();
				nroArmada = cantLiquidacion+2;
			}
		}else{
			nroArmada = 1;
		}
				
		// Obtener monto a liquidar
		double montoALiquidar = 0.00;
		if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){
			List<Constante> listaArmadasDesemb = constanteService.obtenerListaArmadasDesembolso();
			double porcentajeArmada = Util.obtenerPorcentajeArmada(listaArmadasDesemb, nroArmada);
			System.out.println("inversion.getImporteInversion(): "+inversion.getImporteInversion()+" - porcentajeArmada: "+porcentajeArmada);
			montoALiquidar = inversion.getImporteInversion().doubleValue()*(porcentajeArmada/100);			
		}else{
			montoALiquidar = inversion.getImporteInversion();
		}
		System.out.println("montoALiquidar: "+montoALiquidar);
		
		// Generar liquidacion por monto
		generarLiquidacionPorMonto(pedidoInversionSAF.getPedidoInversionID(), montoALiquidar, listaPedidoContrato);
		
		return null;
	}
	
	private String generarLiquidacionPorMonto(String pedidoInversionId, double montoALiquidar, List<Contrato> listaPedidoContrato){
		double montoLiquidado = 0.00;
		
		// Obtener el monto de los contratos
		for(Contrato contrato : listaPedidoContrato){
			if(montoALiquidar>montoLiquidado){
				if(contrato.getMontoDisponible().doubleValue()>0){
					
				}
			}else{
				break;
			}
		}
		
		// Obtener el monto de las diferencias de precio
		for(Contrato contrato : listaPedidoContrato){
			if(montoALiquidar>montoLiquidado){
				if(contrato.getDiferenciaPrecioDisponible().doubleValue()>0){
					
				}
			}else{
				break;
			}
		}
				
		return null;
	}
	
	private PedidoInversionSAF crearPedidoInversion(Integer contratoId){
		PedidoInversionSAF pedidoInversion = new PedidoInversionSAF();
		
		
		return pedidoInversion;
	}
}
