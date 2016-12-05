package com.pandero.ws.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.Parametro;

public class DocumentoUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(DocumentoUtil.class);

	public XWPFDocument openDocument(String file) throws Exception {
		LOG.info("###openDocument : " + file);
		XWPFDocument document = null;
		try {
			document = new XWPFDocument(OPCPackage.open(file));
			LOG.info("###Se abrio el documento");
		} catch (Exception e) {
			LOG.info("###Error-openDocument: ", e);
			e.printStackTrace();
		}
		return document;
	}
	
	public static XWPFDocument replaceTextOrdenIrrevocable(XWPFDocument doc, List<Parametro> params, List<Constante> listaDocuIdentidad, List<Asociado> listaAsociados, List<Contrato> listaContratos, List<Inversion> listaInversiones) {

		LOG.info("---------------------------------------TEXTO----------------------------------------");
		for(int i=0; i<doc.getParagraphs().size(); i++){
			XWPFParagraph p=doc.getParagraphs().get(i);
			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {
				for (XWPFRun r : runs) {
					String text = r.getText(0);
					
//					System.out.println("### XWPFParagraph : " + text);

					if (text != null) {
						if (null != params) {
							for (Parametro param : params) {
								if (null != param) {
									System.out.println("text >>"+text+"<<");									
									if (text.contains("$tabla1")) {
										System.out.println("EN $tabla1");
										/** Tabla datos asociado y certificados **/
										text = text.replace("$tabla1", "");
										r.setText(text, 0);
										
										XmlCursor cursor = p.getCTP().newCursor();
										
										// Tabla Asociados
										XWPFTable t1 = doc.insertNewTbl(cursor);
										t1.getCTTbl().getTblPr().unsetTblBorders();
										int x=0;
										for(Asociado asociado : listaAsociados){
											XWPFTableRow row = null;
											if(x==0){
												row=t1.getRow(0);
												row.getCell(0).setText("NOMBRE ASOCIADO:");
												row.addNewTableCell().setText(asociado.getNombreCompleto());
												row.addNewTableCell().setText(asociado.getTipoDocumentoIdentidad()+":");
												row.addNewTableCell().setText(asociado.getNroDocumentoIdentidad());
											}else{												
												row = t1.createRow();
												row.getCell(0).setText("");
												row.getCell(1).setText(asociado.getNombreCompleto());
												row.getCell(2).setText(asociado.getTipoDocumentoIdentidad()+":");
												row.getCell(3).setText(asociado.getNroDocumentoIdentidad());
											}
											
											x++;
										}
										// Tabla Direccion
										XWPFTableRow row2=t1.createRow();
										row2.getCell(0).setText("DIRECCIÓN:");	
										row2.getCell(1).setText(listaAsociados.get(0).getDireccion());
										
										// Tabla Contratos
										XWPFTableRow row3=t1.createRow();										
										row3.getCell(0).setText("CONTRATOS X APLICAR:");
										row3.getCell(1).setText("NRO CONTRATO");
										row3.getCell(2).setText("IMPORTE");
										row3.getCell(3).setText("FECHA ADJ.");
										for(Contrato contrato : listaContratos){
											row3 = t1.createRow();
											row3.getCell(0).setText("");
											row3.getCell(1).setText(contrato.getNroContrato());
											row3.getCell(2).setText(String.valueOf(contrato.getMontoCertificado()));
											row3.getCell(3).setText(contrato.getFechaAdjudicacion());
										}
									
									}else if(text.contains("$tablaInversiones")){
										System.out.println("EN $tablaInversiones");
										/** Tabla de inversiones **/
										text = text.replace("$tablaInversiones", "");
										r.setText(text, 0);
										System.out.println("EN TABLA INVERSIONES");										
										XmlCursor cursor = p.getCTP().newCursor();
										XWPFTable t1 = doc.insertNewTbl(cursor);
										t1.getCTTbl().getTblPr().unsetTblBorders();
										int x=1;
										for(Inversion inversion : listaInversiones){
											XWPFTableRow row = null;
											if(x==1){
												row=t1.getRow(0);
												row.getCell(0).setText("DESCRIPCION N° "+x);
												row.addNewTableCell().setText("INVERSION: "+inversion.getNroInversion());
											}else{
												row = t1.createRow();
												row.getCell(0).setText("________________________");
												row.getCell(1).setText("__________________________________________");
												row=t1.createRow();
												row.getCell(0).setText("DESCRIPCION N° "+x);
												row.getCell(1).setText("INVERSION: "+inversion.getNroInversion());
											}
											
											if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
												row = completarInversionConstruccion(t1,row,inversion,listaDocuIdentidad);
											}else if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
												row = completarInversionCancelacion(t1,row,inversion,listaDocuIdentidad);
											}else if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
												row = completarInversionAdquisicion(t1,row,inversion,listaDocuIdentidad);
											}											
											x++;
										}
										
									}else if(text.contains("$texto1")){
										System.out.println("EN $texto1");
										/** Tabla de Anotaciones **/
										text = text.replace("$texto1", "");
										r.setText(text, 0);
										System.out.println("EN ANOTACIONES");										
										XmlCursor cursor = p.getCTP().newCursor();
										XWPFTable t1 = doc.insertNewTbl(cursor);
										t1.getCTTbl().getTblPr().unsetTblBorders();
										int x=1;
										for(Inversion inversion : listaInversiones){
											XWPFTableRow row = null;											
											if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
												row=t1.createRow();
												row.getCell(0).setText("(1) SUJETO A VERIFICACIÓN PREVIO AL DESEMBOLSO DEL CERTIFICADO.");
											}else if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
												row=t1.createRow();
												row.getCell(0).setText("(2) EL SALDO A CANCELAR A LA ENTIDAD FINANCIERA DEBERÁ SER ACTUALIZADO POR EL ASOCIADO PREVIO AL DESEMBOLSO DEL CERTIFICADO, SI EL FONDO DISPONIBLE DEL(LOS) CERTIFICADO(S)  NO ES SUFICIENTE PARA CANCELAR EL CRÉDITO, ES RESPONSABILIDAD DEL ASOCIADO ACREDITAR LA CANCELACIÓN DE LA DIFERENCIA DE PRECIO. SÓLO ASÍ PANDERO S.A EAFC HARÁ EFECTIVO EL DESEMBOLSO DEL CERTIFICADO QUE GARANTICE LA CANCELACIÓN DEL CRÉDITO HIPOTECARIO.");
											}else if(Constantes.TipoInversion.ADQUISICION_COD.equals(inversion.getTipoInversion())){
												row=t1.createRow();
												row.getCell(0).setText("(3) SI EL FONDO DISPONIBLE DEL(LOS) CERTIFICADO(S) NO ES SUFICIENTE PARA LA ADQUISICIÓN DEL INMUEBLE, ES RESPONSABILIDAD DEL ASOCIADO ACREDITAR LA CANCELACIÓN DE LA DIFERENCIA DE PRECIO. SÓLO ASÍ PANDERO S.A EAFC HARÁ EFECTIVO EL DESEMBOLSO DEL CERTIFICADO PARA LA ADQUISICIÓN DE SU INMUEBLE");
											}
											x++;
										}
										
									}else if(text.contains("$tablaFirmas")){
										System.out.println("EN $tablaFirmas");
										/** Tabla de Firmas **/
										text = text.replace("$tablaFirmas", "");
										r.setText(text, 0);
										System.out.println("EN TABLA FIRMAS");										
										XmlCursor cursor = p.getCTP().newCursor();
										XWPFTable t1 = doc.insertNewTbl(cursor);
										t1.getCTTbl().getTblPr().unsetTblBorders();
										for(Asociado asociado : listaAsociados){
											XWPFTableRow row = null;				
											row=t1.createRow();
											row.getCell(0).setText("___________________________");
											if(Util.esPersonaJuridica(asociado.getTipoDocumentoIdentidad())){
												row=t1.createRow();
												row.getCell(0).setText("RAZON SOCIAL: "+asociado.getNombreCompleto());
											}else{
												row=t1.createRow();
												row.getCell(0).setText("NOMBRE: "+asociado.getNombreCompleto());
											}
											row=t1.createRow();
											row.getCell(0).setText(asociado.getTipoDocumentoIdentidad()+": "+asociado.getNroDocumentoIdentidad());	
										}
										
									}
																	
									
									if (text.contains(param.getKey())) {
										text = text.replace(param.getKey(), param.getValue());
										r.setText(text, 0);
									}
									
								}
							}
						}
					}

				}
			}
		}

		LOG.info("---------------------------------------TABLA----------------------------------------");

		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {
						for (XWPFRun r : p.getRuns()) {
							String text = r.getText(0);
//							LOG.info("### XWPFTable : " + text);

							if (text != null) {
								if (null != params) {
									for (Parametro param : params) {
										if (null != param) {
											if (text.contains(param.getKey())) {
												text = text.replace(param.getKey(), param.getValue());
												r.setText(text, 0);
											}

										}
									}
								}
							}

						}
					}
				}
			}
		}

		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc);

		if (null != policy) {

			// read header
			XWPFHeader header = policy.getDefaultHeader();

			if (null != header) {
				LOG.info("---------------------------------------HEADER----------------------------------------");
//				LOG.info("HEADER:" + header.getText());
				for (XWPFParagraph p : header.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);

//							LOG.info("### XWPFParagraph : " + text);
//System.out.println("HEADER>>"+text+"<<");
							if (text != null) {
								if (null != params) {
									for (Parametro param : params) {
										if (null != param) {
											if (text.contains(param.getKey())) {
												text = text.replace(param.getKey(), param.getValue());
												r.setText(text, 0);
											}
										}
									}
								}
							}

						}
					}
				}
			}

			// read footer
			XWPFFooter footer = policy.getDefaultFooter();

			if (null != footer) {

				LOG.info("---------------------------------------FOOTER----------------------------------------");
//				LOG.info("FOOTER:" + footer.getText());
				for (XWPFParagraph p : footer.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);

//							LOG.info("### XWPFParagraph : " + text);

							if (text != null) {
								if (null != params) {
									for (Parametro param : params) {
										if (null != param) {
											if (text.contains(param.getKey())) {
												text = text.replace(param.getKey(), param.getValue());
												r.setText(text, 0);
											}
										}
									}
								}
							}

						}
					}
				}
			}
		}
		return doc;
	}
	
	public static XWPFTableRow completarInversionConstruccion(XWPFTable t1, XWPFTableRow row, Inversion inversion, List<Constante> listaDocuIdentidad){
		row = t1.createRow();
		row.getCell(0).setText("TIPO INVERSION:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));
		row = t1.createRow();
		row.getCell(0).setText("SITUACION:");
		if(inversion.getServicioConstructora()){
			row.getCell(1).setText("CON SERVICIO DE CONSTRUCTORA");
			row = t1.createRow();
			row.getCell(0).setText("DATOS DE LA CONSTRUCTORA");
//				row.getCell(1).setText("");	
			if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
				row = t1.createRow();
				row.getCell(0).setText("RAZON SOCIAL:");
				row.getCell(1).setText(inversion.getConstructoraRazonSocial());														
			}else{
				row = t1.createRow();
				row.getCell(0).setText("PERSONA CONSTRUCTORA:");
				row.getCell(1).setText(inversion.getConstructoraNombres()+" "+inversion.getConstructoraApePaterno()+" "+inversion.getConstructoraApeMaterno());
			}
			row = t1.createRow();
			row.getCell(0).setText(Util.getDocuIdentidadNombre(listaDocuIdentidad, inversion.getConstructoraTipoDoc())+":");
			row.getCell(1).setText(inversion.getConstructoraNroDoc());
			if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
				row = t1.createRow();
				row.getCell(0).setText("CONTACTO:");
				row.getCell(1).setText(inversion.getConstructoraContacto());
			}
			row = t1.createRow();
			row.getCell(0).setText("TELEFONO:");
			row.getCell(1).setText(inversion.getConstructoraTelefono());
		}else{
			row.getCell(1).setText("SIN SERVICIO DE CONSTRUCTORA");
		}
		row = t1.createRow();
		row.getCell(0).setText("DATOS DEL INMUEBLE");
