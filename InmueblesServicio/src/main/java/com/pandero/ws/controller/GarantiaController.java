package com.pandero.ws.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.business.GarantiaBusiness;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.UtilExcel;

@Controller
@RequestMapping("/garantia")
public class GarantiaController {

private static final Logger LOG = LoggerFactory.getLogger(GarantiaController.class);
	
	@Autowired
	GarantiaBusiness garantiaBusiness;
	
	@RequestMapping(value = "/crearGarantiaSAF", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> crearGarantiaSAF(@RequestBody Map<String, Object> params) {
		LOG.info("###crearGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoCaspioId = String.valueOf(params.get("pedidoCaspioId"));
			String partidaRegistral = String.valueOf(params.get("partidaRegistral"));
			String fichaConstitucion = String.valueOf(params.get("fichaConstitucion"));
			String fechaConstitucion = String.valueOf(params.get("fechaConstitucion"));
			String uso = String.valueOf(params.get("uso"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			LOG.info("datos: "+pedidoCaspioId+" - "+partidaRegistral+" - "+fichaConstitucion+" "+
					fechaConstitucion+" - "+usuarioId);
			
			result = garantiaBusiness.crearGarantiaSAF(pedidoCaspioId, partidaRegistral, fichaConstitucion, fechaConstitucion, uso, usuarioId);
					
		}catch(Exception e){
			LOG.error("Error pedido/crearGarantiaSAF:: ",e);
			e.printStackTrace();
			result="0";
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/registrarSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> registrarSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.registrarSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/registrarSeguro:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/generarSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.generarSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/generarSeguro:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/anularSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> anularSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.anularSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			response.put("mensaje", "El seguro se anuló satisfactoriamente");
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/renovarSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> renovarSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.renovarSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			response.put("mensaje", "El seguro se renovó satisfactoriamente");
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/editarGarantiaSAF", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> editarGarantiaSAF(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String garantiaId = String.valueOf(params.get("garantiaId"));
			String partidaRegistral = String.valueOf(params.get("partidaRegistral"));
			String fichaConstitucion = String.valueOf(params.get("fichaConstitucion"));
			String fechaConstitucion = String.valueOf(params.get("fechaConstitucion"));
			String uso = String.valueOf(params.get("uso"));
			String montoPrima = String.valueOf(params.get("montoPrima"));			
			String modalidad = String.valueOf(params.get("modalidad"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String nroContrato = String.valueOf(params.get("contratoNro"));
			System.out.println("modalidad="+modalidad+" - montoPrima="+montoPrima);
			
			garantiaBusiness.editarGarantiaSAF(garantiaId, partidaRegistral, fichaConstitucion, 
					fechaConstitucion, montoPrima, modalidad, uso, usuarioId, nroContrato);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/eliminarGarantia", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarGarantia(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarGarantia params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String garantiaId = String.valueOf(params.get("garantiaId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			result = garantiaBusiness.eliminarGarantia(garantiaId, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error pedido/eliminarGarantia:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	private static final int BUFFER_SIZE = 4096;
	@RequestMapping(value = "descargaDatosSeguro/{seguroId}", method = RequestMethod.GET)
	public @ResponseBody void descargaDatosSeguro(@PathVariable(value="seguroId") String seguroId, HttpServletRequest request,
            HttpServletResponse response){
		LOG.info("###.descargaDatosSeguro seguroId:"+seguroId);
		List<Map<String,Object>> result = null;
		if(null!=seguroId){
			try {
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("seguroId", seguroId);
				result = garantiaBusiness.obtenerDatosDescargaSeguro(params);
				File downloadFile = UtilExcel.createExcelFile(result, "seguros", "seguros.xlsx");
				FileInputStream inputStream = new FileInputStream(downloadFile);
		         
		        // get MIME type of the file
		        String mimeType =  "application/octet-stream";
		        System.out.println("MIME type: " + mimeType);
		 
		        // set content attributes for the response
		        response.setContentType(mimeType);
		        response.setContentLength((int) downloadFile.length());
		 
		        // set headers for the response
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"",
		                downloadFile.getName());
		        response.setHeader(headerKey, headerValue);
		 
		        // get output stream of the response
		        OutputStream outStream = response.getOutputStream();
		 
		        byte[] buffer = new byte[BUFFER_SIZE];
		        int bytesRead = -1;
		 
		        // write bytes read from the input stream into the output stream
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		 
		        inputStream.close();
		        outStream.close();
			} catch (Exception e) {
			}
		}
	}
}
