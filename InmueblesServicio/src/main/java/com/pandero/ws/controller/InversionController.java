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

import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
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
	
	@RequestMapping(value = "solicitudDesembolso/{inversionNumero}", method = RequestMethod.GET)
	public @ResponseBody String solicitudDesembolso(@PathVariable(value="inversionNumero") String inversionNumero){
		LOG.info("###ContratoController.solicitudDesembolso inversionNumero:"+inversionNumero);
		String resultadoBean = null;
		if(null!=inversionNumero ){
			try {
				resultadoBean=inversionBusiness.generarDocumentoDesembolso(inversionNumero);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				return "ERROR";
			}
		}
		return resultadoBean;
	}
	
}
