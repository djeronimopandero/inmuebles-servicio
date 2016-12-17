package com.pandero.ws.business.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;

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
		List<Contrato> listaContratosPedidoSAF = pedidoDao.obtenerContratosxPedido(nroPedido);
		
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
		
		// Obtener datos de la inversion
//		Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
		
		// Obtener valores pedido-contrato actualizado
//		List<Contrato> listaPedidoContrato = obtenerContratosPorPedidoActualizado(inversion.getn)
		
		
		return null;
	}
	
	private String generarLiquidacionArmada(String inversionId, int nroArmada){
		
		
		
		return null;
	}
}
