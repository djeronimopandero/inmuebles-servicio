package com.pandero.ws.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.PedidoBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.MailService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentoUtil;
import com.pandero.ws.util.MetodoUtil;

@Component
public class PedidoBusinessImpl implements PedidoBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoBusinessImpl.class);
	
	@Value("${ruta.documentos.templates}")
	private String rutaDocumentosTemplates;
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	@Value("${documento.email.to}")
	private String documentoEmailTo;
	
	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	MailService mailService;

	@Override
	public ResultadoBean registrarNuevoPedido(String nroContrato,
			String usuarioId) throws Exception{
		ResultadoBean resultado = new ResultadoBean();
		// Obtener la situacion del contrato
		Contrato contrato = contratoDao.obtenerContrato(nroContrato);
		
		// Si no esta adjudicado
		if(!MetodoUtil.esSituacionAdjudicado(contrato.getSituacionContrato())){
			// Actualizar estado del contrato a no adjudicado en Caspio
			
			// Enviar mensaje contrato no adjudicado
			resultado.setMensajeError("El contrato no se encuentra adjudicado");
		}else{		
			// Crear pedido y contrato-pedido en SAF
			ResultadoBean pedidoSAF = pedidoDao.crearPedidoSAF(nroContrato, usuarioId);

			if(!pedidoSAF.getMensajeError().equals("")){
				resultado = pedidoSAF;
			}else{
				// Crear pedido en Caspio
				
				
				// Crear contrato-pedido en Caspio
			}
		}
		
		return null;
	}

	@Override
	public void generarOrdenIrrevocablePedido(String pedidoId) throws Exception {
		LOG.info("generarOrdenIrrevocablePedido");
		String nombreDocumento="Orden-irrevocable-inversion-inmobiliaria-Generado.docx";
		try{
			String gestionInmobiliaria="gestion_inversion_inmobiliaria_desembolso";
			 
			DocumentoUtil documentoUtil=new DocumentoUtil();	
			XWPFDocument doc = documentoUtil.openDocument(rutaDocumentosTemplates+"/"+gestionInmobiliaria+"/"+"Orden-irrevocable-de-uso-de-certificado-para-inversion-inmobiliaria.docx");
			
			 
		     if (doc != null) {
		    	 // Obtener contratos del pedido
				 List<Contrato> listaContratos= pedidoService.obtenerContratosxPedidoCaspio(pedidoId);
				 
				 // Obtener nro contrato del asociado
				 String nroContrato = "";
				 nroContrato = listaContratos.get(0).getNroContrato();
				 System.out.println("nroContrato: "+nroContrato);
				 				 
				 // Obtener datos del o los asociados
				 List<Asociado> listaAsociados=pedidoDao.obtenerAsociadosxContratoSAF(nroContrato);
				 
				 // Obtener inversiones del pedido
				 List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoId);
				 
				 // Obtener inversiones del pedido
				 List<Constante> listaDocuIdentidad = constanteService.obtenerListaDocumentosIdentidad();
		    	 
				 // Obtener sumatorias totales
				 double sumaContratos = MetodoUtil.getSumaContratosxPedido(listaContratos);
				 double sumaInversiones = MetodoUtil.getSumaInversionesxPedido(listaInversiones);
				 double saldo = sumaInversiones-sumaContratos;
				 
		    	 List<Parametro> listaParametros=getParametrosOrdenIrrevocable(sumaContratos,sumaInversiones,saldo,pedidoId);
		    	 
		         doc = DocumentoUtil.replaceTextOrdenIrrevocable(doc,listaParametros,listaDocuIdentidad, listaAsociados,listaContratos,listaInversiones);
		         StringBuilder sb=new StringBuilder();
		         documentoUtil.saveDocument(doc, sb.append(rutaDocumentosGenerados).append("/").append(nombreDocumento).toString());
		         
		         System.out.println("SE GENERO EL DOCUMENTO");
		         String asunto = "Orden Irrevocable - "+listaAsociados.get(0).getNombreCompleto();
		         mailService.sendMail("desarrollo@pandero.com.pe", documentoEmailTo, asunto, nombreDocumento);
		     }
			
		}catch(Exception e){
			LOG.error("###Error:",e);
			e.printStackTrace();
		}
		
	}
	
	private List<Parametro> getParametrosOrdenIrrevocable(double sumaContratos, double sumaInversiones, double saldo, String pedidoId) {
		List<Parametro> listParam=null;		
		try{
			listParam = new ArrayList<>();
			
			Parametro parametro = new Parametro("$fecha", MetodoUtil.getDateFormat(new Date(),Constantes.FORMATO_DATE_NORMAL));
			listParam.add(parametro);
			
			parametro = new Parametro("$hora", MetodoUtil.getDateFormat(new Date(),Constantes.FORMATO_DATE_HH_MIN_SS));
			listParam.add(parametro);
			
			String nroPedido = "P000"+pedidoId;
			parametro = new Parametro("$numeroInversion", nroPedido);
			listParam.add(parametro);
			
			parametro = new Parametro("$tablaInversiones", "TABLA INVERSIONISTA");
			listParam.add(parametro);
						
			parametro = new Parametro("$importeInversion", String.valueOf(sumaInversiones));
			listParam.add(parametro);
			
			parametro = new Parametro("$importeTotal", String.valueOf(sumaContratos));
			listParam.add(parametro);
			
			parametro = new Parametro("$diferenciaPrecio", "0.0");
			listParam.add(parametro);
			
			parametro = new Parametro("$otrosIngresos", "0.0");
			listParam.add(parametro);
			
			parametro = new Parametro("$saldoInversion", String.valueOf(saldo));
			listParam.add(parametro);
			
			parametro = new Parametro("$texto1", "TEXTO 1");
			listParam.add(parametro);
			
			parametro = new Parametro("$tablaFirmas", "TABLA FIRMAS");
			listParam.add(parametro);
			
		}catch(Exception e){
			LOG.error("###Error:",e);
			return listParam;
		}
		return listParam;
	}
	
}
