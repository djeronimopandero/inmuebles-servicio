package com.pandero.ws.business.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.ComprobanteCaspio;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisito;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.ObservacionInversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.bean.Usuario;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.GarantiaDao;
import com.pandero.ws.dao.GeneralDao;
import com.pandero.ws.dao.GenericDao;
import com.pandero.ws.dao.InversionDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.dao.UsuarioDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.GenericService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.LiquidDesembService;
import com.pandero.ws.service.MailService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentGenerator;
import com.pandero.ws.util.DocumentoUtil;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;
import com.pandero.ws.util.UtilEnum;
import com.thoughtworks.xstream.core.util.Base64Encoder;

@Component
public class InversionBusinessImpl implements InversionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);

	@Autowired
	InversionService inversionService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	PersonaDao personaDao;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	GarantiaDao garantiaDao;
	@Autowired
	MailService mailService;
	@Autowired
	LiquidacionDao liquidacionDao;
	@Autowired
	LiquidacionBusiness liquidacionBusiness;
	@Autowired
	LiquidDesembService liquidacionDesembolsoService;
	@Autowired
	GarantiaDao garantiaDAO;
	@Autowired
	GarantiaService garantiaService;
	@Autowired
	GenericService genericService;
	@Autowired
	GeneralDao generalDao;
	
	@Autowired
	GenericDao genericDao;
	
	@Autowired
	InversionDao inversionDao;
	
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${desarrollo.pandero.email}")
	private String emailDesarrolloPandero;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	
	
	public void enviarTest(String inversionId) throws Exception {
		String procedure = "USP_LOG_EnviarCorreo_RegistroSaldoDeuda_Inmuebles";
		//Aqui puedes probar los siguientes procedimientos
		//USP_EnviaCorreo_RegistroSaldoDeuda_Inmuebles
		//USP_EnviaCorreo_ComprobantePago_Inmuebles
		//USP_EnviaCorreo_Liquidacion_Inmuebles
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));

		enviarCorreoConfirmacion(pedido,inversion,null,procedure);
		
	}
		
	private Map<String,Object> enviarCorreoConfirmacion(Pedido pedido, Inversion inversion, Map<String,Object> params,String procedimiento) throws Exception{
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("NumeroPedido",pedido.getNroPedido());
		parameters.put("NumeroInversion",inversion.getNroInversion());
		parameters.put("TipoInversion",Constantes.TABLA_TIPO_INVERSION.get(inversion.getTipoInversion()));
		parameters.put("TipoInmueble",inversion.getTipoInmuebleNom());
		parameters.put("LibreGravamen",inversion.getGravamen());
		parameters.put("AreaTotal",inversion.getAreaTotal());
		parameters.put("PartidaRegistral",inversion.getPartidaRegistral());
		parameters.put("ImporteInversion",inversion.getImporteInversion());
		if(params!=null&&!params.isEmpty()){
			parameters.putAll(params);
		}
		parameters.put("ProcesoID","01134");
		return genericDao.executeProcedure(parameters, procedimiento);
	}
	
	@Override
	public void enviarCorreoVerificacion(String inversionId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
				
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("NumeroPedido",pedido.getNroPedido());
		parameters.put("NumeroInversion",inversion.getNroInversion());
		parameters.put("TipoInversion",Constantes.TABLA_TIPO_INVERSION.get(inversion.getTipoInversion()));
		parameters.put("TipoInmueble",inversion.getTipoInmuebleNom());
		parameters.put("LibreGravamen",inversion.getGravamen());
		parameters.put("AreaTotal",inversion.getAreaTotal());
		parameters.put("PartidaRegistral",inversion.getPartidaRegistral());
		parameters.put("ImporteInversion",inversion.getImporteInversion());
		parameters.put("ProcesoID","01134");
		genericDao.executeProcedure(parameters, "USP_LOG_EnviarCorreo_Verificacion_Inmuebles");	
	}
	
	@Override
	public String confirmarInversion(String inversionId, String situacionConfirmado, String usuarioId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		boolean cambiaInmuebleInversionHipotecado = false;
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
				
		// Confirmar Inversion
		if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
			System.out.println("inversion:: "+inversion);
			System.out.println("inversion.getTipoInversion(): "+inversion.getTipoInversion()+" - inversion.getPropietarioTipoDocId(): "+inversion.getPropietarioTipoDocId());
			// Obtener lista de documentos
			List<DocumentoRequisito> listaDocumentos = obtenerDocumentosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
			
			// Validar datos antes de confirmar
			String resultadoValidacion = DocumentoUtil.validarConfirmarInversion(listaDocumentos, inversion);
			if(!Util.esVacio(resultadoValidacion)){
				resultado=resultadoValidacion;
			}else{
				// Validar verificacion de requisitos
				boolean verificacionRequisitos = true;
				 List<InversionRequisito> listaRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
				 if(listaRequisitos!=null && listaRequisitos.size()>0){
					for(InversionRequisito requisito : listaRequisitos){
						if(!Constantes.DocumentoRequisito.ESTADO_REQUISITO_CONFORME.equals(requisito.getEstadoRequisito())){
							verificacionRequisitos=false;
						}
					}
				 }else{
					 verificacionRequisitos=false;
				 }
				 if(verificacionRequisitos==false) resultado=Constantes.Service.RESULTADO_PENDIENTE_REQUISITOS;
			}
			
			// Continuar si paso las validaciones
			String mensajeInmuebleInversionHipotecado="";
			if(Util.esVacio(resultado)){
				// Obtener datos del pedido	
				Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
				
				// Obtener proveedor SAF
				PersonaSAF proveedor = obtenerProveedor(inversion, pedido.getAsociadoId());
				
				//verificamos si el inmueble esta hipotecado
				System.out.println("INMUEBLES HIPOTECADO:: "+inversion.getInmuebleInversionHipotecado());
				if(inversion.getInmuebleInversionHipotecado()){					
					//garantiaDAO.crearCreditoGarantiaEvaluacionCrediticia(inversion.getNroInversion(), usuarioId);	
					
					// Obtener los contratos del pedido
					List<Contrato> listaContratos = pedidoDao.obtenerContratosxPedidoSAF(pedido.getNroPedido());
					String nroContrato = "";
					if(listaContratos!=null && listaContratos.size()>0){
						nroContrato = listaContratos.get(0).getNroContrato();				
					}
					System.out.println("nroContrato: "+nroContrato);
					
					// Obtener la evaluacion crediticia asociada a los contratos
					Map<String,Object> params = new HashMap<String, Object>();
					params.put("numeroContrato", nroContrato);
					Map<String,Object> result = contratoDao.obtenerContratosEvaluacionCrediticia(params);
					List<Map<String,Object>> contratosEvaluacionCrediticia = (List<Map<String,Object>>)result.get("#result-set-1");	
					
					// Si existe una evaluacion crediticia 
					if(contratosEvaluacionCrediticia!=null && contratosEvaluacionCrediticia.size()>0){
						System.out.println("si tiene evaluacion crediticia");
						boolean existePartidaRegistral = false;
						// Obtener el creditoID
						String creditoID = "";
						for(Map<String,Object> evaluacionCrediticia:contratosEvaluacionCrediticia){
							creditoID = evaluacionCrediticia.get("CreditoID").toString();
						}
						System.out.println("CREDITO-ID:: "+creditoID);
						// Obtener garantias de la evaluacion crediticia
						Map<String,Object> params2 = new HashMap<String, Object>();
						params2.put("CreditoID", creditoID);
						Map<String,Object> result2 = contratoDao.obtenerGarantiasEvaluacionCrediticia(params2);
						List<Map<String,Object>> garantiasEvaluacionCrediticia = (List<Map<String,Object>>)result2.get("#result-set-1");
						// Verficar si la partida se encuentra en la evaluacion crediticia
						if(garantiasEvaluacionCrediticia!=null && garantiasEvaluacionCrediticia.size()>0){
							System.out.println("si tiene garantias");
							String partidaRegistral = "";
							for(Map<String,Object> garantia:garantiasEvaluacionCrediticia){
								if("2".equals(garantia.get("TipoGarantiaID").toString())){
								partidaRegistral = garantia.get("PartidaRegistral").toString();
								System.out.println("partidas: "+inversion.getPartidaRegistral()+"-"+partidaRegistral);
								if(inversion.getPartidaRegistral().equals(partidaRegistral)){
									existePartidaRegistral = true;
								}
								}
								
							}
						}
						if(!existePartidaRegistral){
							System.out.println("no existe partida registral");
							inversion.setInmuebleInversionHipotecado(false);
							cambiaInmuebleInversionHipotecado = true;
						}
					}
				}
				
				// Registrar pedido-inversion SAF
				PedidoInversionSAF pedidoInversionSAF = new PedidoInversionSAF();
				pedidoInversionSAF.setNroPedido(pedido.getNroPedido());
				pedidoInversionSAF.setProveedorID(String.valueOf(proveedor.getProveedorID().intValue()));
				pedidoInversionSAF.setPedidoInversionNumero(inversion.getNroInversion());
				pedidoInversionSAF.setPedidoTipoInversionID(Util.obtenerTipoInversionID(inversion.getTipoInversion()));
				if(inversion.getServicioConstructora()){
					pedidoInversionSAF.setServicioConstructora("1");
				}else{
					pedidoInversionSAF.setServicioConstructora("");
				}
				if(inversion.getInmuebleInversionHipotecado()){
					pedidoInversionSAF.setInmuebleInversionHipotecado("1");
				}else
				{
					pedidoInversionSAF.setInmuebleInversionHipotecado("0");					
				}
				pedidoInversionSAF.setConfirmarID("1");
				pedidoInversionSAF.setUsuarioIDCreacion(usuarioId);	
				System.out.println("TIPO INMUEBLE: "+inversion.getTipoInmuebleId());
				pedidoInversionSAF.setTipoInmuebleId(Util.obtenerTipoInmuebleID(String.valueOf(inversion.getTipoInmuebleId())));
				pedidoInversionSAF.setMontoInversion(String.valueOf(inversion.getImporteInversion()));
				pedidoDao.agregarPedidoInversionSAF(pedidoInversionSAF);	
				enviarCorreoConfirmacionInmueble(pedido,inversion);
				// Sincroniza informacion de inmueble a SAF
				registrarInmuebleInversion(inversion, usuarioId);
				
			}
		}
		
		// Desconfirmar Inversion
		if(Constantes.Inversion.SITUACION_NO_CONFIRMADO.equals(situacionConfirmado)){
			Map<String,Object> inputParams = new HashMap<String,Object>();
			inputParams.put("nroInversion", inversion.getNroInversion());
			inputParams = genericDao.executeProcedure(inputParams, "USP_LOG_verificarSolicitudExcedente");
			if(Integer.parseInt(inputParams.get("resultado").toString())==0){
			// Validar verificacion de requisitos
			boolean verificacionRequisitos = true;
			List<InversionRequisito> listaRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
			if(listaRequisitos!=null && listaRequisitos.size()>0){
				for(InversionRequisito requisito : listaRequisitos){
					if(!Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE.equals(requisito.getEstadoRequisito())){
						verificacionRequisitos=false;
					}
				}
			}
			if(verificacionRequisitos==false) resultado=Constantes.Service.RESULTADO_TIENE_REQUISITOS;
			
			// Eliminar pedido-inversion en SAF
			if(Util.esVacio(resultado)){
				// Eliminar la garantia
				//garantiaDAO.eliminarCreditoGarantiaEvaluacionCrediticia(inversion.getNroInversion(), usuarioId);
				
				// Eliminar el pedido inversion
				pedidoDao.eliminarPedidoInversionSAF(inversion.getNroInversion(), usuarioId);
			}
				
			}else{
				resultado="EXCEDENTE";
			}
			
		}
		
		// Actualizar situacion confirmacion inversion en Caspio
		if(Util.esVacio(resultado)){		
			if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
				// Verificar si existe excedente certificado o diferencia de precio
				resultado=validarDiferenciaPrecioExcedenteEnInversion(inversionId, String.valueOf(inversion.getPedidoId()));
			}
			inversionService.actualizarSituacionConfirmadoInversionCaspio(inversionId, situacionConfirmado);

			// Desactivar indicar inmueble inversion hipotecado
			if(cambiaInmuebleInversionHipotecado){
				System.out.println("actualiza indicador inversion inmueble hipotecado");
				inversionService.actualizarIndicadorInmuebleHipotecadoInversionCaspio(inversionId, "0");
				resultado+=" "+Constantes.Service.RESULTADO_INMUEBLE_HIPOTECADO_NO;
			}
			if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(situacionConfirmado)){
				List<Inversion> listaInversionesCaspio = pedidoService.obtenerInversionesxPedidoCaspio(String.valueOf(inversion.getPedidoId()));
				resultado += "|inversionesConfirmadas";
				 if(listaInversionesCaspio!=null && listaInversionesCaspio.size()>0){
					 for(Inversion inversionVerificar : listaInversionesCaspio){
						 if(!Constantes.Inversion.SITUACION_CONFIRMADO.equals(inversionVerificar.getConfirmado())){
							 resultado = resultado.substring(0, resultado.indexOf("|"));
							 break;
						 }
					 }
				 }
			}
		}
							
		return resultado;
	}
	
	private void registrarInmuebleInversion(Inversion inversion, String usuarioId){
		try{
			LOG.error("EN registrarInmuebleInversion");
			// Obtener tipos de documento
			List<Constante> listaTiposDocuIden = constanteService.obtenerListaDocumentosIdentidad();
			// Obtener tipo de documento SAF
			String tipoDocuIdenSAF = Util.obtenerTipoDocuIdenSAFPorCaspioId(listaTiposDocuIden, inversion.getPropietarioTipoDocId());			
			inversion.setPropietarioTipoDocId(tipoDocuIdenSAF);
			
			pedidoDao.registrarInmuebleInversionSAF(inversion, usuarioId);
		}catch(Exception e){
			LOG.error("ERROR registrarInmuebleInversion: ", e);
		}
	}
	
	private PersonaSAF obtenerProveedor(Inversion inversion, Integer asociadoId) throws Exception{		
		Integer proveedorId = null, personaId = null;
		String tipoDocuProv="", nroDocuProv="", tipoProveedor="";
		// Obtener tipo de proveedor
		if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
			System.out.println("PROVEEDOR 1");
			tipoProveedor = Constantes.Proveedor.TIPO_ENTIDAD_FINANCIERA_COD;
			proveedorId = inversion.getEntidadFinancieraId();					
		}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
			if(inversion.getServicioConstructora()){
				System.out.println("PROVEEDOR 2");
				tipoProveedor = Constantes.Proveedor.TIPO_CONSTRUCTORA_COD;
				tipoDocuProv = inversion.getConstructoraTipoDoc();
				nroDocuProv = inversion.getConstructoraNroDoc();
			}else{
				System.out.println("PROVEEDOR 3");
				tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
				personaId = asociadoId;
			}
		}else{
			System.out.println("PROVEEDOR 4");
			tipoProveedor = Constantes.Proveedor.TIPO_PERSONA_COD;
			tipoDocuProv = inversion.getPropietarioTipoDocId();
			nroDocuProv = inversion.getPropietarioNroDoc();
		}
		// Obtener tipos de documento
		List<Constante> listaTiposDocuIden = constanteService.obtenerListaDocumentosIdentidad();

		// Obtener proveedor SAF	
		String tipoDocuIdenSAF = Util.obtenerTipoDocuIdenSAFPorCaspioId(listaTiposDocuIden, tipoDocuProv);
		PersonaSAF proveedor = personaDao.obtenerProveedorSAF(proveedorId, tipoProveedor, personaId, tipoDocuIdenSAF, nroDocuProv);
		if(proveedor==null){
			PersonaSAF proveedorSAF = new PersonaSAF();
			proveedorSAF.setTipoProveedor(tipoProveedor);
			proveedorSAF.setPersonaID(personaId);
			proveedorSAF.setTipoDocumentoID(tipoDocuIdenSAF);
			proveedorSAF.setPersonaCodigoDocumento(nroDocuProv);
			if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
					&& inversion.getServicioConstructora()){
				proveedorSAF.setNombre(inversion.getConstructoraNombres());
				proveedorSAF.setApellidoPaterno(inversion.getConstructoraApePaterno());
				proveedorSAF.setApellidoMaterno(inversion.getConstructoraApeMaterno());
				proveedorSAF.setRazonSocial(inversion.getConstructoraRazonSocial());
			}else if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
				proveedorSAF.setNombre(inversion.getPropietarioNombres());
				proveedorSAF.setApellidoPaterno(inversion.getPropietarioApePaterno());
				proveedorSAF.setApellidoMaterno(inversion.getPropietarioApeMaterno());
				proveedorSAF.setRazonSocial(inversion.getPropietarioRazonSocial());
			}					
			proveedor = personaDao.registrarProveedorSAF(proveedorSAF);
		}
		
		return proveedor;
	}
	
	private String validarDiferenciaPrecioExcedenteEnInversion(String inversionId, String pedidoId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		// Obtener inversiones del pedido
		List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoId);
		double montoTotalCertificados = 0, montoCertificadoUsado = 0, montoInversion = 0;		
		if(listaInversiones!=null && listaInversiones.size()>0){
			for(Inversion inversion : listaInversiones){
				System.out.println("inversion.getConfirmado():: "+inversion.getConfirmado());
				if(Constantes.Inversion.SITUACION_CONFIRMADO.equals(inversion.getConfirmado())){
					montoCertificadoUsado += inversion.getImporteInversion();
				}
				if(String.valueOf(inversion.getInversionId().intValue()).equals(inversionId)){
					montoInversion = inversion.getImporteInversion();
				}
			}
		}		
		
		// Obtener contratos del pedido
		List<Contrato> listaContratos= pedidoService.obtenerContratosxPedidoCaspio(pedidoId);
		if(listaContratos!=null && listaContratos.size()>0){
			for(Contrato contrato : listaContratos){
				montoTotalCertificados += contrato.getMontoCertificado();
			}
		}
		
		double montoCertificadoDisponible = montoTotalCertificados-montoCertificadoUsado;		
		double montoInversionRequerido = montoCertificadoDisponible-montoInversion;
		System.out.println("montoTotalCertificados:: "+montoTotalCertificados+" - montoCertificadoUsado:: "+montoCertificadoUsado);
		System.out.println("montoInversionRequerido:: "+montoInversionRequerido+" - montoInversion:: "+montoInversion);
		if(montoInversionRequerido>0){
			resultado=Constantes.Inversion.EXCEDENTE_CERTIFICADO;
		}else if(montoInversionRequerido<0){
			resultado=Constantes.Inversion.DIFERENCIA_PRECIO;
		}
		return resultado;
	}
	
	@Override
	public String eliminarInversion(String inversionId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		inversionService.actualizarEstadoInversionCaspio(inversionId, Constantes.Inversion.ESTADO_ANULADO);
		return null;
	}

	@Override
	public String registrarInversionRequisitos(String inversionId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		inversionService.setTokenCaspio(tokenCaspio);
		
		// Obtener requisitos de la inversion
		List<InversionRequisito> listaInversionRequisitos = inversionService.obtenerRequisitosPorInversion(inversionId);
		
		if(listaInversionRequisitos==null){
			// Obtener datos de la inversion
			Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
			// Obtener lista requisitos
			List<DocumentoRequisito> listaRequisitos = obtenerRequisitosTipoInversion(inversion.getTipoInversion(), inversion.getPropietarioTipoDocId());
			if(listaRequisitos!=null && listaRequisitos.size()>0){
				for(DocumentoRequisito requisito : listaRequisitos){
					// Crear el requisito de la inversion
					inversionService.crearRequisitoInversion(inversionId, String.valueOf(requisito.getRequisitoId().intValue()));
				}
			}
		}		
		return null;
	}
	
	private List<DocumentoRequisito> obtenerRequisitosTipoInversion(String tipoInversion, String tipoDocId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		
		List<DocumentoRequisito> listaRequisitos = new ArrayList<DocumentoRequisito>();
		// Obtener lista de documentos
		List<DocumentoRequisito> listaRequisitosTotal = constanteService.obtenerListaRequisitosPorTipoInversion(tipoInversion);		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversion)){
			for(DocumentoRequisito documentoRequisito : listaRequisitosTotal){
				String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(tipoDocId);
				if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
					listaRequisitos.add(documentoRequisito);
				}
			}
		}else{
			listaRequisitos = listaRequisitosTotal;
		}
		return listaRequisitos;
	}
	
	private List<DocumentoRequisito> obtenerDocumentosTipoInversion(String tipoInversion, String tipoDocId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		constanteService.setTokenCaspio(tokenCaspio);
		
		List<DocumentoRequisito> listaDocumentos = new ArrayList<DocumentoRequisito>();
		// Obtener lista de documentos
		List<DocumentoRequisito> listaDocumentosTotal = constanteService.obtenerListaDocumentosPorTipoInversion(tipoInversion);		
		// Obtener la lista de documentos por tipo persona
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(tipoInversion)){
			for(DocumentoRequisito documentoRequisito : listaDocumentosTotal){
				String propietarioTipoPersona = Util.getTipoPersonaPorDocIden(tipoDocId);
				if(documentoRequisito.getTipoPersona().equals(propietarioTipoPersona)){
					listaDocumentos.add(documentoRequisito);
				}
			}
		}else{
			listaDocumentos = listaDocumentosTotal;
		}
		return listaDocumentos;
	}	
	
	@Override
	public String anularVerificacion(String inversionId) throws Exception {
		String resultado = "";
		LOG.info("###anularVerificacion inversionId:"+inversionId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
				
		// Validar si existe liquidacion
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(inversion.getNroInversion());
		
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			resultado = Constantes.Service.RESULTADO_EXISTE_LIQUIDACION;
		}else{
			// Anular verificacion
			inversionService.actualizarEstadoInversionRequisitoCaspio(inversionId, Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE);
		}
		return resultado;
	}

	@Override
	public String generarCartaObservacion(String inversionId, String usuarioSAFId) throws Exception {
		LOG.info("###generarCartaObservacion inversionId:"+inversionId);
		
		String msg="";
		boolean verificacionExitosa=false;
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		boolean existePendientes=false;
		if(!StringUtils.isEmpty(inversionId)){
			 List<InversionRequisito> list= inversionService.obtenerRequisitosPorInversion(inversionId);
			 if(null!=list){
				 List<ObservacionInversion> listObs=new ArrayList<>();
				 for(InversionRequisito irc:list){
					 LOG.info("##irc.getEstadoRequisito():"+irc.getEstadoRequisito());
					 if(Constantes.DocumentoRequisito.ESTADO_REQUISITO_NO_CONFORME.equals(irc.getEstadoRequisito()!=null?irc.getEstadoRequisito():"")){
						 LOG.info("##Por agregar a la lista para el doc obs:"+irc.getObservacion());
						 ObservacionInversion obs=new ObservacionInversion();
						 obs.setObservacion(irc.getObservacion());
						 listObs.add(obs);
					 }
					 if(!existePendientes){
						 if(Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE.equals(irc.getEstadoRequisito()!=null?irc.getEstadoRequisito():"")){
							 existePendientes=true;
						 }
					 }
				 }
				 
				 if(existePendientes){
					 msg="No es posible generar la carta de validación por que existen requisitos PENDIENTES"; 
					 return msg;
				 }
				 
				 if(listObs.size()==0){
					 verificacionExitosa = true;
					 //Actualizar estado de la inversion como CONFORME
					 
					 msg="No es posible generar la carta de validación por que no existen requisitos con estado NO CONFORME"; 
//					 return msg;
				 }
				 
				String docxGenerado="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".docx";
				String pdfConvertido="Carta-de-validacion-de-datos-inversion-inmobiliaria-generado-"+inversionId+".pdf";
				String gestionInmobiliaria="gestion_inversion_inmobiliaria_desembolso";
				
				/*Generar documento*/
				DocumentoUtil documentoUtil=new DocumentoUtil();
				XWPFDocument docx = documentoUtil.openDocument(rutaDocumentosTemplates+"/"+gestionInmobiliaria+"/"+"Carta-de-validacion-de-datos-inversion-inmobiliaria-TEMPLATE.docx");
				
				/*Obtener datos de la inversion*/
				LOG.info("##Por obtener Inversion Caspio, inversionId:"+inversionId);
				Inversion pic= inversionService.obtenerInversionCaspioPorId(inversionId);
				
				LOG.info("##Obtener Pedido Caspio Por Id, pic.getPedidoId():"+pic.getPedidoId());
				Pedido pedidoCaspio= pedidoService.obtenerPedidoCaspioPorId(String.valueOf(pic.getPedidoId()));
				LOG.info("##Obtener Persona SAF, pedidoCaspio.getAsociadoId():"+pedidoCaspio.getAsociadoId());
				PersonaSAF personaSAF= personaDao.obtenerPersonaSAF(String.valueOf(pedidoCaspio.getAsociadoId()));
				
				LOG.info("##Obtener ContratoxPedido, pedidoCaspio.getPedidoId():"+pedidoCaspio.getPedidoId());
				List<Contrato> listContratos = pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoCaspio.getPedidoId()));
				//obtener primero contrato para buscar al funcionario de servicios
				Contrato contrato1 = listContratos.get(0);
				LOG.info("##Obtener Contrato SAF, contrato1.getNroContrato():"+contrato1.getNroContrato());
				ContratoSAF contratoSAF= contratoDao.obtenerContratoSAF(contrato1.getNroContrato());
				
				List<Parametro> params=new ArrayList<>();
				Parametro parametro1=new Parametro("$FechaDocumento", Util.getFechaFormateada(Util.getFechaActual(), Constantes.FORMATO_CARTA_VALIDACION_INVERSION) );//fecha
				Parametro parametro2=new Parametro("$NombreCompleto", personaSAF.getNombreCompleto());//asociado
				Parametro parametro3=new Parametro("$TipoInversion", StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion() );//tipo inversion
				Parametro parametro4=new Parametro("$TipoInmueble", StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom());//tipo inmbueble
				Parametro parametro5=new Parametro("$Importe", Util.getMontoFormateado(pic.getImporteInversion()));//Importe
				Parametro parametro7=new Parametro("$FuncionarioServYVentas", contratoSAF.getFuncionarioServicioyVentas());//Funcionario de servicios y ventas
				
				params.add(parametro1);
				params.add(parametro2);
				params.add(parametro3);
				params.add(parametro4);
				params.add(parametro5);
				params.add(parametro7);
				
				
				docx=DocumentoUtil.replaceTextCartaValidacion(docx,params,listObs);
				StringBuilder sb=new StringBuilder();
				documentoUtil.saveDocument(docx, sb.append(rutaDocumentosGenerados).append("/").append(docxGenerado).toString());
				
				/*Convertir a pdf*/
				sb=new StringBuilder();
				String strRutaGenerados=sb.append(rutaDocumentosGenerados).append("/").toString();
				DocumentoUtil.convertDocxToPdf(
						strRutaGenerados+docxGenerado,
						strRutaGenerados+pdfConvertido);
				
				
				/*Enviar correo con archivo adjunto*/
				LOG.info("SE GENERO LA CARTA DE OBSERVACION");
		        String asunto = "Resultado de verificación de inversión inmobiliaria - "+personaSAF.getNombreCompleto();
		        String speech = "";
		        boolean enviarArchivo=true;
		        String strNroContratos = Util.getContratosFromList(listContratos);
		        
		        if(verificacionExitosa){
		        	enviarArchivo=false;
		        	speech = DocumentoUtil.getHtmlCartaValidacionConforme(
		        			strNroContratos,
		        			personaSAF.getNombreCompleto(), 
		        			StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
		        			StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
		        			StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
		        			Util.getMontoFormateado(pic.getImporteInversion()), 
		        			Util.getMontoFormateado(pic.getAreaTotal()), 
		        			StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(),
		        			pic.getNroInversion());
		        	
		        }else{
		        	speech = DocumentoUtil.getHtmlCartaValidacionNoConforme(
		        			strNroContratos,
		        			personaSAF.getNombreCompleto(), 
		        			StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
		        			StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
		        			StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
		        			Util.getMontoFormateado(pic.getImporteInversion()), 
		        			Util.getMontoFormateado(pic.getAreaTotal()), 
		        			StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(),
		        			pic.getNroInversion(),
		        			listObs);
		        }
		        
		        Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
		        LOG.info("getCelulaCorreo:: "+usuario.getCelulaCorreo()+" - getEmpleadoCorreo: "+usuario.getEmpleadoCorreo());
		         
		         String emailTo = documentoEmailTo;
		         if(!Util.esVacio(usuario.getCelulaCorreo())){
		        	 emailTo = usuario.getCelulaCorreo();
		         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
		        	 emailTo = usuario.getEmpleadoCorreo();
		         }

		         LOG.info("##emailTo: "+emailTo);
		         LOG.info("##Enviar por correo archivo PDF: "+strRutaGenerados+pdfConvertido);
		         LOG.info("##enviarArchivo: "+enviarArchivo);
		         
		         EmailBean emailBean=new EmailBean();
		         emailBean.setEmailFrom(emailDesarrolloPandero);
		         emailBean.setEmailTo(emailTo);
		         emailBean.setSubject(asunto);
		         emailBean.setDocumento(pdfConvertido);
		         emailBean.setTextoEmail(speech);
		         emailBean.setFormatHtml(true);
		         emailBean.setEnviarArchivo(enviarArchivo);
		         mailService.sendMail(emailBean);
		         
		         if(!verificacionExitosa){
		        	 msg="Operación exitosa. Se ha emitido la carta de validación de datos de la inversión inmobiliaria"; 
		         }
				
			 }
		}
		return msg;
	}

	@Override
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion,
			String estadoInversion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, estadoInversion);
		
		return "";

	}
	
	public ResultadoBean getImporteComprobante(String inversionNumero, Integer nroArmada) throws Exception {
		LOG.info("###InversionBusinessImpl.getImporteComprobante inversionNumero:"+inversionNumero+", nroArmada:"+nroArmada);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		Double importe=0.00;
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(inversionNumero);
		
		
		if(null!=inversion){
			List<ComprobanteCaspio> listComprobante=inversionService.getComprobantes(inversion.getInversionId(), nroArmada);
			if(null!=listComprobante){
				for(ComprobanteCaspio comprobante:listComprobante){
					importe += (comprobante.getImporte()==null?0.00:comprobante.getImporte());
				}
			}
		}
		
		ResultadoBean rb=new ResultadoBean();
		rb.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		rb.setResultado(importe);
		
		return rb;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> generarDocumentoDesembolso(String nroInversion, String nroArmada, String usuarioSAFId)
			throws Exception {
		Map<String,Object> response = new HashMap<String,Object>();
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidacionDesembolsoService.setTokenCaspio(tokenCaspio);
		LOG.info("### InversionBusinessImpl.generarDocumentoDesembolso nroInversion:"+nroInversion+", nroArmada:"+nroArmada+", usuarioSAFId:"+usuarioSAFId);
		
		List<LiquidacionSAF> liquidaciones = liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion,nroArmada);
		
		if(liquidaciones==null || !"3".equals(liquidaciones.get(0).getLiquidacionEstado())){
			LOG.info("### liquidaciones es null o el estado de sul ultima liquidacion no es 3 DESEMBOLSADO");
			throw new Exception("Ocurrió un error. La liquidación no se encuentra desembolsada");
		}else{
			LOG.info("### Se procede a generar el doc de desembolso");
			LiquidacionSAF liquidacion = liquidaciones.get(0);
			String armada = Constantes.ARMADAS_DOC_DESEMBOLSO.get(liquidacion.getNroArmada());
			PedidoInversionSAF pedidoInversion = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
			List<Contrato> contratos =pedidoDao.obtenerContratosxPedidoSAF(pedidoInversion.getNroPedido());
			if(contratos!=null){
				Double dblImporteDesembolsoParcial=0.00;
				List<String> asociadosLiquidacion = new ArrayList<String>();
				String asociadosDocumento = "";
				String contratoDocumento = "";
				List<Asociado> asociados=new ArrayList<>();
				
				for(int i=0;i<contratos.size();i++){
					Contrato contrato = contratos.get(i);
					if(contratos.size()>0 && i+1<=contratos.size() && i>0){
						contratoDocumento+= ", ";
					}
					contratoDocumento+= contrato.getNroContrato();
				}
				
				asociados = contratoDao.obtenerAsociadosxContratoSAF(contratos
						.get(0).getNroContrato());

				for (int i = 0; i < asociados.size(); i++) {
					Asociado asociado = asociados.get(i);
					String asociadoImpresion = asociado.getNombreCompleto()
							+ " identificado con "
							+ asociado.getTipoDocumentoIdentidad() + " N° "
							+ asociado.getNroDocumentoIdentidad()
							+ " con domicilio en " + asociado.getDireccion()
							+ "|";
					if (asociadosLiquidacion.indexOf(asociadoImpresion) == -1) {
						asociadosLiquidacion.add(asociadoImpresion);
						asociadosDocumento += asociadoImpresion;

					}
				}
				
				String arr[] = asociadosDocumento.split("\\|");
				asociadosDocumento = "";
				for(int i=0;i<arr.length;i++){
					if(arr.length>0 && i+1==arr.length && i>0){
						asociadosDocumento+= " y ";
					}
					if(arr.length>0 && i+1<arr.length && i>0){
						asociadosDocumento+= ", ";
					}
					asociadosDocumento += arr[i];
					
				}
								
				Map<String,Object> parameters = new HashMap<String, Object>();
				parameters.put("PagoTesoreriaID", liquidacion.getLiquidacionPagoTesoreria());
				Map<String,Object> pagoTesoreria = liquidacionDao.executeProcedure(parameters, "USP_LOG_ReportePagoTesoreria_Dos");
				
				LOG.info("pago tesoreria: "+pagoTesoreria);
				LOG.info("asociadosLiquidacion: "+asociadosLiquidacion);
				
				String desembolso = "";
				if(!asociadosLiquidacion.isEmpty()){
					List<Map<String,Object>> data = (List<Map<String,Object>>)pagoTesoreria.get("#result-set-1");

					for(int i=0;i<data.size();i++){
						Map<String,Object> current=data.get(i);
						
						dblImporteDesembolsoParcial += (current.get("Importe")!=null?Double.parseDouble(current.get("Importe").toString()):0.00);
						
						if(i==asociados.size()-1){
							desembolso +=  " mediante " +  current.get("Tipo")  + " Nro. "  + current.get("NumeroReferencia") + ", la suma de US$ " +  current.get("Importe");
						}else{
							desembolso +=  " mediante " +  current.get("Tipo")  + " Nro. "  + current.get("NumeroReferencia") + ", la suma de US$ " +  current.get("Importe") + ", ";
						}
						
					}
				}
				
				DocumentoUtil documentoUtil=new DocumentoUtil();	
				XWPFDocument doc = documentoUtil.openDocument(rutaDocumentosTemplates+"/gestion_inversion_inmobiliaria_desembolso/Declaracion-Jurada-de-conformidad-de-desembolso-TEMPLATE.docx");
				if (doc != null) {
					List<Parametro> parametros = new ArrayList<Parametro>();
					parametros.add(new Parametro("$asociados", asociadosDocumento));
					parametros.add(new Parametro("$contratos", contratoDocumento));
					parametros.add(new Parametro("$desembolsos", desembolso));
					parametros.add(new Parametro("$armada", armada));
					DocumentoUtil.replaceParamsDocumentoDesembolso(doc, parametros,asociados);
					StringBuilder sb=new StringBuilder();
					String docGenerado = sb.append(rutaDocumentosGenerados).append("/").append("Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".doc").toString();
					documentoUtil.saveDocument(doc, docGenerado);
					File newPdf = new File(rutaDocumentosGenerados, "Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".pdf");
					DocumentGenerator.generatePdfFromOds(new File(docGenerado),newPdf);
						/*Enviar por correo*/
					 	Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
				        LOG.info("getCelulaCorreo:: "+usuario.getCelulaCorreo()+" - getEmpleadoCorreo: "+usuario.getEmpleadoCorreo());
				         
				         String emailTo = documentoEmailTo;
				         if(!Util.esVacio(usuario.getCelulaCorreo())){
				        	 emailTo = usuario.getCelulaCorreo();
				         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
				        	 emailTo = usuario.getEmpleadoCorreo();
				         }
				         
				        
				         
				         Inversion pic= inversionService.obtenerInversionCaspioPorNro(nroInversion);
				         
				         String speech = DocumentoUtil.getHtmlConstanciaDesembolsoParcial(
				        		 nroInversion,
				        		 contratoDocumento,
				        		 asociados.get(0).getNombreCompleto(), 
				        		 StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(), 
				        		 StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
				        		 StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
				        		 Util.getMontoFormateado(pic.getAreaTotal()), 
				        		 StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
				        		 Util.getMontoFormateado(pic.getImporteInversion()), 
				        		 Util.getMontoFormateado(dblImporteDesembolsoParcial));
				         
				         LOG.info("###emailTo :"+emailTo);
				         LOG.info("###speech :"+speech);
				         LOG.info("###docGenerado :"+"Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".docx");
				         
				         EmailBean emailBean=new EmailBean();
				         emailBean.setEmailFrom(emailDesarrolloPandero);
				         emailBean.setEmailTo(emailTo);
				         emailBean.setSubject("Constancia de desembolso parcial - "+asociados.get(0).getNombreCompleto());
				         emailBean.setDocumento("Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".docx");
				         emailBean.setTextoEmail(speech);
				         emailBean.setFormatHtml(true);
				         emailBean.setEnviarArchivo(true);
				         //mailService.sendMail(emailBean);
				         Map<String,Object> params = new HashMap<String,Object>();
				         params.put("InversionId", pic.getInversionId());
				         params.put("NroArmada", nroArmada);
				         params.put("ConstanciaGenerada", "1");
				         liquidacionDesembolsoService.setGenerarConstanciaInversioPedido(params);
							response.put("result", 1);
							response.put("detail", newPdf.getName()); 
				}
			}
		}
		return response;
	}

	@Override
	public String getURLCancelarComprobante(String inversionId) throws Exception {
	LOG.info("###InversionBusinessImpl.getURLCancelarComprobante inversionId:"+inversionId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String locationHref="";
		
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		if(null!=inversion){
			
			if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.CONSTRUCCION_COD)){
			    if(!inversion.getServicioConstructora()){//No
			      locationHref = "registrar-factura-proveedor.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			    }else{
			    	locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			    }
			 }else if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.CANCELACION_COD)){
				 locationHref = "registrar-actualizacion-saldo.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }else if(inversion.getTipoInversion().equalsIgnoreCase(Constantes.TipoInversion.ADQUISICION_COD) && inversion.getPropietarioTipoDocId().equals(String.valueOf(UtilEnum.TIPO_DOCUMENTO.RUC.getCodigoCaspio()))){
				 locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }else{
				 locationHref = "registrar-factura-proveedor-sin-armadas.aspx?NroInversion="+inversion.getNroInversion()+"&InversionId="+inversionId;
			 }
			
		}	
		return locationHref;
	}

	@Override
	public ResultadoBean enviarCargoContabilidad(String inversionId, String nroArmada, String usuario, String usuarioId)throws Exception {
		LOG.info("###InversionBusinessImpl.enviarCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
		boolean liquidacionAutomatica=false, liquidacionAutomaticaExitosa=false;
		String resultado = "";
		Double totalFacturas = 0.0;
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);	
		
		// Verificar si requiere registro de comprobantes
		boolean comprobanteEnviado = false;
		//boolean requiereDocumentos = Util.requiereRegistrarDocumento(inversion, nroArmada);
		//if(requiereDocumentos){
			// Obtener lista comprobantes
			List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
			if(listaComprobantes==null || listaComprobantes.size()==0){
				resultado=Constantes.Service.RESULTADO_SIN_COMPROBANTES;
			}else{				
				for(ComprobanteCaspio comprobante:listaComprobantes){
					if(!Util.esVacio(comprobante.getEnvioContabilidadFecha())){
						comprobanteEnviado = true;
						break;
					}
					totalFacturas +=comprobante.getImporte();
				}
			}
		//}
		
		// Verificar si ya se enviaron los documentos
		if(comprobanteEnviado){
			resultado = Constantes.Service.RESULTADO_OPERACION_YA_REGISTRADA;
		}				
		
		if(resultado.equals("")){			
			//La sumatoria de los comprobantes registrados debe ser igual al importe de la inversión (para ADQUSICIÓN PJ y CONSTRUCCIÓN CON CONSTRUCTORA)
			if(Constantes.TipoInversion.ADQUISICION_COD.equalsIgnoreCase(inversion.getTipoInversion()) || 
			(Constantes.TipoInversion.CONSTRUCCION_COD.equalsIgnoreCase(inversion.getTipoInversion()) && inversion.getServicioConstructora())){
				if(!inversion.getImporteInversion().equals(totalFacturas)){
					resultado = Constantes.Service.RESULTADO_ERROR_SUMA_COMPROBANTES_EXCEDE_INVERSION;
				}
			}
		}
			
		if(resultado.equals("")){		
			// Actualizar estado de envio a contabilidad
			Date date=Util.getFechaActual();
			String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
			inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,strFecha,usuario,UtilEnum.ESTADO_COMPROBANTE.ENVIADO.getTexto());
					
			// Obtener la ultima liquidacion
			LiquidacionSAF ultimaLiquidacion = obtenerUltimaLiquidacionInversion(inversion.getNroInversion());
			LOG.info("inversion.getTipoInversion():: "+inversion.getTipoInversion());
			// Si es construccion sin constructora
			if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
					&& !inversion.getServicioConstructora()){				
				if(ultimaLiquidacion!=null){
					int nroArmadaActual = ultimaLiquidacion.getNroArmada();
					LOG.info("nroArmadaActual:: "+nroArmadaActual);
					if(nroArmadaActual==2||nroArmadaActual==3){
						liquidacionAutomatica = true;
					}
					if(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO.equals(ultimaLiquidacion.getLiquidacionEstado())){
						liquidacionAutomatica = true;
						// Obtener monto de los comprobantes
						List<ComprobanteCaspio> comprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
						double totalComprobantes = 0;
						if(comprobantes!=null && comprobantes.size()>0){
							for(ComprobanteCaspio comprobante : comprobantes){
								totalComprobantes += (comprobante.getImporte()==null?0.00:comprobante.getImporte().doubleValue());
							}								
						}
						// Obtener monto del desembolso
						double montoDesembolso = ultimaLiquidacion.getLiquidacionImporte();
						// Si se va a generar la ultima liquidacion
						if(nroArmadaActual==3){
							// Obtener el monto total
							double montoTotal = montoDesembolso/(Constantes.GenLista.ARMADA_3);
							montoDesembolso = montoTotal*Constantes.GenLista.ARMADA_2; // Se obtiene el otro 50%
						}
						
						// Obtener monto minimo desembolso
						double montoMinimoDesembolso = montoDesembolso*Constantes.Liquidacion.PORCENTAJE_MIN_DESEMBOLSO;
						LOG.info("totalComprobantes:: "+totalComprobantes+ " - montoMinimoDesembolso:: "+montoMinimoDesembolso);
						if(totalComprobantes>=montoMinimoDesembolso){
							// Generar siguiente liquidacion
							int siguienteArmada=nroArmadaActual+1;
							LOG.info("LIQUIDACION AUTOMATICA - NRO:: "+siguienteArmada);
							String resultLiquidacion = liquidacionBusiness.generarLiquidacionPorInversion(inversion.getNroInversion(), String.valueOf(siguienteArmada), usuarioId);
							LOG.info("RESULTADO LIQU AUTOMATICA: "+resultLiquidacion);
							if(resultLiquidacion.equals("")){
								liquidacionAutomaticaExitosa = true;
							}
						}					
					}
				}
			}
			
			if(liquidacionAutomaticaExitosa){
				resultado = "Se enviaron los documentos a contabilidad y se generó la liquidación automática.";
			}else if(liquidacionAutomatica){
				resultado = "Se enviaron los documentos a contabilidad, pero no se generó la liquidación automática.";
			}else{
				resultado = "Se enviaron los documentos a contabilidad";
			}
		}
		LOG.info("RESULTADO ENVIO CONTABILIDAD: "+resultado);
		
		// Enviar respuesta
		resultadoBean = new ResultadoBean();
		resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		resultadoBean.setResultado(resultado);
	
		return resultadoBean;
	}

	@Override
	public ResultadoBean anularCargoContabilidad(String inversionId, String nroArmada, String usuarioId)
			throws Exception {
		LOG.info("###InversionBusinessImpl.anularCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		
		List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
		if(listaComprobantes!=null && listaComprobantes.size()>0){
			for(ComprobanteCaspio comprobante : listaComprobantes){
				if(!Util.esVacio(comprobante.getRecepContabilidadFecha())){
					resultado = Constantes.Service.RESULTADO_EXISTE_RECEPCION_CARGO_CONTABILIDAD;
					break;
				}else if(Util.esVacio(comprobante.getEnvioContabilidadFecha())){
					resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
					break;
				}
			}
			if(resultado.equals("")){
				inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,"","","");
				resultado = "Se anuló el envío de documentos a contabilidad.";
			}
		}else{
			resultado = Constantes.Service.RESULTADO_SIN_COMPROBANTES;
		}
		
		ResultadoBean resultadoBean  = new ResultadoBean();
		resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		resultadoBean.setResultado(resultado);
		
		return resultadoBean;
	}

	@Override
	public String recepcionarCargoContabilidad(String inversionId,
			String nroArmada, String fechaRecepcion, String usuarioRecepcion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String resultado = "";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		// Verificar si requiere registro de comprobantes
		boolean requiereDocumentos = Util.requiereRegistrarDocumento(inversion, nroArmada);
		if(requiereDocumentos){
			// Obtener lista de comprobantes
			List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
			
			if(listaComprobantes!=null && listaComprobantes.size()>0){
				boolean documentoEnviado = false;
				boolean documentoRecepcionado = false;
				for(ComprobanteCaspio comprobante : listaComprobantes){
					if(!Util.esVacio(comprobante.getRecepContabilidadFecha())){
						documentoRecepcionado = true;
						break;
					}
					if(!Util.esVacio(comprobante.getEnvioContabilidadFecha())){
						documentoEnviado=true;
						break;
					}
				}
				if(documentoRecepcionado){
					resultado = Constantes.Service.RESULTADO_OPERACION_YA_REGISTRADA;
				}else{
					if(documentoEnviado){
						// Recepcionar cargo
						inversionService.recepcionarCargoContabilidad(inversionId, nroArmada, fechaRecepcion, usuarioRecepcion);
					}else{
						resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
					}
				}				
			}else{
				resultado = Constantes.Service.RESULTADO_SIN_COMPROBANTES;
			}			
		}else{
			resultado=Constantes.Service.RESULTADO_NO_REQUIERE_COMPROBANTES;
		}
		
		return resultado;
	}
	
	public String anularRecepcionCargoContabilidad(String inversionId,
			String nroArmada, String usuario) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String resultado = "";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
				
		// Obtener lista de comprobantes
		List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
				
		if(listaComprobantes!=null && listaComprobantes.size()>0){
			System.out.println("COMPROBANTES: "+listaComprobantes.size());
			boolean envioDocumentos = false;
			for(ComprobanteCaspio comprobante : listaComprobantes){
				if(Util.esVacio(comprobante.getRecepContabilidadFecha())){
					resultado=Constantes.Service.RESULTADO_SIN_RECEPCION_CARGO_CONTABILIDAD;
					break;
				}
			}
			
			// Obtener liquidacion
			List<LiquidacionSAF> listaLiquidacion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(inversion.getNroLiquidacion());
			if(listaLiquidacion!=null && listaLiquidacion.size()>0){
				// Obtener el nroArmada
				int nroArmadaLiquidacion = Integer.parseInt(nroArmada);
				if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
						&& !inversion.getServicioConstructora()){
					nroArmadaLiquidacion = nroArmadaLiquidacion+1;
				}				
				for(LiquidacionSAF liquidacion : listaLiquidacion){
					if(liquidacion.getNroArmada()==nroArmadaLiquidacion){
						if(liquidacion.getLiquidacionEstado().equals("2")){
							resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
							break;
						}else if(liquidacion.getLiquidacionEstado().equals("3")){
							resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
							break;
						}
					}
				}
			}
			
			if(resultado.equals("")){
				// Anular recepcion cargo
				inversionService.anularRecepcionCargoContabilidad(inversionId, nroArmada, usuario);
			}
		}else{
			resultado = Constantes.Service.RESULTADO_SIN_COMPROBANTES;
		}
		
		return resultado;
	}

	@Override
	public String envioCargoContabilidadActualizSaldo(String inversionId, String usuarioEnvio) throws Exception {
		LOG.info("###envioCargoContabilidadActualizSaldo inversionId:"+inversionId+", usuarioEnvio:"+usuarioEnvio);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		String resultado="";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		// Verificar si ya se enviaron los documentos
		if(!Util.esVacio(inversion.getEnvioContabilidadFecha())){
			resultado = Constantes.Service.RESULTADO_OPERACION_YA_REGISTRADA;
		}else{		
			// Verificar si registro la actualizacion de saldo de deuda
			if(inversion.getImporteInversionInicial()!=null && !Util.esVacio(inversion.getFechaActualizacionSaldo())){
				// Enviar cargo contabilidad
				Date date=Util.getFechaActual();
				String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
				inversionService.envioCargoContabilidadActualizSaldo(inversionId, strFecha, usuarioEnvio);
			}else{
				resultado = Constantes.Service.RESULTADO_SIN_ACTUALZ_SALDO_DEUDA;
			}
		}
		
		return resultado;
	}

	@Override
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,
			String fechaRecepcion, String usuarioRecepcion) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String resultado = "";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		// Validar si ya se registro la recepcion 
		if(!Util.esVacio(inversion.getRecepContabilidadFecha())){
			resultado = Constantes.Service.RESULTADO_OPERACION_YA_REGISTRADA;
		}else{
			// Validar si se enviaron los documentos
			if(!Util.esVacio(inversion.getEnvioContabilidadFecha())){			
				// Recepcionar cargo contabilidad
				inversionService.recepcionarCargoContabilidadActualizSaldo(inversionId, fechaRecepcion, usuarioRecepcion);		
			}else{
				resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
			}
		}
		
		return resultado;
	}
	
	@Override
	public String anularRecepcionCargoContabilidadActualizSaldo(String inversionId, String usuario) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		String resultado = "";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		// Validar si se enviaron los documentos
		if(!Util.esVacio(inversion.getRecepContabilidadFecha())){			
			// Obtener liquidacion
			List<LiquidacionSAF> listaLiquidacion = liquidacionDao.obtenerLiquidacionPorInversionArmada(inversion.getNroInversion(), "1");
			if(listaLiquidacion!=null && listaLiquidacion.size()>0){			
				for(LiquidacionSAF liquidacion : listaLiquidacion){					
					if(liquidacion.getLiquidacionEstado().equals("2")){
						resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
						break;
					}else if(liquidacion.getLiquidacionEstado().equals("3")){
						resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
						break;
					}
				}
			}			
			if(resultado.equals("")){
				// Recepcionar cargo contabilidad
				inversionService.recepcionarCargoContabilidadActualizSaldo(inversionId, "", "");
			}
		}else{
			resultado = Constantes.Service.RESULTADO_SIN_RECEPCION_CARGO_CONTABILIDAD;
		}
		return resultado;
	}
	
	@Override
	public String anularEnvioCargoContabilidadActualizSaldo(String inversionId,String usuario) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		if(Util.esVacio(inversion.getEnvioContabilidadFecha())){
			resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
		}else{
			if(!Util.esVacio(inversion.getRecepContabilidadFecha())){
				resultado = Constantes.Service.RESULTADO_EXISTE_RECEPCION_CARGO_CONTABILIDAD;
			}else{
				// Anular envio cargo contabilidad
				inversionService.envioCargoContabilidadActualizSaldo(inversionId, null, null);
			}
		}
		
		return resultado;
	}
	
	public ResultadoBean verificarRegistrarFacturas(String inversionId, String nroArmada) throws Exception {
		LOG.info("###InversionBusinessImpl.verificarRegistrarFacturas inversionId:"+inversionId+", nroArmada:"+nroArmada);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
		
		/*****/
		//Para el tipo de inversión ADQUISICIÓN PJ y CONSTRUCCIÓN CON CONSTRUCTORA se debe considerar la siguiente validación: 
		//(i) No se debe permitir registrar el comprobante del proveedor de la inversión si no se encuentra CONFIRMADA
		
		Inversion inversion= inversionService.obtenerInversionCaspioPorId(inversionId);
		if(null!=inversion){
			LOG.info("###Estado:"+inversion.getEstado());
			LOG.info("###Servicio Cons:"+inversion.getServicioConstructora());
			LOG.info("###Confirmado:"+inversion.getConfirmado());
			if(Constantes.TipoInversion.ADQUISICION_COD.equalsIgnoreCase(inversion.getTipoInversion()) || 
			(Constantes.TipoInversion.CONSTRUCCION_COD.equalsIgnoreCase(inversion.getTipoInversion()) && inversion.getServicioConstructora() )){
				if(!inversion.getConfirmado().equalsIgnoreCase(Constantes.Inversion.SITUACION_CONFIRMADO)){
					resultadoBean = new ResultadoBean();
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
					resultadoBean.setResultado("Operación cancelada. La inversión no ha sido confirmada.");
					return resultadoBean;
				}
			}
		}
		/*****/
		
		List<ComprobanteCaspio> listComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
		
		if(null!=listComprobantes){
			
			ComprobanteCaspio comprobanteCaspio = listComprobantes.get(0);
			
			if(comprobanteCaspio.getEstadoContabilidad().equalsIgnoreCase(UtilEnum.ESTADO_COMPROBANTE.ENVIADO.getTexto())){
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setResultado("Operación cancelada. Los documentos registrados ya han sido enviados a contabilidad, para registrar más comprobantes deberá anular el envío.");
			}else{
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado("Proceder al registro de facturas");
			}
			
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			resultadoBean.setResultado("Proceder al registro de facturas");
		}
		
		return resultadoBean;
	}

	@Override
	public ResultadoBean grabarComprobantes(String inversionId, String nroArmada, String usuarioId) throws Exception {
		LOG.info("###InversionBusinessImpl.grabarComprobantes inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean  = new ResultadoBean();
		boolean validarTotalMontoDesembolso=true;
		
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		if(null!=inversion){
			
			if(Constantes.TipoInversion.CONSTRUCCION_COD.equalsIgnoreCase(inversion.getTipoInversion())){
				if(!inversion.getServicioConstructora()){//Sin constructora
					//No validar
					validarTotalMontoDesembolso=false;
				}
			}
			
			if(validarTotalMontoDesembolso){
				//Validar que la suma de los comprobantes cubra el 100% para todas las inversiones menos SC
				Double totalComprobantes=0.00;
				List<ComprobanteCaspio> listComprobantes=inversionService.getComprobantes(Integer.parseInt(inversionId), Integer.parseInt(nroArmada));
				
				if(null!=listComprobantes){
					for(ComprobanteCaspio comprobante:listComprobantes){
						totalComprobantes+= (comprobante.getImporte()!=null?comprobante.getImporte():0.00);
					}
					
					LOG.info("## Suma de comprobantes de la inversion "+inversionId+", totalComprobantes :"+totalComprobantes);
					
					if(inversion.getImporteInversion()!=totalComprobantes){
						resultadoBean = new ResultadoBean();
						resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
						resultadoBean.setResultado("El total de comprobantes no cubre el monto desembolsado");
						return resultadoBean;
					}
				}else{
					resultadoBean = new ResultadoBean();
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
					resultadoBean.setResultado("El total de comprobantes no cubre el monto desembolsado");
					return resultadoBean;
				}
			}
			
			Date date=Util.getFechaActual();
			String strFecha = Util.getDateFormat(date, Constantes.FORMATO_DATE_YMD);
			LOG.info("##strFecha:"+strFecha);
			inversionService.actualizarComprobanteEnvioCartaContabilidad(inversionId,nroArmada,strFecha,usuarioId,UtilEnum.ESTADO_COMPROBANTE.GUARDADO.getTexto());
			
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			resultadoBean.setResultado("Se grabo el registro de comprobantes para inversionId:"+inversionId);
			
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("No existe la inversion:"+inversionId);
			return resultadoBean;
		}
		
		return resultadoBean;
	}

	public LiquidacionSAF obtenerUltimaLiquidacionInversion(String nroInversion)
			throws Exception {
		LiquidacionSAF ultimaLiquidacion = null;
		
		List<LiquidacionSAF> listaLiquidacion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(nroInversion);
		int ultimoNroArmada = obtenerUltimoNroArmada(listaLiquidacion);
		if(listaLiquidacion!=null && listaLiquidacion.size()>0){
			ultimaLiquidacion = new LiquidacionSAF();
			double liquidacionImporte = 0.00;
			for(LiquidacionSAF liquidacion : listaLiquidacion){
				if(liquidacion.getNroArmada()==ultimoNroArmada){
					ultimaLiquidacion.setLiquidacionEstado(liquidacion.getLiquidacionEstado());
					ultimaLiquidacion.setLiquidacionFecha(liquidacion.getLiquidacionFecha());
					ultimaLiquidacion.setLiquidacionFechaEstado(liquidacion.getLiquidacionFechaEstado());
					liquidacionImporte += liquidacion.getLiquidacionImporte().doubleValue();
					ultimaLiquidacion.setLiquidacionNumero(liquidacion.getLiquidacionNumero());
					ultimaLiquidacion.setNroArmada(liquidacion.getNroArmada());
				}
			}
			ultimaLiquidacion.setLiquidacionImporte(liquidacionImporte);
		}		
		return ultimaLiquidacion;
	}
	
	private int obtenerUltimoNroArmada(List<LiquidacionSAF> listaLiquidacion){
		int ultimoNroArmada=0;
		if(listaLiquidacion!=null && listaLiquidacion.size()>0){
			for(LiquidacionSAF liquidacion:  listaLiquidacion){
				if(liquidacion.getNroArmada()>ultimoNroArmada){
					ultimoNroArmada=liquidacion.getNroArmada();
				}
			}
		}
		
		return ultimoNroArmada;
	}
	
	public LiquidacionSAF obtenerUltimaLiquidacionInversionPorId(String inversionId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		LiquidacionSAF ultimaLiquidacion = null;
		
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		
		List<LiquidacionSAF> listaLiquidacion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(inversion.getNroInversion());
		int ultimoNroArmada = obtenerUltimoNroArmada(listaLiquidacion);
		if(listaLiquidacion!=null && listaLiquidacion.size()>0){
			ultimaLiquidacion = new LiquidacionSAF();
			double liquidacionImporte = 0.00;
			for(LiquidacionSAF liquidacion : listaLiquidacion){
				if(liquidacion.getNroArmada()==ultimoNroArmada){
					ultimaLiquidacion.setLiquidacionEstado(liquidacion.getLiquidacionEstado());
					ultimaLiquidacion.setLiquidacionFecha(liquidacion.getLiquidacionFecha());
					ultimaLiquidacion.setLiquidacionFechaEstado(liquidacion.getLiquidacionFechaEstado());
					liquidacionImporte += liquidacion.getLiquidacionImporte().doubleValue();
					ultimaLiquidacion.setLiquidacionNumero(liquidacion.getLiquidacionNumero());
					ultimaLiquidacion.setNroArmada(liquidacion.getNroArmada());
				}
			}
			ultimaLiquidacion.setLiquidacionImporte(liquidacionImporte);
		}		
		return ultimaLiquidacion;
	}
	
	@Override
	public LinkedHashMap<String,Object> getComprobanteResumen(String inversionNumero, Integer nroArmada) throws Exception {
		
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		Double importe=0.00;
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(inversionNumero);
		String fecha = "";
		
		if(null!=inversion){
			List<ComprobanteCaspio> listComprobante=inversionService.getComprobantes(inversion.getInversionId(), nroArmada);
			if(null!=listComprobante){
				for(ComprobanteCaspio comprobante:listComprobante){
					importe += (null!=comprobante.getImporte()?comprobante.getImporte().doubleValue():0);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
					Date d = sdf.parse(comprobante.getFechaCreacion());
					sdf.applyPattern("dd/MM/yyyy");
					fecha = sdf.format(d);
				}
			}
		}
		
		LinkedHashMap<String,Object> result = new LinkedHashMap<String,Object>();
		result.put("fecha", fecha);
		result.put("importe", importe);
		result.put("tipo", "COMPROBANTES ENTREGADOS");
		
		return result;
	}

	@Override
	public boolean validarImporteComprobantesNoExcedaInversion(String inversionId, Integer nroArmada, Double importeIngresar) throws Exception {
		LOG.info("###InversionBusinessImpl.validarImporteComprobantesNoExcedaInversion inversionId:"+inversionId);
	
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		Double dblImporteTotalComprobantes = 0.0;
		
		Inversion inversion= inversionService.obtenerInversionCaspioPorId(inversionId);
		
		if(Constantes.TipoInversion.CONSTRUCCION_COD.equalsIgnoreCase(inversion.getTipoInversion())){
			if(!inversion.getServicioConstructora()){//Sin constructora
				return true;
			}
		}
		
		
		List<ComprobanteCaspio> listComprobantes = inversionService.getComprobantes(Integer.parseInt(inversionId), nroArmada);
		if(listComprobantes!=null && listComprobantes.size()>0){
			for(ComprobanteCaspio comprobante:listComprobantes){
				dblImporteTotalComprobantes+=comprobante.getImporte();
			}
		}
		
		dblImporteTotalComprobantes = dblImporteTotalComprobantes + (importeIngresar==null?0:importeIngresar.doubleValue());
		
		LOG.info("###dblImporteTotalComprobantes:"+dblImporteTotalComprobantes);
		LOG.info("###Importe de inversion:"+inversion.getImporteInversion());
		
		if(dblImporteTotalComprobantes > inversion.getImporteInversion()){
			LOG.info("### dblImporteTotalComprobantes es mayor");
			return false; //Invalido
		}else{
			if(null!=importeIngresar){
				if(importeIngresar>inversion.getImporteInversion()){
					LOG.info("### validarImporteComprobantesNoExcedaInversion return false");
					return false;
				}else{
					LOG.info("### validarImporteComprobantesNoExcedaInversion return true");
					return true;//Invalido
				}
			}else{
				return false;
			}
		}
		
	}
	
	@Override
	public ResultadoBean actualizarInversionMonto(Map<String,Object> params)throws Exception{
		LOG.info("###actualizarInversionMonto, params:"+params);
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean= new ResultadoBean();
		resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
		
		if(null!=params){
			Map<String,Object> inputParams = new HashMap<String,Object>();
			inputParams.put("nroInversion", params.get("nroInversion").toString());
			inputParams = genericDao.executeProcedure(inputParams, "USP_LOG_verificarSolicitudExcedente");
			if(Integer.parseInt(inputParams.get("resultado").toString())==0){
			String nroInversion = params.get("nroInversion")!=null?params.get("nroInversion").toString():"0";
			List<LiquidacionSAF> list= liquidacionDao.obtenerLiquidacionesPorInversionSAF(nroInversion);
			System.out.println("###Se obtuvo la lista de liquidaciones.");
			if(null!=list){
				System.out.println("###Operación cancelada. La inversión ya ha sido liquidada.");
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setMensajeError("Operación cancelada. La inversión ya ha sido liquidada.");
			}else{
				System.out.println("###Por llamar al procedure USP_LOG_ActualizarMontoInversion");
				liquidacionDao.executeProcedure(params, "USP_LOG_ActualizarMontoInversion");
			}
		}else{
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setMensajeError("Operación cancelada. El pedido cuenta con una solicitud de excedente de certificado emitida y/o aplicada.");
			}
			
			
		}else{
			System.out.println("###Los parametros son nulos");
		}
		
		
		return resultadoBean;
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public String generarActaEntrega(String nroInversion, String usuarioSAFId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidacionDesembolsoService.setTokenCaspio(tokenCaspio);
		LOG.info("### InversionBusinessImpl.generarDocumentoDesembolso nroInversion:"+nroInversion+", usuarioSAFId:"+usuarioSAFId);
		
		List<LiquidacionSAF> liquidaciones = null; // liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion,nroArmada);
		
		if(liquidaciones==null || !"3".equals(liquidaciones.get(0).getLiquidacionEstado())){
			LOG.info("### liquidaciones es null o el estado de sul ultima liquidacion no es 3 DESEMBOLSADO");
			return "Ocurrió un error. La liquidación no se encuentra desembolsada";
		}else{
			LOG.info("### Se procede a generar el doc de desembolso");
			LiquidacionSAF liquidacion = liquidaciones.get(0);
			String armada = Constantes.ARMADAS_DOC_DESEMBOLSO.get(liquidacion.getNroArmada());
			PedidoInversionSAF pedidoInversion = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
			List<Contrato> contratos =pedidoDao.obtenerContratosxPedidoSAF(pedidoInversion.getNroPedido());
			if(contratos!=null){
				List<Contrato> contratosLiquidacion = new ArrayList<Contrato>();
				for(LiquidacionSAF liquidacionContrato:liquidaciones){
					for(Contrato contrato:contratos){
						if(liquidacionContrato.getContratoID().intValue()==contrato.getContratoId().intValue()){
							contratosLiquidacion.add(contrato);
							break;
						}
					}
				}
				
				Double dblImporteDesembolsoParcial=0.00;
				List<String> asociadosLiquidacion = new ArrayList<String>();
				String asociadosDocumento = "";
				String contratoDocumento = "";
				List<Asociado> asociados=new ArrayList<>();
				
				for(Contrato contrato:contratosLiquidacion){
					asociados = contratoDao.obtenerAsociadosxContratoSAF(contrato.getNroContrato());
					
					for(int i=0; i<asociados.size();i++){
						Asociado asociado = asociados.get(i);
						String asociadoImpresion = asociado.getNombreCompleto() + " identificado con " + asociado.getTipoDocumentoIdentidad() +  " N° " + asociado.getNroDocumentoIdentidad() + " con domicilio en " + asociado.getDireccion() + " y ";
						if(asociadosLiquidacion.indexOf(asociadoImpresion)==-1){
							asociadosLiquidacion.add(asociadoImpresion);
							asociadosDocumento+=asociadoImpresion;
							
							if(i==asociados.size()-1){
								contratoDocumento+= contrato.getNroContrato();
							}else{
								contratoDocumento+= contrato.getNroContrato() + ", ";
							}
							
						}
					}
				}
								
				Map<String,Object> parameters = new HashMap<String, Object>();
				parameters.put("PagoTesoreriaID", liquidacion.getLiquidacionPagoTesoreria());
				Map<String,Object> pagoTesoreria = liquidacionDao.executeProcedure(parameters, "USP_LOG_ReportePagoTesoreria_Dos");
				
				LOG.info("pago tesoreria: "+pagoTesoreria);
				LOG.info("asociadosLiquidacion: "+asociadosLiquidacion);
				
				String desembolso = "";
				if(!asociadosLiquidacion.isEmpty()){
					List<Map<String,Object>> data = (List<Map<String,Object>>)pagoTesoreria.get("#result-set-1");
//					for(Map<String,Object> current:data){
					for(int i=0;i<data.size();i++){
						Map<String,Object> current=data.get(i);
						
						dblImporteDesembolsoParcial += (current.get("Importe")!=null?Double.parseDouble(current.get("Importe").toString()):0.00);
						
						if(i==asociados.size()-1){
							desembolso +=  " mediante " +  current.get("Tipo")  + " Nro. "  + current.get("NumeroReferencia") + ", la suma de US$ " +  current.get("Importe");
						}else{
							desembolso +=  " mediante " +  current.get("Tipo")  + " Nro. "  + current.get("NumeroReferencia") + ", la suma de US$ " +  current.get("Importe") + ", ";
						}
						
					}
				}
				
				DocumentoUtil documentoUtil=new DocumentoUtil();	
				XWPFDocument doc = documentoUtil.openDocument(rutaDocumentosTemplates+"/gestion_inversion_inmobiliaria_desembolso/Declaracion-Jurada-de-conformidad-de-desembolso-TEMPLATE.docx");
				if (doc != null) {
					List<Parametro> parametros = new ArrayList<Parametro>();
					parametros.add(new Parametro("$asociados", asociadosDocumento));
					parametros.add(new Parametro("$contratos", contratoDocumento));
					parametros.add(new Parametro("$desembolsos", desembolso));
					parametros.add(new Parametro("$armada", armada));
					DocumentoUtil.replaceParamsDocumentoDesembolso(doc, parametros,asociados);
					StringBuilder sb=new StringBuilder();
					String docGenerado = sb.append(rutaDocumentosGenerados).append("/").append("Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".docx").toString();
					documentoUtil.saveDocument(doc, docGenerado);

						/*Enviar por correo*/
					 	Usuario usuario = usuarioDao.obtenerCorreoUsuarioCelula(usuarioSAFId);
				        LOG.info("getCelulaCorreo:: "+usuario.getCelulaCorreo()+" - getEmpleadoCorreo: "+usuario.getEmpleadoCorreo());
				         
				         String emailTo = documentoEmailTo;
				         if(!Util.esVacio(usuario.getCelulaCorreo())){
				        	 emailTo = usuario.getCelulaCorreo();
				         }else if(!Util.esVacio(usuario.getEmpleadoCorreo())){
				        	 emailTo = usuario.getEmpleadoCorreo();
				         }
				         
				         String strNroContratos = Util.getContratosFromList(contratosLiquidacion);
				         
				         Inversion pic= inversionService.obtenerInversionCaspioPorNro(nroInversion);
				         
				         String speech = DocumentoUtil.getHtmlConstanciaDesembolsoParcial(
				        		 nroInversion,
				        		 strNroContratos,
				        		 asociados.get(0).getNombreCompleto(), 
				        		 StringUtils.isEmpty(pic.getTipoInversion())?"":pic.getTipoInversion(), 
				        		 StringUtils.isEmpty(pic.getTipoInmuebleNom())?"":pic.getTipoInmuebleNom(), 
				        		 StringUtils.isEmpty(pic.getGravamen())?"":pic.getGravamen(), 
				        		 Util.getMontoFormateado(pic.getAreaTotal()), 
				        		 StringUtils.isEmpty(pic.getPartidaRegistral())?"":pic.getPartidaRegistral(), 
				        		 Util.getMontoFormateado(pic.getImporteInversion()), 
				        		 Util.getMontoFormateado(dblImporteDesembolsoParcial));
				         
				         LOG.info("###emailTo :"+emailTo);
				         LOG.info("###speech :"+speech);
				         LOG.info("###docGenerado :"+"Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".docx");
				         
				         EmailBean emailBean=new EmailBean();
				         emailBean.setEmailFrom(emailDesarrolloPandero);
				         emailBean.setEmailTo(emailTo);
				         emailBean.setSubject("Constancia de desembolso parcial - "+asociados.get(0).getNombreCompleto());
				         emailBean.setDocumento("Declaracion-Jurada-conformidad-desembolso-generado-"+nroInversion+".docx");
				         emailBean.setTextoEmail(speech);
				         emailBean.setFormatHtml(true);
				         emailBean.setEnviarArchivo(true);
				         mailService.sendMail(emailBean);
				         Map<String,Object> params = new HashMap<String,Object>();
				         params.put("InversionId", pic.getInversionId());
				        // params.put("NroArmada", nroArmada);
				         params.put("ConstanciaGenerada", "1");
				         liquidacionDesembolsoService.setGenerarConstanciaInversioPedido(params);
				}
			}
		}
		return "Se generó la carta de desembolso correctamente.";
	}
	
	@Override
	public Map<String,Object> getLiquidacionDesembolso(Map<String,Object> params) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		liquidacionDesembolsoService.setTokenCaspio(tokenCaspio);
		return liquidacionDesembolsoService.getLiquidacionDesembolso(params);
	}
	
	@Override
	public Map<String,Object> verificarConfirmacionEntrega(Map<String,Object> params)throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(params.get("where")!=null){
			String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
			liquidacionDesembolsoService.setTokenCaspio(tokenCaspio);
			if(liquidacionDesembolsoService.getLiquidacionDesembolso(params).size()>0){
				resultMap.put("estadoRetorno", "0");
				return resultMap;
			}
		}
		resultMap = liquidacionDao.executeProcedure(params, "USP_FOC_consultarConfirmacionEntregaInmuebles");
		return resultMap;
	}
	
	@Override
	public Map<String,Object> confirmacionEntrega(Map<String,Object> params)throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		resultMap = inversionService.confirmacionEntrega(params);
		if("OK".equals(resultMap.get("mensaje"))){
			resultMap = liquidacionDao.executeProcedure(params, "USP_LOG_entregarInversionInmueble");
		}	
		resultMap.put("mensaje", "OK");
		return resultMap;
	}

	@Override
	public ResultadoBean actualizarInmuebleInversionHipotecado(String nroInversion,String check) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean = new ResultadoBean();
		
		Integer result =  inversionService.actualizarInmuebleInversionHipotecado(nroInversion,check);
		resultadoBean.setEstado(result);
		
		return resultadoBean;
	}

	@Override
	public ResultadoBean crearCreditoGarantia(String nroInversion, String usuarioId) throws Exception {
		Integer res = garantiaDAO.crearCreditoGarantiaEvaluacionCrediticia(nroInversion, usuarioId);
		ResultadoBean resultadoBean=new ResultadoBean();
		resultadoBean.setEstado(res);
		return resultadoBean;
	}
	
	@Override
	public ResultadoBean eliminarCreditoGarantia(String nroInversion, String usuarioId) throws Exception {
		Integer res = garantiaDAO.crearCreditoGarantiaEvaluacionCrediticia(nroInversion, usuarioId);
		ResultadoBean resultadoBean=new ResultadoBean();
		resultadoBean.setEstado(res);
		return resultadoBean;
	}
	
	@Override
	public String validarInversionesRetiroAdjudicacion(String nroPedido) throws Exception{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("where", "Pedido_NroPedido='" + nroPedido + "'");
		List<Map<String,Object>> inversiones = genericService.obtenerTablaCaspio(genericService.viewPedidoInversion, JsonUtil.toJson(params));
		if(inversiones.size()>0){
			params.put("where", "Pedido_NroPedido='" + nroPedido + "'  and PedidoInversion_Confirmado='NO'");
			inversiones = genericService.obtenerTablaCaspio(genericService.viewPedidoInversion, JsonUtil.toJson(params));
			if(inversiones.size()>0){
				return "INC";
			}else{
				return "";
			}
		}else{
			return "NI";
		}
	}
	

	private void enviarCorreoConfirmacionInmueble(Pedido pedido, Inversion inversion) throws Exception{

		String siguienteProceso="";
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
			if(Constantes.Persona.TIPO_DOCUMENTO_RUC_ID.equals(inversion.getPropietarioTipoDocId())){
				//El proveedor es PJ
				siguienteProceso="el Registro de comprobante(s) de pago emitidos por el proveedor/vendedor del inmueble";
			}
			else{
				//El proveedor es PN
				siguienteProceso="la Liquidación de fondos disponibles";
			}
		}
		else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
			if(inversion.getServicioConstructora()){
				//Con constructora
				siguienteProceso="el Registro de comprobante(s) de pago emitidos por el proveedor/vendedor del inmueble";
			}
			else{
				//Sin constructora
				siguienteProceso="la Liquidación de fondos disponibles";
			}
		}
		else if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
			siguienteProceso="la Actualización del saldo de la deuda";
		}

		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("NumeroPedido",pedido.getNroPedido());
		parameters.put("NumeroInversion",inversion.getNroInversion());
		parameters.put("TipoInversion",Constantes.TABLA_TIPO_INVERSION.get(inversion.getTipoInversion()));
		parameters.put("TipoInmueble",inversion.getTipoInmuebleNom());
		parameters.put("LibreGravamen",inversion.getGravamen());
		parameters.put("AreaTotal",inversion.getAreaTotal());
		parameters.put("PartidaRegistral",inversion.getPartidaRegistral());
		parameters.put("ImporteInversion",inversion.getImporteInversion());
		parameters.put("SiguienteProceso",siguienteProceso);
		parameters.put("ProcesoID","01134");
		genericDao.executeProcedure(parameters, "USP_LOG_EnviarCorreo_Confirmacion_Inmuebles");
	}

	@Override
	public void enviarCorreoDesembolsoExcepcional(String inversionId, String nroArmada, Map<String, Object> params)
			throws Exception {
		String procedimiento = "USP_LOG_EnviarCorreo_DesembolsoExcepcional_Inmuebles";

		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
		List<LiquidacionSAF> liquidacionesSAF = liquidacionDao.obtenerLiquidacionPorInversionArmada(inversion.getNroInversion(), nroArmada);
		if(liquidacionesSAF!=null && liquidacionesSAF.size()>0){
			LiquidacionSAF liquidacionSAF = new LiquidacionSAF();
			double liquidacionImporte = 0.00;
			for(LiquidacionSAF liquidacion : liquidacionesSAF){
				liquidacionSAF.setLiquidacionFecha(liquidacion.getLiquidacionFecha());
				liquidacionImporte+=liquidacion.getLiquidacionImporte();
			}
			liquidacionSAF.setLiquidacionImporte(liquidacionImporte);
			
			params.put("importeDesembolso", liquidacionSAF.getLiquidacionImporte());
			params.put("importeComprobante", getComprobanteResumen(inversion.getNroInversion(),Integer.parseInt(nroArmada)).get("importe"));
		}
		if(params.get("solicitudInformacionGeneral")!=null){
			genericDao.update("update GEN_Solicitud set SolicitudInformacionGeneral='"+(String)params.get("solicitudInformacionGeneral")+"' where SolicitudID="+(String)params.get("solicitudId"));
		}
		enviarCorreoDesembolsoExcepcional(pedido,inversion,params,procedimiento);
	}

	@Override
	public void enviarCorreoRegistroDesembolso(String nroInversion,String nroArmada,String parcial) throws Exception {
		String procedure = "USP_LOG_EnviarCorreo_Tesoreria_Inmuebles";
		Map<String,Object> params = new HashMap<String,Object>();
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
        Inversion inversion= inversionService.obtenerInversionCaspioPorNro(nroInversion);
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId()));
		if(parcial.equals("PARCIAL")){
			procedure = "USP_LOG_EnviarCorreo_EmisionConstancia_Inmuebles";
			List<LiquidacionSAF> liquidacionesSAF = liquidacionDao.obtenerLiquidacionPorInversionArmada(inversion.getNroInversion(), nroArmada);
			if(liquidacionesSAF!=null && liquidacionesSAF.size()>0){
				LiquidacionSAF liquidacionSAF = new LiquidacionSAF();
				double liquidacionImporte = 0.00;
				for(LiquidacionSAF liquidacion : liquidacionesSAF){
					liquidacionSAF.setLiquidacionFecha(liquidacion.getLiquidacionFecha());
					liquidacionImporte+=liquidacion.getLiquidacionImporte();
				}
				liquidacionSAF.setLiquidacionImporte(liquidacionImporte);				
				params.put("ImporteDesembolso", liquidacionSAF.getLiquidacionImporte());
			}
		}
		
		enviarCorreoConfirmacion(pedido,inversion,params,procedure);
	}

	private Map<String,Object> enviarCorreoDesembolsoExcepcional(Pedido pedido, Inversion inversion,Map<String,Object> params, String procedimiento) throws Exception{
		
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("NumeroPedido",pedido.getNroPedido());
		parameters.put("TipoInversion",Constantes.TABLA_TIPO_INVERSION.get(inversion.getTipoInversion()));
		parameters.put("TipoInmueble",inversion.getTipoInmuebleNom());
		parameters.put("NumeroSolicitud", (String)params.get("solicitudId"));
		parameters.put("Observacion", (String)params.get("observacion"));
		parameters.put("UsuarioId", Integer.parseInt(params.get("usuarioId").toString()));
		parameters.put("ImporteDesembolso", params.get("importeDesembolso"));
		parameters.put("ImporteComprobante", params.get("importeComprobante"));		
		parameters.put("Estado", params.get("estado"));		
		parameters.put("ProcesoID","01134");
		return genericDao.executeProcedure(parameters, procedimiento);
	}

	@Override
	public List<Map<String,Object>> getListaInversionPorNroPedido(String nroPedido) throws Exception {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("where", "Pedido_NroPedido='" + nroPedido + "'");
		return genericService.obtenerTablaCaspio(genericService.viewPedidoInversion, JsonUtil.toJson(params));
		
	}

	@Override
	public List<Map<String, Object>> consultaEventoInversion(String nroInversion) throws Exception{
		final int EVENTO_CONFIRMACION = 1;
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
        Inversion inversion= inversionService.obtenerInversionCaspioPorNro(nroInversion);
		List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(inversion.getInversionId());

		List<Map<String,Object>> listaEventosSAF = inversionDao.getListaEventoInversionPorNumeroInversion(nroInversion);
		
		Map<String,Object> mapEventoConfirmacion = listaEventosSAF.get(EVENTO_CONFIRMACION);//Estado confirmacion Inmueble
		String estadoConfirmacion = (String)mapEventoConfirmacion.get("EstadoSolicitud");
		if("CONFIRMADO".equalsIgnoreCase(estadoConfirmacion)){
			Map<String, Object> mapEventoFacturaProveedor = getEventoFacturaProveedor(inversion, listaComprobantes);
			Map<String, Object> mapEventoLiquidacion = getEventoLiquidacion(nroInversion);
			
			listaEventosSAF.add(listaEventosSAF.size()-1,mapEventoFacturaProveedor);
			listaEventosSAF.add(listaEventosSAF.size()-1,mapEventoLiquidacion);
			
			if(!((String)mapEventoLiquidacion.get("EstadoSolicitud")).equals("DESEMBOLSADO")){
				listaEventosSAF.remove(listaEventosSAF.size()-1);
			}
			
		}
		
		return listaEventosSAF;
	}

	private Map<String, Object> getEventoLiquidacion(String nroInversion) throws Exception {
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(nroInversion);
		Map<String,Object> mapEventoLiquidacion = new HashMap<String,Object>();
		mapEventoLiquidacion.put("indice", "4");
		mapEventoLiquidacion.put("Instancia", "LIQUIDACIÓN DE INMUEBLE");
		mapEventoLiquidacion.put("EstadoSolicitud", "PENDIENTE");
		mapEventoLiquidacion.put("FechaModificacion", "");
		mapEventoLiquidacion.put("UsuarioNombre", "");
		mapEventoLiquidacion.put("Referencia", "");
		for (LiquidacionSAF liquidacionSAF : liquidacionInversion) {
			if(liquidacionSAF.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_CREADO)){
				mapEventoLiquidacion.put("EstadoSolicitud", "LIQUIDADO");
			}
			else if(liquidacionSAF.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_VB_CONTB)){
				mapEventoLiquidacion.put("EstadoSolicitud", "V°B° CONTABLE");
			}
			else if(liquidacionSAF.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
				mapEventoLiquidacion.put("EstadoSolicitud", "DESEMBOLSADO");
				mapEventoLiquidacion.put("Referencia", "DESEMBOLSO");
			}
			mapEventoLiquidacion.put("FechaModificacion", liquidacionSAF.getLiquidacionFecha());
			mapEventoLiquidacion.put("UsuarioNombre", liquidacionSAF.getUsuarioLogin());
		}
		return mapEventoLiquidacion;
	}

	private Map<String, Object> getEventoFacturaProveedor(Inversion inversion,
			List<ComprobanteCaspio> listaComprobantes) {
		Map<String,Object> mapEventoFacturaProveedor = new HashMap<String,Object>();
		mapEventoFacturaProveedor.put("indice", "3");
		mapEventoFacturaProveedor.put("Instancia", "FACTURA DEL PROVEEDOR");
		mapEventoFacturaProveedor.put("EstadoSolicitud", "PENDIENTE");
		mapEventoFacturaProveedor.put("FechaModificacion", "");
		mapEventoFacturaProveedor.put("UsuarioNombre", "");
		mapEventoFacturaProveedor.put("Referencia", "");
		
		if(listaComprobantes!=null&&listaComprobantes.size()>0){
			mapEventoFacturaProveedor.put("EstadoSolicitud", "REGISTRADA");
			String[] listaNroComprobantes = new String[listaComprobantes.size()];
			int i = 0;
			for(ComprobanteCaspio comprobanteCaspio: listaComprobantes){
				mapEventoFacturaProveedor.put("FechaModificacion", Util.convertirFechaDate(comprobanteCaspio.getFechaEmision(),"yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"));
				mapEventoFacturaProveedor.put("UsuarioNombre",comprobanteCaspio.getEnvioContabilidadUsuario());
				listaNroComprobantes[i] =  comprobanteCaspio.getSerie()+"-"+comprobanteCaspio.getNumero();
				i++;
			}
			mapEventoFacturaProveedor.put("Referencia", StringUtils.join(listaNroComprobantes,","));
		}
		if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
			if(!Constantes.Persona.TIPO_DOCUMENTO_RUC_ID.equals(inversion.getPropietarioTipoDocId())){
				mapEventoFacturaProveedor.put("EstadoSolicitud", "NO APLICA");
			}
		}
		return mapEventoFacturaProveedor;
	}
	
	
	
}
