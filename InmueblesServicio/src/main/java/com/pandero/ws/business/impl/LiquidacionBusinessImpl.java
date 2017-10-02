package com.pandero.ws.business.impl;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.ComprobanteCaspio;
import com.pandero.ws.bean.ConceptoLiquidacion;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.GarantiaDao;
import com.pandero.ws.dao.GenericDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.LiquidDesembService;
import com.pandero.ws.service.MailService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentGenerator;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.XDocReportException;

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
	GarantiaService garantiaService;
	@Autowired
	LiquidDesembService liquidDesembService;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	LiquidacionDao liquidacionDao;
	@Autowired
	ContratoBusiness contratoBusiness;
	@Autowired
	GarantiaDao garantiaDao;
	@Autowired
	GenericDao genericDao;
	
	@Autowired
	MailService mailService;
	
	
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${desarrollo.pandero.email}")
	private String emailDesarrolloPandero;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	
	public List<Contrato> obtenerTablaContratosPedidoActualizado(String nroPedido) throws Exception{		
		// Obtener contratos del pedido
		System.out.println("NRO PEDIDO: "+nroPedido);
		List<Contrato> listaContratosPedidoSAF = pedidoDao.obtenerContratosxPedidoSAF(nroPedido);		
		if(listaContratosPedidoSAF==null){
			LOG.info("listaContratosPedidoSAF:: NULL");
		}else{
			LOG.info("listaContratosPedidoSAF: "+listaContratosPedidoSAF.size());
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
		if(liquidacionPedido==null){
			LOG.info("liquidacionPedido:: NULL");
		}else{
			LOG.info("liquidacionPedido: "+liquidacionPedido.size());
		}
		
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
		LOG.info("LUEGO - Obtener monto liquidado x contrato");
		
		// Obtener montos diponibles
		for(Contrato contrato : listaContratosPedidoSAF){
			contrato.setMontoDisponible(contrato.getMontoCertificado().doubleValue()-contrato.getMontoLiquidacionContrato().doubleValue());
			contrato.setDiferenciaPrecioDisponible(contrato.getDiferenciaPrecio().doubleValue()-contrato.getMontoLiquidacionDifPrecio().doubleValue());
			contrato.setTotalDisponible(contrato.getMontoDisponible().doubleValue()+contrato.getDiferenciaPrecioDisponible());
		}
		LOG.info("LUEGO - Obtener montos diponibles");
		
		return listaContratosPedidoSAF;
	}
	
	@Override
	public String generarLiquidacionPorInversion(String nroInversion, String nroArmada, String usuarioId)
			throws Exception {
		System.out.println("EN generarLiquidacionPorInversion");
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		garantiaService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		liquidDesembService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		boolean validacionLiquidacion = true;
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		
		// Obtener nroArmadaId		
		if(nroArmada==null || nroArmada.equals("")){ nroArmada="1"; }
		if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
				&& inversion.getServicioConstructora().booleanValue()==false){	
			if(nroArmada.equals("1")){ nroArmada="2"; }
		}
				
		// Validar inversion confirmada
		if(inversion.getConfirmado()==null || !inversion.getConfirmado().equals("SI")){
			validacionLiquidacion = false;
			resultado = Constantes.Service.RESULTADO_INVERSION_NO_CONFIRMADA;
		}else{		
			// Obtener datos pedido-inversion SAF
			PedidoInversionSAF pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
					
			// Obtener valores pedido-contrato actualizado
			LOG.info("pedidoInversionSAF.getNroPedido(): "+pedidoInversionSAF.getNroPedido());
			List<Contrato> listaPedidoContrato = obtenerTablaContratosPedidoActualizado(pedidoInversionSAF.getNroPedido());
			
			// Verificar si existe monto disponible para la inversion
			double totalDisponible=0;
			double totalDisponibleContratos = obtenerTotalDisponibleEnPedido(listaPedidoContrato);
			Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
			double montoDifPrecio = pedido.getCancelacionDiferenciaPrecioMonto()==null?0.00:pedido.getCancelacionDiferenciaPrecioMonto();
			// Total disponible
			totalDisponible=totalDisponibleContratos+montoDifPrecio;
			LOG.info("totalDisponible:: "+totalDisponibleContratos+"+"+montoDifPrecio);
			
			if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){
				
			}else{
				if(validacionLiquidacion){				
					// Verficar si hay monto para liquidar
					if(totalDisponible<inversion.getImporteInversion().doubleValue()){
						resultado = Constantes.Service.NO_MONTO_DISPONIBLE_LIQUIDAR;
						validacionLiquidacion = false;
					}
				}
			}
			
			// Verificar comprobantes
			if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
				LOG.info("CANCELACION: "+inversion.getImporteInversionInicial()+" - "+inversion.getFechaActualizacionSaldo());
				if(inversion.getImporteInversionInicial()==null || Util.esVacio(inversion.getFechaActualizacionSaldo())){
					validacionLiquidacion = false;
					resultado = Constantes.Service.RESULTADO_SIN_ACTUALZ_SALDO_DEUDA;
				}else{
					if(Util.esVacio(inversion.getEnvioContabilidadFecha())){
						validacionLiquidacion = false;
						resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
					}
				}
				
			}else if((Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())
					&& Constantes.DocumentoIdentidad.RUC_ID.equals(inversion.getPropietarioTipoDocId())) 
					|| (Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
					&& inversion.getServicioConstructora())){
				List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(inversion.getInversionId(), Integer.parseInt(nroArmada));
				if(listaComprobantes==null || listaComprobantes.size()==0){
					validacionLiquidacion = false;
					resultado = Constantes.Service.RESULTADO_SIN_COMPROBANTES;
				}else{
					for(ComprobanteCaspio comprobante : listaComprobantes){
						if(Util.esVacio(comprobante.getEnvioContabilidadFecha())){
							validacionLiquidacion = false;
							resultado = Constantes.Service.RESULTADO_SIN_ENVIO_CARGO_CONTABILIDAD;
							break;
						}
					}
				}
			}
								
			// Validar montos por cobrar
			if(validacionLiquidacion){
				boolean validacionConceptos = validacionConceptosLiquidacion(inversion, totalDisponible);
				if(validacionConceptos==false){
					validacionLiquidacion = false;
					resultado = Constantes.Service.RESULTADO_PENDIENTE_COBROS;
				}
			}
			
			// Verificar si todos los contratos estan cancelados
			int cantContratosPedido = 0, cantContratosCancelados = 0;
			cantContratosPedido = listaPedidoContrato.size();
			for(Contrato contrato : listaPedidoContrato){
				System.out.println("CONTRATOS: "+contrato.getNroContrato()+" - "+contrato.getSituacionContrato());
				if(contrato.getSituacionContrato()!=null){
					System.out.println("situacion: "+contrato.getSituacionContrato().substring(0,2));
				}
				if(contrato.getSituacionContrato()!=null && 
						contrato.getSituacionContrato().substring(0,2).equals("01")){
					cantContratosCancelados += 1;
				}
			}
			
			// Validar garantias
			if(validacionLiquidacion){
				// Si todos los contratos estan cancelados no se evaluan las garantias
				if(cantContratosPedido>cantContratosCancelados){				
					// Obtener garantias del pedido
					List<Garantia> listaGarantias = garantiaDao.obtenerGarantiasPorInversion(nroInversion);
					if(listaGarantias!=null && listaGarantias.size()>0){
						for(Garantia garantia : listaGarantias){
							System.out.println("garantia: "+garantia.getIdGarantia()+" - "+garantia.getFichaConstitucion()+" - "+garantia.getFechaConstitucion()+" - "+garantia.getConstitucionEtapaID());
							if(!Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
									&& inversion.getInmuebleInversionHipotecado()){
								int constitucionGarantiaEtapa = garantia.getConstitucionEtapaID()==null?0:garantia.getConstitucionEtapaID();
								if(Constantes.Garantia.CONST_GARANTIA_ETAPA_BLOQUEO_REGISTRAL>constitucionGarantiaEtapa){
									validacionLiquidacion=false;
									resultado = Constantes.Service.RESULTADO_NO_GARANTIA_BLOQUEO_REGISTRAL;
									break;
								}
							}else{
								if(Util.esVacio(garantia.getFichaConstitucion()) || Util.esVacio(garantia.getFechaConstitucion())){
									validacionLiquidacion=false;
									resultado = Constantes.Service.RESULTADO_NO_GARANTIA_FICHA_FECHA;
									break;
								}
							}				
						}
					}else{
						validacionLiquidacion=false;
						resultado = Constantes.Service.RESULTADO_NO_GARANTIAS;
					}				
				}else{
					LOG.info("No se evaluan las garantias");
					System.out.println("No se evaluan las garantias");
				}
			}
			
			
						
			// Validar el estado de la liquidacion
			if(validacionLiquidacion){
				// Obtener liquidaciones del pedido
				List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionesPorInversionSAF(nroInversion);						
				String estadoLiquidacion = Util.obtenerEstadoLiquidacionPorNroArmada(liquidacionInversion, nroArmada);
				LOG.info("ESTADO LIQUIDACION:: "+estadoLiquidacion);
				if(!estadoLiquidacion.equals("")){
					resultado = Constantes.Service.RESULTADO_EXISTE_LIQUIDACION;
					validacionLiquidacion = false;
				}
			}
										
			// Obtener liquidacion de la inversion
			if(validacionLiquidacion){
				// Obtener Armada x tipo de inversion
				double montoALiquidar = 0.00;
				if(Constantes.TipoInversion.CONSTRUCCION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){				
					// Obtener monto a liquidar
					List<Constante> listaArmadasDesemb = constanteService.obtenerListaArmadasDesembolso();
					double porcentajeArmada = Util.obtenerPorcentajeArmada(listaArmadasDesemb, nroArmada);
					LOG.info("inversion.getImporteInversion(): "+inversion.getImporteInversion()+" - porcentajeArmada: "+porcentajeArmada);
					
					double montoAUsarLiquidacion = inversion.getImporteInversion().doubleValue();
					if(inversion.getServicioConstructora() || 
							(!inversion.getServicioConstructora() && nroArmada.equals("2"))){
						if(totalDisponibleContratos<inversion.getImporteInversion().doubleValue()){
							montoAUsarLiquidacion = totalDisponibleContratos;
						}
					}
					if(!inversion.getServicioConstructora() &&
							(nroArmada.equals("3") || nroArmada.equals("4"))){
						List<LiquidacionSAF> liquidacion1 = liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion, "2");
						double montoLiquidacion1 = obtenerMontoTotalLiquidacion(liquidacion1);
						montoAUsarLiquidacion = montoLiquidacion1*2;
					}
					
					double porcentajeDecimal = porcentajeArmada/100;					
					montoALiquidar = montoAUsarLiquidacion*porcentajeDecimal;	
					System.out.println("montoAUsarLiquidacion:: "+montoAUsarLiquidacion+" - porcentajeDecimal:: "+porcentajeDecimal);
					System.out.println("MONTO LIQUIDAR:: "+montoALiquidar);
				}else{
					montoALiquidar = inversion.getImporteInversion();
				}
				LOG.info("montoALiquidar: "+montoALiquidar);
				
				// Generar liquidacion por monto
				generarLiquidacionPorMonto(pedidoInversionSAF, montoALiquidar, nroArmada, listaPedidoContrato, usuarioId, inversion);
				
							
				// Actualizar montos de los contratos del pedido
				actualizarTablaContratosPedido(pedidoInversionSAF.getNroPedido());

				//Aqui agregamos los 3 tipos de correo.
				if(Constantes.TipoInversion.CANCELACION_ID.equals(pedidoInversionSAF.getPedidoTipoInversionID())){
					//Correo cuando aplica el registro del saldo de la deuda
					LOG.info("Correo confirmacion de liquidacion: actualizar saldo de deuda");					
					enviarCorreoActualizacionSaldoDeuda(inversion, pedido);
					
				}
				else{
					List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(inversion.getInversionId(), Integer.parseInt(nroArmada));
					if(listaComprobantes!=null&&listaComprobantes.size()>0){
						//Correo cuando aplica el registro de comprobante de pago
						LOG.info("Correo confirmacion de liquidacion: registro de comprobante");
						enviarCorreoRegistroComprobante(inversion,pedido,listaComprobantes);						
					}
					else{
						LOG.info("Correo confirmacion de liquidacion: No registro de comprobante");
						//Correo cuando no aplica el registro de comprobante de pago ni saldo de deuda
						enviarCorreoLiquidacion(pedido,inversion,"USP_EnviaCorreo_Liquidacion_Inmuebles");						
					}
				}				
			}			
		}
		LOG.info("TERMINO LIQUIDACION");		
		
		return resultado;
	}

	private void enviarCorreoActualizacionSaldoDeuda(Inversion inversion, Pedido pedido)
			throws IOException, XDocReportException, XDocConverterException, ConnectException, Exception {

		Map<String,Object> outputMap = enviarCorreoLiquidacion(pedido,inversion,"USP_EnviaCorreo_RegistroSaldoDeuda_Inmuebles");

		File rutaTemplate = new File(rutaDocumentosTemplates+"/liquidacion/cancelacionDeuda.odt");
		File rutaSalida = File.createTempFile("tmp", ".odt");
		File rutaSalidaPdf = File.createTempFile("cargo"+System.currentTimeMillis(), ".pdf");

		Map<String, Object> contexto = new HashMap<String, Object>();
		contexto.put("nroCredito", inversion.getNroCredito());
		contexto.put("importeInicial",  Util.getMontoFormateado(inversion.getImporteInversionInicial()));
		contexto.put("importeFinal", Util.getMontoFormateado(inversion.getImporteInversion()));		
		contexto.put("diferencia", Util.getMontoFormateado(inversion.getImporteInversionInicial()-inversion.getImporteInversion()));
		contexto.put("emisor",inversion.getEnvioContabilidadUsuario());
		contexto.put("proveedor", inversion.getPropietarioNombreCompleto()+""+inversion.getPropietarioRazonSocial());
		contexto.put("asociados", outputMap.get("AsociadoNombreCompleto"));
		contexto.put("funcionario", outputMap.get("FuncionarioNombreCompleto"));
		contexto.put("contratos", outputMap.get("ContratoNumeroDetalle"));
		contexto.put("fecha", Util.getDateFormat(new Date(System.currentTimeMillis()),"dd/MM/yyyy hh:mm a" ));

		DocumentGenerator.generateOdtFromOdtTemplate(rutaTemplate, rutaSalida, contexto);
		DocumentGenerator.generatePdfFromOds(rutaSalida, rutaSalidaPdf);

		
		EmailBean email = new EmailBean();
		email.setEmailFrom(documentoEmailTo);
		email.setTextoEmail((String)outputMap.get("Mensaje"));
		email.setSubject((String)outputMap.get("Asunto"));
		email.setFormatHtml(true);
		email.setEmailTo((String)outputMap.get("DestinatarioCorreo"));
		email.setEnviarArchivo(true);
		email.setAttachment(rutaSalidaPdf);
		mailService.sendMail(email);
		FileUtils.forceDelete(rutaSalida);
		FileUtils.forceDelete(rutaSalidaPdf);
	}
	

	private void enviarCorreoRegistroComprobante(Inversion inversion, Pedido pedido, List<ComprobanteCaspio> listaComprobantes)
			throws IOException, XDocReportException, XDocConverterException, ConnectException, Exception {

		Map<String,Object> outputMap = enviarCorreoLiquidacion(pedido,inversion,"USP_EnviaCorreo_ComprobantePago_Inmuebles");

		File rutaTemplate = new File(rutaDocumentosTemplates+"/liquidacion/registroComprobantesUnProveedor.odt");
		if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
			if(!inversion.getServicioConstructora()){
				rutaTemplate = new File(rutaDocumentosTemplates+"/liquidacion/registroComprobantesMultiProveedor.odt");
			}
		}
				
		File rutaSalida = File.createTempFile("tmp", ".odt");
		File rutaSalidaPdf = File.createTempFile("cargo"+System.currentTimeMillis(), ".pdf");

		Map<String, Object> contexto = new HashMap<String, Object>();
		contexto.put("emisor",inversion.getEnvioContabilidadUsuario());
		contexto.put("proveedor", inversion.getPropietarioNombreCompleto()+""+inversion.getPropietarioRazonSocial());
		contexto.put("asociados", outputMap.get("AsociadoNombreCompleto"));
		contexto.put("funcionario", outputMap.get("FuncionarioNombreCompleto"));
		contexto.put("contratos", outputMap.get("ContratoNumeroDetalle"));
		
		List<Constante> listaTipoDocumento = constanteService.obtenerListaTipoComprobante();
		Map<Integer,String> mapTipoDocumento = new HashMap<Integer,String>();
		for (Constante constante : listaTipoDocumento) {
			mapTipoDocumento.put(constante.getConstanteId(), constante.getNombreConstante());
		}
				
		List<String[]> lista = new ArrayList<String[]>();
		int i = 0;
		Double montoTotalSoles = 0.00;
		Double montoTotalDolares = 0.00;
		
		for (ComprobanteCaspio comprobanteCaspio : listaComprobantes) {
			String[] comprobante = new String[6];
			String simbolo = Constantes.Moneda.DOLAR_SIMBOLO;
			String sql = "select Proveedor=CASE WHEN TipoDocumentoID=8 THEN PersonaRazonSocial ELSE PersonaNombreCompleto END from PER_Persona where PersonaID=";
			comprobante[0] = String.valueOf(++i);
			comprobante[1] = (String)genericDao.queryForMap(sql+comprobanteCaspio.getProveedor()).get("Proveedor") ;
			comprobante[2] = mapTipoDocumento.get(Float.valueOf(comprobanteCaspio.getDocumentoID()).intValue());
			comprobante[3] = comprobanteCaspio.getSerie()+"-"+comprobanteCaspio.getNumero();
			comprobante[4] = Util.convertirFechaDate(comprobanteCaspio.getFechaEmision(),"yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy");
			if(Float.valueOf(comprobanteCaspio.getTipoMoneda()).intValue() != Integer.parseInt(Constantes.Moneda.DOLAR_CODIGO)){
				simbolo = Constantes.Moneda.SOLES_SIMBOLO;
				montoTotalSoles+=comprobanteCaspio.getImporte();
			}
			else{
				montoTotalDolares+=comprobanteCaspio.getImporte();
			}
			comprobante[5] = simbolo+" "+Util.getMontoFormateado(comprobanteCaspio.getImporte());
			lista.add(comprobante);
			
		}
		contexto.put("listaComprobantes", lista);
		contexto.put("fecha", Util.getDateFormat(new Date(System.currentTimeMillis()),"dd/MM/yyyy hh:mm a" ));
		if(montoTotalSoles>0){
			contexto.put("etiquetaSoles","TOTAL S/.");
			contexto.put("montoTotalSoles", Util.getMontoFormateado(montoTotalSoles));	
		}
		if(montoTotalDolares>0){
			contexto.put("etiquetaDolares","TOTAL US$");
			contexto.put("montoTotalDolares", Util.getMontoFormateado(montoTotalDolares));
		}
		
		DocumentGenerator.generateOdtFromOdtTemplate(rutaTemplate, rutaSalida, contexto);
		DocumentGenerator.generatePdfFromOds(rutaSalida, rutaSalidaPdf);

		
		EmailBean email = new EmailBean();
		email.setEmailFrom(documentoEmailTo);
		email.setTextoEmail((String)outputMap.get("Mensaje"));
		email.setSubject((String)outputMap.get("Asunto"));
		email.setFormatHtml(true);
		email.setEmailTo((String)outputMap.get("DestinatarioCorreo"));
		email.setEnviarArchivo(true);
		email.setAttachment(rutaSalidaPdf);
		mailService.sendMail(email);
		FileUtils.forceDelete(rutaSalida);
		FileUtils.forceDelete(rutaSalidaPdf);
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
	
	private String generarLiquidacionPorMonto(PedidoInversionSAF pedidoInversionSAF, double montoALiquidar, String nroArmadaId,
			List<Contrato> listaPedidoContrato, String usuarioId, Inversion inversion) throws Exception{
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
				liquidacion.setNroArmada(Integer.parseInt(nroArmadaId));
				liquidacionDao.registrarLiquidacionInversionSAF(liquidacion,usuarioId);
			}
			// Actualizar nro liquidacion en inversion
			inversionService.actualizarEstadoInversionLiquidadoPorNro(pedidoInversionSAF.getPedidoInversionNumero(), numeroLiquidacion, Constantes.Inversion.ESTADO_LIQUIDADO);
			// Registrar liquidacion en caspio
			String inversionId = String.valueOf(inversion.getInversionId().intValue());
			liquidDesembService.registrarLiquidacionInversion(inversionId, nroArmadaId, numeroLiquidacion);
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
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidDesembService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		boolean existeLiquidacionArmada = false;
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
					
		// Obtener nroArmadaId	
		if(nroArmada==null || nroArmada.equals("")){ nroArmada="1"; }
		if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
				&& inversion.getServicioConstructora().booleanValue()==false){	
			if(nroArmada.equals("1")){ nroArmada="2"; }
		}
		
		// Obtener liquidaciones
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion,nroArmada);
		
		// Obtener el estado de la liquidacion
		PedidoInversionSAF pedidoInversionSAF = null;
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			// Obtener datos pedido-inversion SAF
			pedidoInversionSAF = pedidoDao.obtenerPedidoInversionSAF(nroInversion);
			// Obtener estado de la liquidacion
			for(LiquidacionSAF liquidacion : liquidacionInversion){
				if(liquidacion.getNroArmada()==Integer.parseInt(nroArmada)){
					existeLiquidacionArmada = true;
					if(liquidacion.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_VB_CONTB)){
						resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
						break;
					}else if(liquidacion.getLiquidacionEstado().equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
						resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
						break;
					}
				}
			}
		}
		
		if(existeLiquidacionArmada==false){
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
		}
		
		if(resultado.equals("")){
			// Eliminar liquidacion
			liquidacionDao.eliminarLiquidacionInversionSAF(nroInversion, nroArmada, usuarioId);
			
			// Actualizar nro liquidacion en inversion
			inversionService.actualizarEstadoInversionLiquidadoPorNro(pedidoInversionSAF.getPedidoInversionNumero(), "", Constantes.Inversion.ESTADO_EN_PROCESO);
						
			// Eliminar liquidacion en caspio
			String inversionId = String.valueOf(inversion.getInversionId().intValue());
			liquidDesembService.eliminarLiquidacionInversion(inversionId, nroArmada);
			
			// Actualizar montos de los contratos del pedido
			actualizarTablaContratosPedido(pedidoInversionSAF.getNroPedido());
		}
		
		return resultado;
	}

	@Override
	public String confirmarLiquidacionInversion(String nroInversion, String nroArmada, String usuarioId) throws Exception {
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put("inversionNumero", nroInversion);
		Map<String,Object> out = genericDao.executeProcedure(parameters, "USP_FOC_ValidarAsociadoMoroso_Inmuebles");
		if(out.get("resultado") != null){
			return Constantes.Service.RESULTADO_ASOCIADO_MOROSO;
		}
		
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidDesembService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
						
		// Obtener liquidaciones de la inversion
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion,nroArmada);
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			System.out.println("liquidacionInversion: "+liquidacionInversion.size());
			String estadoLiquidacion="";
			for(LiquidacionSAF liquidacion : liquidacionInversion){
				estadoLiquidacion=liquidacion.getLiquidacionEstado();
			}
			System.out.println("ESTADO LIQUIDA: "+estadoLiquidacion);
			// Verificar estado liquidacion
			if(Util.esVacio(estadoLiquidacion)){
				resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
			}else if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_VB_CONTB)){
				resultado = Constantes.Service.RESULTADO_INVERSION_VB_CONTABLE;
			}else if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
				resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
			}
		}else{
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
			System.out.println("liquidacionInversion:. NULL");
		}
		
		// Verificar envio de comprobantes
		if(resultado.equals("")){
			Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
			if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
				if(Util.esVacio(inversion.getRecepContabilidadFecha())){
					resultado = Constantes.Service.RESULTADO_SIN_RECEPCION_CARGO_CONTABILIDAD;
				}
			}else if((Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())
							&& Constantes.DocumentoIdentidad.RUC_ID.equals(inversion.getPropietarioTipoDocId())) 
						|| (Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
							&& inversion.getServicioConstructora())
						|| (Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
							&& !inversion.getServicioConstructora() && Integer.parseInt(nroArmada)>2)){
				int nroArmadaComprobante = Integer.parseInt(nroArmada);
				if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())
						&& !inversion.getServicioConstructora()){
					nroArmadaComprobante=nroArmadaComprobante-1;
				}
				
				List<ComprobanteCaspio> listaComprobantes = inversionService.getComprobantes(inversion.getInversionId(), nroArmadaComprobante);
				if(listaComprobantes!=null && listaComprobantes.size()>0){
					for(ComprobanteCaspio comprobante : listaComprobantes){
						if(Util.esVacio(comprobante.getRecepContabilidadFecha())){
							resultado = Constantes.Service.RESULTADO_SIN_RECEPCION_CARGO_CONTABILIDAD;
							break;
						}
					}
				}else{
					resultado = Constantes.Service.RESULTADO_SIN_RECEPCION_CARGO_CONTABILIDAD;
				}
			}
		}
		
		// Confirmar liquidacion
		if(resultado.equals("")){
			// Confirmar liquidacion es SAF
			liquidacionDao.confirmarLiquidacionInversion(nroInversion, nroArmada, usuarioId);
			
			// Actualizar estado liquidacion Caspio
			inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, Constantes.Inversion.ESTADO_VB_CONTABLE);
			
			// Actualizar estado liquidacion-desembolso
			Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
			String inversionId = String.valueOf(inversion.getInversionId().intValue());
			liquidDesembService.actualizarEstadoLiquDesembInversion(inversionId, nroArmada, Constantes.Inversion.ESTADO_VB_CONTABLE);
			Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
			enviarCorreoLiquidacion(pedido, inversion, "USP_EnviaCorreo_VistoBuenoContabilidad_Inmuebles");
		}
		
		return resultado;
	}
	
	public DetalleDiferenciaPrecio obtenerMontosDifPrecioInversion(String nroInversion) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		
		
		
		// Obtener datos de inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		
		// Obtener datos del pedido
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
		
		// Obtener saldo diferencia precio
		DetalleDiferenciaPrecio ddp = contratoBusiness.obtenerMontoDiferenciaPrecio(pedido.getPedidoId());
		Double montoDiferenciaPrecio = Double.parseDouble(ddp.getDiferenciaPrecio());
		if(montoDiferenciaPrecio<0){		
			ddp.setDiferenciaPrecio(Util.getMontoFormateado(Double.parseDouble(ddp.getDiferenciaPrecio())));
			ddp.setImporteFinanciado(Util.getMontoFormateado(Double.parseDouble(ddp.getImporteFinanciado())));
			ddp.setSaldoDiferencia(Util.getMontoFormateado(Double.parseDouble(ddp.getSaldoDiferencia())));
			// Obtener monto pagado dif. precio			
			ddp.setMontoDifPrecioPagado(Util.getMontoFormateado(pedido.getCancelacionDiferenciaPrecioMonto()));
			ddp.setTipoInversion(inversion.getTipoInversion());
			
			List<Contrato> listaPedidoContrato = obtenerTablaContratosPedidoActualizado(pedido.getNroPedido());
			
			// Verificar si existe monto disponible para la inversion
			double totalDisponible=0;
			double totalDisponibleContratos = obtenerTotalDisponibleEnPedido(listaPedidoContrato);
			Pedido pedidoDif = pedidoService.obtenerPedidoCaspioPorId(String.valueOf(inversion.getPedidoId().intValue()));
			double montoDifPrecio = pedido.getCancelacionDiferenciaPrecioMonto()==null?0.00:pedido.getCancelacionDiferenciaPrecioMonto();
			// Total disponible
			totalDisponible=totalDisponibleContratos+montoDifPrecio;
			ddp.setMontoDisponible(totalDisponible);
			ddp.setMontoInversion(inversion.getImporteInversion());
		}else{
			ddp = null;
		}
		return ddp;
	}

	@Override
	public String actualizarDesembolsoCaspio(String nroInversion,
			String nroArmada, String nroDesembolso) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidDesembService.setTokenCaspio(tokenCaspio);
		
		// Actualizar estado de la inversion
		inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, Constantes.Inversion.ESTADO_DESEMBOLSADO);
		
		// Obtener inversion
		Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
		String inversionId = String.valueOf(inversion.getInversionId().intValue());
		
		// Registrar desembolso en caspio
		liquidDesembService.registrarDesembolsoInversion(inversionId, nroArmada, nroDesembolso);
				
		return null;
	}
	
	
	private boolean validacionConceptosLiquidacion(Inversion inversion, double totalDisponibleContratos) throws Exception{
		boolean resultado = false, conceptoNoPagado=false;
		
		// Obtener conceptos de liquidacion
		List<ConceptoLiquidacion> listaConceptos = liquidacionDao.obtenerConceptosLiquidacion(inversion.getNroInversion());
		if(listaConceptos!=null && listaConceptos.size()>0){
			for(ConceptoLiquidacion concepto : listaConceptos){
				if(Constantes.Liquidacion.CONCEPTO_SITUACION_PENDIENTE.equals(concepto.getSituacionID())){
					System.out.println("concepto no pagado: "+concepto.getConceptoNombre()+"-"+concepto.getSituacionID());
					conceptoNoPagado=true;
					break;
				}
			}
		}

		// Obtener cancelacion diferencia precio
		if(!Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
			if(totalDisponibleContratos<inversion.getImporteInversion() ){
			DetalleDiferenciaPrecio detalleDifPrecio = obtenerMontosDifPrecioInversion(inversion.getNroInversion());
			if(detalleDifPrecio!=null){
				double montoSaldoDiferencia = Double.parseDouble(detalleDifPrecio.getSaldoDiferencia()==null?"0.00":detalleDifPrecio.getSaldoDiferencia().replace(",", ""));
				double montoPagado = Double.parseDouble(detalleDifPrecio.getMontoDifPrecioPagado()==null?"0.00":detalleDifPrecio.getMontoDifPrecioPagado().replace(",", ""));
				double totalPendiente = montoSaldoDiferencia-montoPagado;
				if (totalPendiente > 0){
					System.out.println("DiferenciaPrecio no pagada: "+montoPagado+" < "+montoSaldoDiferencia);
					conceptoNoPagado=true;
				}
			}
		}
		}
		
		if(conceptoNoPagado==false){
			resultado=true;
		}
		
		return resultado;
	}

	@Override
	public String eliminarConformidadLiquidacion(String nroInversion, String nroArmada, String usuarioId) throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		liquidDesembService.setTokenCaspio(tokenCaspio);
		
		String resultado = "";
		
		// Obtener liquidaciones de la inversion
		List<LiquidacionSAF> liquidacionInversion = liquidacionDao.obtenerLiquidacionPorInversionArmada(nroInversion,nroArmada);
		if(liquidacionInversion!=null && liquidacionInversion.size()>0){
			LOG.info("liquidacionInversion: "+liquidacionInversion.size());
			String estadoLiquidacion="";
			for(LiquidacionSAF liquidacion : liquidacionInversion){
				estadoLiquidacion=liquidacion.getLiquidacionEstado();
			}
			LOG.info("ESTADO LIQUIDA: "+estadoLiquidacion);
			// Verificar estado liquidacion
			if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_CREADO)){
				resultado = Constantes.Service.RESULTADO_LIQUIDACION_NO_CONFIRMADA;
			}else if(estadoLiquidacion.equals(Constantes.Liquidacion.LIQUI_ESTADO_DESEMBOLSADO)){
				resultado = Constantes.Service.RESULTADO_INVERSION_DESEMBOLSADA;
			}
		}else{
			resultado = Constantes.Service.RESULTADO_NO_EXISTE_LIQUIDACION;
			LOG.info("liquidacionInversion:. NULL");
		}
		
		//Eliminar confirmacion de liquidacion
		if(resultado.equals("")){
			// Confirmar liquidacion es SAF
			liquidacionDao.eliminarConformidadInversion(nroInversion, nroArmada, usuarioId);
			// Actualizar estado liquidacion Caspio
			inversionService.actualizarEstadoInversionCaspioPorNro(nroInversion, Constantes.Inversion.ESTADO_LIQUIDADO);
			// Actualizar estado liquidacion-desembolso
			Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
			String inversionId = String.valueOf(inversion.getInversionId().intValue());
			liquidDesembService.actualizarEstadoLiquDesembInversion(inversionId, nroArmada, Constantes.Inversion.ESTADO_LIQUIDADO);
		}
		
		return resultado;
	}
	
	private double obtenerMontoTotalLiquidacion(List<LiquidacionSAF> listaLiquidacion){
		double montoLiquidacion = 0.00;
		if(listaLiquidacion!=null && listaLiquidacion.size()>0){
			for(LiquidacionSAF liquidacion : listaLiquidacion){
				montoLiquidacion+=liquidacion.getLiquidacionImporte();
			}
		}
		return montoLiquidacion;
	}
	
	private Map<String,Object> enviarCorreoLiquidacion(Pedido pedido, Inversion inversion, String procedimiento) throws Exception{
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("NumeroPedido",pedido.getNroPedido());
		parameters.put("NumeroInversion",inversion.getNroInversion());
		parameters.put("TipoInversion",inversion.getTipoInversion());
		parameters.put("TipoInmueble",inversion.getTipoInmuebleNom());
		parameters.put("LibreGravamen",inversion.getGravamen());
		parameters.put("AreaTotal",inversion.getAreaTotal());
		parameters.put("PartidaRegistral",inversion.getPartidaRegistral());
		parameters.put("ImporteInversion",inversion.getImporteInversion());
		parameters.put("ProcesoID","01134");
		return genericDao.executeProcedure(parameters, procedimiento);
	}

	
	
}
