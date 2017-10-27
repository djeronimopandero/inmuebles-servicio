package com.pandero.ws.business.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilExcel;

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
	LiquidacionDao liquidacionDao;
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
	public Map<String, Object> registrarSeguro(Map<String, Object> params)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(params.get("fechaInicioVigencia")!=null){
			params.put("fechaInicioVigencia", sdf.parse(params.get("fechaInicioVigencia").toString()));
			params.put("fechaFinVigencia", sdf.parse(params.get("fechaFinVigencia").toString()));
		}		
		Map<String,Object> out = liquidacionDao.executeProcedure(params, "USP_CRE_registrarCreditoGarantia");		
		return out;
	}
	
	@Override
	public Map<String, Object> generarSeguro(Map<String, Object> params)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Map<String,Object> out = new HashMap<String, Object>();
		if(params.get("fechaInicioVigencia")!=null){
			params.put("fechaInicioVigencia", sdf.parse(params.get("fechaInicioVigencia").toString()));
			params.put("fechaFinVigencia", sdf.parse(params.get("fechaFinVigencia").toString()));
		}		
		if ("".equals(String.valueOf(params.get("seguroIdProcedencia")))
				|| params.get("seguroIdProcedencia") == null) {
			out = liquidacionDao.executeProcedure(params, "USP_CRE_generarSeguro");
		}else{
			out = liquidacionDao.executeProcedure(params, "USP_CRE_generarSeguro_procedencia");
		}
				
		return out;
	}
	
	
	@Override
	public Map<String, Object> anularSeguro(Map<String, Object> params)
			throws Exception {		
		Map<String,Object> out = liquidacionDao.executeProcedure(params, "USP_CRE_actualizarEstadoSeguro");	
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		garantiaService.setTokenCaspio(tokenCaspio);		
		garantiaService.anularSeguroCaspio(params);
		return out;
	}
	
	@Override
	public Map<String, Object> renovarSeguro(Map<String, Object> params)
			throws Exception {		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(params.get("fechaInicioVigencia")!=null){
			params.put("fechaInicioVigencia", sdf.parse(params.get("fechaInicioVigencia").toString()));
			params.put("fechaFinVigencia", sdf.parse(params.get("fechaFinVigencia").toString()));
		}
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		garantiaService.setTokenCaspio(tokenCaspio);	
		Map<String,Object> out = liquidacionDao.executeProcedure(params, "USP_CRE_renovarSeguro");	
		List resultset = (ArrayList) out.get("#result-set-1");
		Map<String,Object> map = (Map<String, Object>)resultset.get(0);
		params.putAll(map);
		garantiaService.renovarSeguroCaspio(params);
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
	
	@Override
	public List<Map<String,Object>> obtenerDatosDescargaSeguro(Map<String,Object> params)throws Exception{
		List<Map<String,Object>> resultado = new ArrayList<Map<String,Object>>();
		Map<String,Object> out = liquidacionDao.executeProcedure(params, "USP_CRE_obtenerDatosDescargaSeguroInmuebles");
		List lout = (List)out.get("#result-set-1");
		if(!lout.isEmpty()){
		resultado.add((Map<String,Object>)lout.get(0));
		}		
		return resultado;
	}
	
	public File obtenerArchivoDescargaSeguro(List<Map<String,Object>> result, Map<String,Object> params) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String nombreArchivo = "";
		String fechaIni = String.valueOf(params.get("fechaIni"));
		String fechaFin = String.valueOf(params.get("fechaFin"));
		if(!result.isEmpty()){
			Map<String,Object> seguro = result.get(0);
			nombreArchivo = "Trama_alta_" + seguro.get("NOMBRE") + "_" + seguro.get("NUMEROCONTRATO");
			seguro.remove("NUMEROCONTRATO");
			if("".equals(seguro.get("FECINIVIG"))){
				seguro.put("FECINIVIG", fechaIni.replaceAll("-", "/"));
				seguro.put("FECFINVIG", fechaFin.replaceAll("-", "/"));
				seguro.put("PRIMAANUAL", Double.parseDouble(String.valueOf(seguro.get("PRIMAANUAL"))) * getMonthsDifference(sdf.parse(fechaIni),sdf.parse(fechaFin)));
			}
		}
		
		return UtilExcel.createExcelFile(result, nombreArchivo, nombreArchivo + ".xlsx");
	}
	
	public static final int getMonthsDifference(Date dateFrom, Date dateTo) {
		Calendar calendarFrom = new GregorianCalendar();
		calendarFrom.setTime(dateFrom);
		
		Calendar calendarTo = new GregorianCalendar();
		calendarTo.setTime(dateTo);
		
	    int m1 = calendarFrom.get(Calendar.YEAR) * 12 + calendarFrom.get(Calendar.MONTH);
	    int m2 = calendarTo.get(Calendar.YEAR) * 12 + calendarTo.get(Calendar.MONTH);
	    return m2 - m1 + 1;
	}
	
}
