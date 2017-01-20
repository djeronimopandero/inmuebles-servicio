package com.pandero.ws.business.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.Seguro;
import com.pandero.ws.business.GarantiaBusiness;
import com.pandero.ws.dao.GarantiaDao;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class GarantiaBusinessImpl implements GarantiaBusiness{
	
	private static final Logger LOG = LoggerFactory.getLogger(GarantiaBusinessImpl.class);
	
	@Autowired
	GarantiaDao garantiaDao;
	@Autowired
	GarantiaService garantiaService;
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	InversionService inversionService;

	@Override
	public String crearGarantiaSAF(String pedidoCaspioId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String uso, String usuarioId)
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
		garantia.setUsoBien(uso.equals("")?null:uso);
	
		// Crear garantia en el SAF		
		String garantiaSAFId = garantiaDao.crearGarantiaSAF(garantia, usuarioId);		
		
		return garantiaSAFId;
	}

	@Override
	public Map<String,Object> editarGarantiaSAFV2(String garantiaId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String montoPrima, String modalidad, String uso, String usuarioId, String nroContrato)
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
		garantia.setUsoBien((uso.equals("") || uso.equals("null"))?null:uso);
		garantia.setMontoPrima((montoPrima.equals("") || montoPrima.equals("null"))?null:montoPrima);
		garantia.setModalidad((modalidad.equals("") || modalidad.equals("null"))?null:modalidad);
		garantia.setNroContrato((nroContrato.equals("") || nroContrato.equals("null"))?null:nroContrato);
		System.out.println("datos: "+garantia.getFichaConstitucion()+ " - "+garantia.getFechaConstitucion()+" -"+montoPrima+"-"+modalidad);
		// Actualizar garantia en SAF
		Map<String,Object> out = garantiaDao.editarGarantiaSAFV2(garantia, usuarioId);
		
		return out;
	}
	
	@Override
	public String editarGarantiaSAF(String garantiaId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String montoPrima, String modalidad, String uso, String usuarioId, String nroContrato)
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
		garantia.setUsoBien((uso.equals("") || uso.equals("null"))?null:uso);
		garantia.setMontoPrima((montoPrima.equals("") || montoPrima.equals("null"))?null:montoPrima);
		garantia.setModalidad((modalidad.equals("") || modalidad.equals("null"))?null:modalidad);
		garantia.setNroContrato((nroContrato.equals("") || nroContrato.equals("null"))?null:nroContrato);
		System.out.println("datos: "+garantia.getFichaConstitucion()+ " - "+garantia.getFechaConstitucion()+" -"+montoPrima+"-"+modalidad);
		// Actualizar garantia en SAF
		garantiaDao.editarGarantiaSAF(garantia, usuarioId);
		
		return null;
	}

	@Override
	public String eliminarGarantia(String garantiaId, String usuarioId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		garantiaService.setTokenCaspio(tokenCaspio);
		inversionService.setTokenCaspio(tokenCaspio);
		
		String resultado="";
		// Obtener garantia por Id
		Garantia garantiaCaspio = garantiaService.obtenerGarantiaPorId(garantiaId);		
		if(garantiaCaspio!=null){
			System.out.println("garantiaCaspio.getGarantiaSAFId():: "+garantiaCaspio.getGarantiaSAFId());
		}
		
		// Obtener seguros por garantia
		List<Seguro> listaSeguros = garantiaService.obtenerSegurosPorGarantiaId(garantiaId);
		
		
		if(listaSeguros!=null && listaSeguros.size()>0){
			resultado = "Operación Cancelada. La Garantía cuenta con un seguro registrado.";
		}else{
			// Eliminar garantia en SAF
			String garantiaSAFId = String.valueOf(garantiaCaspio.getGarantiaSAFId().intValue());
			garantiaDao.eliminarGarantiaSAF(garantiaSAFId, usuarioId);
			
			// Eliminar garantia en Caspio
			garantiaService.eliminarGarantiaPorId(garantiaId);
			
			inversionService.actualizarInversionGarantiaHipotecado(garantiaCaspio);
		}
	
		return resultado;
	}
	
	
}
