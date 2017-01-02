package com.pandero.ws.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.UtilEnum;

@Controller
@RequestMapping("/inversionPedido")
public class InversionController {

	private static final Logger LOG = LoggerFactory.getLogger(InversionController.class);

	@Autowired
	InversionBusiness inversionBusiness;
	@Autowired
	LiquidacionBusiness liquidacionBusiness;
	@Autowired
	InversionService inversionService;
	
	@RequestMapping(value = "/obtenerInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> obtenerInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###obtenerInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
//			String inversionId = String.valueOf(params.get("inversionId"));
//			Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);		
//			if(inversion!=null){
//				detail = JsonUtil.toJson(inversion);
//			}
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/obtenerInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/confirmarInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> confirmarInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###confirmarInversion params:"+params);	
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String situacionConfirmado = String.valueOf(params.get("situacionConfirmado"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result  = inversionBusiness.confirmarInversion(inversionId,situacionConfirmado,usuarioId);		
		}catch(Exception e){
			LOG.error("Error inversion/confirmarInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}

	@RequestMapping(value = "/eliminarInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarInversion params:"+params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			inversionBusiness.eliminarInversion(inversionId);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/eliminarInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/registrarInversionRequisitos", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> registrarInversionRequisitos(@RequestBody Map<String, Object> params) {
		LOG.info("###registrarInversionRequisitos params:"+params);			
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			inversionBusiness.registrarInversionRequisitos(inversionId);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/registrarInversionRequisitos:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/anularVerificacion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularVerificacion(@RequestBody Map<String, Object> params) {
		LOG.info("###anularVerificacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			if(null!=params){
				if(null!=params.get("inversionId")){					
					String inversionId = String.valueOf(params.get("inversionId"));
					result = inversionBusiness.anularVerificacion(inversionId);
				}
			}			
		}catch(Exception e){
			LOG.error("Error inversion/anularVerificacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
			
		return response;
	}
	
	@RequestMapping(value = "/generarCartaObservacion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarCartaObservacion(@RequestBody Map<String, Object> params) {
		LOG.info("###generarCartaObservacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String inversionId = String.valueOf(params.get("inversionId"));
			result = inversionBusiness.generarCartaObservacion(inversionId, usuarioId);
			
		}catch(Exception e){
			LOG.error("Error inversion/generarCartaObservacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		
		return response;
	}
	
	@RequestMapping(value = "/generarLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> generarLiquidacionInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###generarLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));	
			result = liquidacionBusiness.generarLiquidacionPorInversion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/generarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
					
		return response;
	}
	
	@RequestMapping(value = "/anularLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularLiquidacionInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###anularLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));	
			result = liquidacionBusiness.eliminarLiquidacionInversion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/generarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/actualizarEstadoInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> actualizarEstadoInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###actualizarEstadoInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String estadoInversion = String.valueOf(params.get("estadoInversion"));
			result = inversionBusiness.actualizarEstadoInversionCaspioPorNro(nroInversion, estadoInversion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/actualizarEstadoInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/confirmarLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> confirmarLiquidacionInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###confirmarLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result = liquidacionBusiness.confirmarLiquidacionInversion(nroInversion, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/confirmarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}

	@RequestMapping(value = "obtenerImporteComprobante/{inversionNumero}/{nroArmada}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getImporteComprobantePath(@PathVariable(value="inversionNumero") String inversionNumero,@PathVariable(value="nroArmada") Integer nroArmada){
		LOG.info("###ContratoController.getImporteComprobante inversionNumero:"+inversionNumero+", nroArmada:"+nroArmada);
		ResultadoBean resultadoBean = null;
		//Obtener el id de inversion y con eso llamar a los comprbantes y por armada
		if(null!=inversionNumero && null!=nroArmada){
			try {
				resultadoBean=inversionBusiness.getImporteComprobante(inversionNumero,nroArmada);
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al obtener el importe de comprobante");
				LOG.error("###obtenerImporteComprobante:",e);
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "obtenerMontosDifPrecioInversion/{nroInversion}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> obtenerMontosDifPrecioInversion(@PathVariable(value="nroInversion") String nroInversion) {	
		LOG.info("###obtenerMontosDifPrecioInversion params: "+nroInversion);
		Map<String, Object> response = new HashMap<String, Object>();
		try{
			DetalleDiferenciaPrecio detalleDifPrecio = liquidacionBusiness.obtenerMontosDifPrecioInversion(nroInversion);
			if(detalleDifPrecio!=null){	
				response.put("result","1");
				response.put("diferenciaPrecio",detalleDifPrecio.getDiferenciaPrecio()==null?"0.00":detalleDifPrecio.getDiferenciaPrecio().replace(",", ""));
				response.put("importeFinanciado",detalleDifPrecio.getImporteFinanciado()==null?"0.00":detalleDifPrecio.getImporteFinanciado().replace(",", ""));
				response.put("saldoDiferencia",detalleDifPrecio.getSaldoDiferencia()==null?"0.00":detalleDifPrecio.getSaldoDiferencia().replace(",", ""));
				response.put("montoDifPrecioPagado",detalleDifPrecio.getMontoDifPrecioPagado()==null?"0.00":detalleDifPrecio.getMontoDifPrecioPagado().replace(",", ""));
				response.put("tipoInversion",detalleDifPrecio.getTipoInversion());
			}
		}catch(Exception e){
			LOG.error("Error inversion/obtenerMontosDifPrecioInversion:: ",e);
			e.printStackTrace();
			response.put("result","0");
			response.put("detail",e.getMessage());
		}
			
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
		
	//Serice recibe parametro InversionId y retorne la url
	@RequestMapping(value = "obtenerUrlCancelarComprobante/{inversionId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getURLCancelarComprobante(@PathVariable(value="inversionId") String inversionId){
		LOG.info("###ContratoController.getURLCancelarComprobante inversionId:"+inversionId);
		
		ResultadoBean resultadoBean = null;
		//Obtener el id de inversion y con eso llamar a los comprbantes y por armada
		if(null!=inversionId){
			try {
				
				String locationHref = inversionBusiness.getURLCancelarComprobante(inversionId);
				
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado(locationHref);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al obtener la url de cancelar comprobante");
				LOG.error("###getURLCancelarComprobante:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, el InversionId es null");
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "enviarCartaContabilidad/{inversionId}/{nroArmada}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean enviarCartaContabilidad(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,@PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.enviarCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada && null!=usuarioId){
			try {
				
				resultadoBean = inversionBusiness.enviarCargoContabilidad(inversionId, nroArmada, usuarioId);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al enviar carta contabilidad");
				LOG.error("###enviarCartaContabilidad:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "anularCartaContabilidad/{inversionId}/{nroArmada}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean anularCartaContabilidad(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,@PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.anularCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada && null!=usuarioId){
			try {
				
				resultadoBean = inversionBusiness.anularCargoContabilidad(inversionId, nroArmada, usuarioId);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al anular el envio de carta contabilidad");
				LOG.error("###enviarCartaContabilidad:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}
		
	@RequestMapping(value = "/recepcionarCargoContabilidad", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recepcionarCargoContabilidad(@RequestBody Map<String, Object> params) {	
		LOG.info("###recepcionarCargoContabilidad params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String fechaRecepcion = String.valueOf(params.get("fechaRecepcion"));
			String usuarioRecepcion = String.valueOf(params.get("usuarioRecepcion"));
			result = inversionBusiness.recepcionarCargoContabilidad(inversionId, nroArmada, fechaRecepcion, usuarioRecepcion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/recepcionarCargoContabilidad:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}
	
	@RequestMapping(value = "envioCargoContabilidadActualizSaldo/{inversionId}/{usuarioId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> envioCargoContabilidadActualizSaldo(@PathVariable(value="inversionId") String inversionId,@PathVariable(value="usuarioId") String usuarioId) {	
		LOG.info("###envioCargoContabilidadActualizSaldo inversionId:"+inversionId+", usuarioId:"+usuarioId);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			if(null!=inversionId && null!=usuarioId){
				result = inversionBusiness.envioCargoContabilidadActualizSaldo(inversionId, usuarioId);
				if(result.equals("")){
					result = Constantes.Service.RESULTADO_EXITOSO;
				}
			}else{
				result = Constantes.Service.RESULTADO_DATOS_PENDIENTES;
			}
			
		}catch(Exception e){
			LOG.error("Error inversion/envioCargoContabilidadActualizSaldo:: ",e);
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		LOG.info("RESPONSE: " +  response);			
		return response;
	}	

	@RequestMapping(value = "/recepcionarCargoContabilidadActualizSaldo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recepcionarCargoContabilidadActualizSaldo(@RequestBody Map<String, Object> params) {	
		LOG.info("###recepcionarCargoContabilidadActualizSaldo params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String fechaRecepcion = String.valueOf(params.get("fechaRecepcion"));
			String usuarioRecepcion = String.valueOf(params.get("usuarioRecepcion"));
			result = inversionBusiness.recepcionarCargoContabilidadActualizSaldo(inversionId, fechaRecepcion, usuarioRecepcion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/recepcionarCargoContabilidadActualizSaldo:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	

	@RequestMapping(value = "verificarRegistrarFacturas/{inversionId}/{nroArmada}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean verificarRegistrarFacturas(
	@PathVariable(value="inversionId") String inversionId,@PathVariable(value="nroArmada") String nroArmada){
		LOG.info("###ContratoController.verificarRegistrarFacturas inversionId:"+inversionId+", nroArmada:"+nroArmada);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada){
			try {
				
				resultadoBean = inversionBusiness.verificarRegistrarFacturas(inversionId, nroArmada);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al verificar si es posible registrar facturas");
				LOG.error("###verificarRegistrarFacturas:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "/obtenerUltimaLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> obtenerUltimaLiquidacionInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###recepcionarCargoContabilidadActualizSaldo params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			LiquidacionSAF liquidacionSAF = inversionBusiness.obtenerUltimaLiquidacionInversion(nroInversion);
			if(liquidacionSAF!=null){
				result = "1";
				detail = JsonUtil.toJson(liquidacionSAF);
			}
		}catch(Exception e){
			LOG.error("Error inversion/obtenerUltimaLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	
}
