package com.pandero.ws.business.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.business.GarantiaBusiness;
import com.pandero.ws.dao.GarantiaDao;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Service
public class GarantiaBusinessImpl implements GarantiaBusiness{
	
	private static final Logger LOG = LoggerFactory.getLogger(GarantiaBusinessImpl.class);
	
	@Autowired
	GarantiaDao garantiaDao;
	@Autowired
	GarantiaService garantiaService;
	@Autowired
	PedidoService pedidoService;

	@Override
	public String crearGarantiaSAF(String pedidoCaspioId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String montoPrima, String usuarioId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos del pedido
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(pedidoCaspioId);
		
		// Completar objeto garantia SAF		
		Garantia garantia = new Garantia();
		garantia.setPedidoNumero(pedido.getNroPedido());
		garantia.setPartidaRegistral(partidaRegistral.equals("")?null:partidaRegistral);		
		garantia.setFichaConstitucion(fichaConstitucion.equals("")?null:fichaConstitucion);
		garantia.setFechaConstitucion(fechaConstitucion.equals("")?null:fechaConstitucion);
		if(garantia.getFechaConstitucion()!=null){
			Date fechaConst = Util.convertirFechaStrADate(garantia.getFechaConstitucion(), "dd/MM/yyyy");
			garantia.setFechaConstitucion(Util.getDateFormat(fechaConst, "yyyy-MM-dd"));
		}
		garantia.setMontoPrima(montoPrima.equals("")?null:montoPrima);
		
		// Crear garantia en el SAF		
		String garantiaSAFId = garantiaDao.crearGarantiaSAF(garantia, usuarioId);		
		
		return garantiaSAFId;
	}

	@Override
	public String editarGarantiaSAF(String garantiaId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String montoPrima, String usuarioId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		garantiaService.setTokenCaspio(tokenCaspio);
		
		// Obtener garantia por Id
		Garantia garantiaCaspio = garantiaService.obtenerGarantiaPorId(garantiaId);
		
		if(garantiaCaspio!=null){
			System.out.println("garantiaCaspio.getGarantiaSAFId():: "+garantiaCaspio.getGarantiaSAFId());
		}		
				
		// Completar objeto garantia SAF
		Garantia garantia = new Garantia();
		garantia.setGarantiaSAFId(garantiaCaspio.getGarantiaSAFId());
		garantia.setPartidaRegistral((partidaRegistral.equals("")||partidaRegistral.equals("null"))?null:partidaRegistral);		
		garantia.setFichaConstitucion((fichaConstitucion.equals("")||fichaConstitucion.equals("null"))?null:fichaConstitucion);
		garantia.setFechaConstitucion((fechaConstitucion.equals("")||fechaConstitucion.equals("null"))?null:fechaConstitucion);
		if(garantia.getFechaConstitucion()!=null){
			Date fechaConst = Util.convertirFechaStrADate(garantia.getFechaConstitucion(), "dd/MM/yyyy");
			String nuevaFecha = Util.getDateFormat(fechaConst, "yyyy-MM-dd");
			garantia.setFechaConstitucion(nuevaFecha);
		}
		garantia.setMontoPrima((montoPrima.equals("") || montoPrima.equals("null"))?null:montoPrima);
		System.out.println("datos: "+garantia.getFichaConstitucion()+ " - "+garantia.getFechaConstitucion()+" -"+montoPrima+"-");
		// Actualizar garantia en SAF
		garantiaDao.editarGarantiaSAF(garantia, usuarioId);
		
		return null;
	}
	
	
}