//			row.getCell(1).setText(inversion.getConstructoraTelefono());
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INMUEBLE:");
		row.getCell(1).setText(inversion.getTipoInmuebleNom());
		row = t1.createRow();
		row.getCell(0).setText("PARTIDA REGISTRAL:");
		row.getCell(1).setText(inversion.getPartidaRegistral());
		row = t1.createRow();
		row.getCell(0).setText("DIRECCION:");
		row.getCell(1).setText(inversion.getDireccion());
		row = t1.createRow();
		row.getCell(0).setText("DEPARTAMENTO:");
		row.getCell(1).setText(Util.getDepartamentoNombre(inversion.getDepartamentoId()));
		row = t1.createRow();
		row.getCell(0).setText("PROVINCIA:");
		row.getCell(1).setText(Util.getProvinciaNombre(inversion.getProvinciaId()));
		row = t1.createRow();
		row.getCell(0).setText("DISTRITO:");
		row.getCell(1).setText(inversion.getDistritoNom());
		row = t1.createRow();
		row.getCell(0).setText("AREA TOTAL (M2):");
		row.getCell(1).setText(String.valueOf(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
		}
		row = t1.createRow();
		row.getCell(0).setText("DESCRIPCION DE OBRA:");
		row.getCell(1).setText(inversion.getDescripcionObra());
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSION ($):");
		row.getCell(1).setText(String.valueOf(inversion.getImporteInversion()));		
		
		return row;
	}
	
	public static XWPFTableRow completarInversionCancelacion(XWPFTable t1, XWPFTableRow row, Inversion inversion, List<Constante> listaDocuIdentidad){
		row = t1.createRow();
		row.getCell(0).setText("TIPO INVERSION:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));
		row = t1.createRow();
		row.getCell(0).setText("ENTIDAD FINANCIERA:");
		row.getCell(1).setText(inversion.getEntidadFinancieraNom());
		row = t1.createRow();
		row.getCell(0).setText("NRO CREDITO:");
		row.getCell(1).setText(inversion.getNroCredito());
		row = t1.createRow();
		row.getCell(0).setText("SECTORISTA:");
		row.getCell(1).setText(inversion.getSectorista());
		row = t1.createRow();
		row.getCell(0).setText("TELEFONO:");
		row.getCell(1).setText(inversion.getTelefonoContacto());			
		row = t1.createRow();
		row.getCell(0).setText("DATOS DEL INMUEBLE");
//			row.getCell(1).setText(inversion.getConstructoraTelefono());
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INMUEBLE:");
		row.getCell(1).setText(inversion.getTipoInmuebleNom());
		row = t1.createRow();
		row.getCell(0).setText("PARTIDA REGISTRAL:");
		row.getCell(1).setText(inversion.getPartidaRegistral());
		row = t1.createRow();
		row.getCell(0).setText("DIRECCION:");
		row.getCell(1).setText(inversion.getDireccion());
		row = t1.createRow();
		row.getCell(0).setText("DEPARTAMENTO:");
		row.getCell(1).setText(Util.getDepartamentoNombre(inversion.getDepartamentoId()));
		row = t1.createRow();
		row.getCell(0).setText("PROVINCIA:");
		row.getCell(1).setText(Util.getProvinciaNombre(inversion.getProvinciaId()));
		row = t1.createRow();
		row.getCell(0).setText("DISTRITO:");
		row.getCell(1).setText(inversion.getDistritoNom());
		row = t1.createRow();
		row.getCell(0).setText("AREA TOTAL (M2):");
		row.getCell(1).setText(String.valueOf(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
		}		
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSION ($):");
		row.getCell(1).setText(String.valueOf(inversion.getImporteInversion()));		
		if(inversion.getObservacion()!=null && !inversion.getObservacion().equals("")){
			row = t1.createRow();
			row.getCell(0).setText("OBSERVACION:");
			row.getCell(1).setText(inversion.getObservacion());
		}		
		return row;
	}
	
	public static XWPFTableRow completarInversionAdquisicion(XWPFTable t1, XWPFTableRow row, Inversion inversion, List<Constante> listaDocuIdentidad){
		row = t1.createRow();
		row.getCell(0).setText("TIPO INVERSION:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));			
		row = t1.createRow();
		row.getCell(0).setText("DATOS DEL INMUEBLE");
//			row.getCell(1).setText(inversion.getConstructoraTelefono());
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INMUEBLE:");
		row.getCell(1).setText(inversion.getTipoInmuebleNom());
		row = t1.createRow();
		row.getCell(0).setText("PARTIDA REGISTRAL:");
		row.getCell(1).setText(inversion.getPartidaRegistral());
		row = t1.createRow();
		row.getCell(0).setText("DIRECCION:");
		row.getCell(1).setText(inversion.getDireccion());
		row = t1.createRow();
		row.getCell(0).setText("DEPARTAMENTO:");
		row.getCell(1).setText(Util.getDepartamentoNombre(inversion.getDepartamentoId()));
		row = t1.createRow();
		row.getCell(0).setText("PROVINCIA:");
		row.getCell(1).setText(Util.getProvinciaNombre(inversion.getProvinciaId()));
		row = t1.createRow();
		row.getCell(0).setText("DISTRITO:");
		row.getCell(1).setText(inversion.getDistritoNom());
		row = t1.createRow();
		row.getCell(0).setText("AREA TOTAL (M2):");
		row.getCell(1).setText(String.valueOf(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
			row = t1.createRow();
			row.getCell(0).setText("REPRESENTANTE LEGAL:");
			row.getCell(1).setText(inversion.getRepresentanteNombres()+" "+inversion.getRepresentanteApePaterno()+" "+inversion.getRepresentanteApeMaterno());
			row = t1.createRow();
			row.getCell(0).setText("DNI:");
			row.getCell(1).setText(inversion.getRepresentanteNroDoc());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
		}		
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSION ($):");
		row.getCell(1).setText(String.valueOf(inversion.getImporteInversion()));		
		
		return row;
	}
	
	public void saveDocument(XWPFDocument doc, String file) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			doc.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Parametro> getParametrosOrdenIrrevocable(double sumaContratos, double sumaInversiones, double saldo, String pedidoId) {
		List<Parametro> listParam=null;		
		try{
			listParam = new ArrayList<>();
			
			Parametro parametro = new Parametro("$fecha", Util.getDateFormat(new Date(),Constantes.FORMATO_DATE_NORMAL));
			listParam.add(parametro);
			
			parametro = new Parametro("$hora", Util.getDateFormat(new Date(),Constantes.FORMATO_DATE_HH_MIN_SS));
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
	
	public static String validarConfirmarInversion(List<DocumentoRequisito> listaDocumentos, Inversion inversion) throws Exception {
		String resultado="";
		if(inversion!=null){									
			// Validar la lista de documentos
			boolean permiteConfirmar = true;
			if(inversion.getDocumentosRequeridos()!=null && !inversion.getDocumentosRequeridos().equals("")){
				String[] listaDocuSelec = inversion.getDocumentosRequeridos().split(",");
				if(listaDocuSelec.length!=listaDocumentos.size()){
					permiteConfirmar = false;
				}
			}else{
				permiteConfirmar = false;
			}
			if(permiteConfirmar==false) resultado=Constantes.Service.RESULTADO_PENDIENTE_DOCUMENTOS;
						
			if(permiteConfirmar){
				// Validar datos por tipo de inversion
				if(Constantes.TipoInversion.CANCELACION_COD.equals(inversion.getTipoInversion())){
					if(inversion.getEntidadFinancieraId()==null
							|| Util.esVacio(inversion.getNroCredito())
							|| Util.esVacio(inversion.getSectorista())
							|| Util.esVacio(inversion.getTelefonoContacto())){
						System.out.println("DATOS_PENDIENTES - 1");
						permiteConfirmar = false;
					}
				}else if(Constantes.TipoInversion.CONSTRUCCION_COD.equals(inversion.getTipoInversion())){
					if(inversion.getServicioConstructora()){
						if(!Util.esVacio(inversion.getConstructoraTipoDoc())){
							if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraRazonSocial())
										|| Util.esVacio(inversion.getConstructoraTelefono())
										|| Util.esVacio(inversion.getConstructoraContacto())){
									System.out.println("DATOS_PENDIENTES - 2");
								}
							}else{
								if(Util.esVacio(inversion.getConstructoraNroDoc())
										|| Util.esVacio(inversion.getConstructoraNombres())
										|| Util.esVacio(inversion.getConstructoraApePaterno())
										|| Util.esVacio(inversion.getConstructoraApeMaterno())
										|| Util.esVacio(inversion.getConstructoraTelefono())){
									System.out.println("DATOS_PENDIENTES - 3");
									permiteConfirmar = false;
								}
							}
						}else{
							System.out.println("DATOS_PENDIENTES - 4");
							permiteConfirmar = false;
						}
					}
					if(Util.esVacio(inversion.getDescripcionObra())){
						System.out.println("DATOS_PENDIENTES - 5");
						permiteConfirmar = false;
					}
				}
				
				// Validar datos del propietario
				if(permiteConfirmar){
					if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
						if(Util.esVacio(inversion.getPropietarioRazonSocial())
								|| Util.esVacio(inversion.getRepresentanteTipoDocId())
								|| Util.esVacio(inversion.getRepresentanteNroDoc())
								|| Util.esVacio(inversion.getRepresentanteApePaterno())
								|| Util.esVacio(inversion.getRepresentanteApeMaterno())
								|| Util.esVacio(inversion.getRepresentanteNombres())){
							System.out.println("DATOS_PENDIENTES - 6");
							permiteConfirmar = false;
						}
					}else{
						if(Util.esVacio(inversion.getPropietarioNombres())
								|| Util.esVacio(inversion.getPropietarioApePaterno())
								|| Util.esVacio(inversion.getPropietarioApeMaterno())){
							System.out.println("DATOS_PENDIENTES - 7");
							permiteConfirmar = false;
						}
					}
				}
				
				// Validar datos del beneficiario
				if(permiteConfirmar){
					if(!inversion.getBeneficiarioAsociado()){
						if(Util.esVacio(inversion.getBeneficiarioTipoDocId())
								|| Util.esVacio(inversion.getBeneficiarioNroDoc())
								|| Util.esVacio(inversion.getBeneficiarioNombreCompleto())
								|| inversion.getBeneficiarioRelacionAsociadoId()==null){
							System.out.println("DATOS_PENDIENTES - 8");
							permiteConfirmar = false;
						}
					}
				}
				if(permiteConfirmar==false) resultado=Constantes.Service.RESULTADO_DATOS_PENDIENTES;				
			}						
		}
				
		return resultado;
	}
}
