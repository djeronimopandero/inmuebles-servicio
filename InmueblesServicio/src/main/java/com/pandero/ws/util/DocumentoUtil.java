package com.pandero.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.Constante;
import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.ObservacionInversion;
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
									
									LOG.info("text >>"+text+"<<");
									
									if (text.contains("$tabla1")) {
										LOG.info("EN $tabla1");
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
												row.getCell(0).setText("NOMBRE DEL ASOCIADO:");
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
										row3.getCell(0).setText("CONTRATOS POR APLICAR:");
										row3.getCell(1).setText("NRO CONTRATO");
										row3.getCell(2).setText("IMPORTE");
										row3.getCell(3).setText("FECHA ADJ.");
										for(Contrato contrato : listaContratos){
											row3 = t1.createRow();
											row3.getCell(0).setText("");
											row3.getCell(1).setText(contrato.getNroContrato());
											row3.getCell(2).setText(Util.getMontoFormateado(contrato.getMontoCertificado()));
											row3.getCell(3).setText(contrato.getFechaAdjudicacion());
										}
										
										XWPFTableRow row4=t1.createRow();
										row4.getCell(0).setText("FECHA DE COMPRA:");	
										row4.getCell(1).setText(Util.getDateFormat(Util.getFechaActual(),Constantes.FORMATO_DATE_NORMAL));
									
									}else if(text.contains("$tablaInversiones")){
										
										LOG.info("EN $tablaInversiones");
										/** Tabla de inversiones **/
										text = text.replace("$tablaInversiones", "");
										r.setText(text, 0);
										LOG.info("EN TABLA INVERSIONES");
										
										XmlCursor cursor = p.getCTP().newCursor();
										XWPFTable t1 = doc.insertNewTbl(cursor);
										t1.getCTTbl().getTblPr().unsetTblBorders();
										int x=1;
										for(Inversion inversion : listaInversiones){
											XWPFTableRow row = null;
											if(x==1){
												row=t1.getRow(0);
												row.getCell(0).setText("DESCRIPCIÓN N° "+x);
												row.addNewTableCell().setText("INVERSIÓN: "+inversion.getNroInversion());
											}else{
												row = t1.createRow();
												row.getCell(0).setText("________________________");
												row.getCell(1).setText("__________________________________________");
												row=t1.createRow();
												row.getCell(0).setText("DESCRIPCIÓN N° "+x);
												row.getCell(1).setText("INVERSIÓN: "+inversion.getNroInversion());
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
												row.getCell(0).setText("RAZÓN SOCIAL: "+asociado.getNombreCompleto());
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
				for (XWPFParagraph p : header.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);

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
				for (XWPFParagraph p : footer.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);
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
		row.getCell(0).setText("TIPO DE INVERSIÓN:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));
		row = t1.createRow();
		row.getCell(0).setText("SITUACIÓN:");
		if(inversion.getServicioConstructora()){
			row.getCell(1).setText("CON SERVICIO DE CONSTRUCTORA");
			row = t1.createRow();
			row.getCell(0).setText("DATOS DE LA CONSTRUCTORA");
			if(Util.esPersonaJuridica(inversion.getConstructoraTipoDoc())){
				row = t1.createRow();
				row.getCell(0).setText("RAZÓN SOCIAL:");
				row.getCell(1).setText(inversion.getConstructoraRazonSocial());														
			}else{
				row = t1.createRow();
				row.getCell(0).setText("CONSTRUCTORA:");
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
		row.getCell(0).setText("DIRECCIÓN:");
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
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
		}
		
		row = t1.createRow();
		UtilEnum.TIPO_DOCUMENTO tipoDocumento = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(null!=inversion.getPropietarioTipoDocId()?Integer.parseInt(inversion.getPropietarioTipoDocId()):59);
		LOG.info("###TipoDoc:"+tipoDocumento.getTexto());
		row.getCell(0).setText(tipoDocumento.getTexto());
		row.getCell(1).setText(inversion.getPropietarioNroDoc());
		
		row = t1.createRow();
		row.getCell(0).setText("DESCRIPCIÓN DE OBRA:");
		row.getCell(1).setText(inversion.getDescripcionObra());
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSIÓN ($):");
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getImporteInversion()));		
		if(inversion.getObservacion()!=null && !inversion.getObservacion().equals("")){
			row = t1.createRow();
			row.getCell(0).setText("OBSERVACIÓN:");
			row.getCell(1).setText(inversion.getObservacion());
		}	
		
		return row;
	}
	
	public static XWPFTableRow completarInversionCancelacion(XWPFTable t1, XWPFTableRow row, Inversion inversion, List<Constante> listaDocuIdentidad){
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INVERSIÓN:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));
		row = t1.createRow();
		row.getCell(0).setText("ENTIDAD FINANCIERA:");
		row.getCell(1).setText(inversion.getEntidadFinancieraNom());
		row = t1.createRow();
		row.getCell(0).setText("NRO CRÉDITO:");
		row.getCell(1).setText(inversion.getNroCredito());
		row = t1.createRow();
		row.getCell(0).setText("SECTORISTA:");
		row.getCell(1).setText(inversion.getSectorista());
		row = t1.createRow();
		row.getCell(0).setText("TELÉFONO:");
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
		row.getCell(0).setText("DIRECCIÓN:");
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
		row.getCell(0).setText("ÁREA TOTAL (M2):");
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
		}
		
		row = t1.createRow();
		UtilEnum.TIPO_DOCUMENTO tipoDocumento = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(null!=inversion.getPropietarioTipoDocId()?Integer.parseInt(inversion.getPropietarioTipoDocId()):59);
		LOG.info("###TipoDoc:"+tipoDocumento.getTexto());
		row.getCell(0).setText(tipoDocumento.getTexto());
		row.getCell(1).setText(inversion.getPropietarioNroDoc());
		
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSIÓN ($):");
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getImporteInversion()));		
		if(inversion.getObservacion()!=null && !inversion.getObservacion().equals("")){
			row = t1.createRow();
			row.getCell(0).setText("OBSERVACIÓN:");
			row.getCell(1).setText(inversion.getObservacion());
		}		
		return row;
	}
	
	public static XWPFTableRow completarInversionAdquisicion(XWPFTable t1, XWPFTableRow row, Inversion inversion, List<Constante> listaDocuIdentidad){
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INVERSIÓN:");
		row.getCell(1).setText(Util.getTipoInversionNombre(inversion.getTipoInversion()));			
		row = t1.createRow();
		row.getCell(0).setText("DATOS DEL INMUEBLE");
		row = t1.createRow();
		row.getCell(0).setText("TIPO DE INMUEBLE:");
		row.getCell(1).setText(inversion.getTipoInmuebleNom());
		row = t1.createRow();
		row.getCell(0).setText("PARTIDA REGISTRAL:");
		row.getCell(1).setText(inversion.getPartidaRegistral());
		row = t1.createRow();
		row.getCell(0).setText("DIRECCIÓN:");
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
		row.getCell(0).setText("ÁREA TOTAL (M2):");
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getAreaTotal()));
		row = t1.createRow();
		row.getCell(0).setText("PROPIETARIO:");
		if(Util.esPersonaJuridica(inversion.getPropietarioTipoDocId())){
			row.getCell(1).setText(inversion.getPropietarioRazonSocial());
			
			row = t1.createRow();
			UtilEnum.TIPO_DOCUMENTO tipoDocumento = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(null!=inversion.getPropietarioTipoDocId()?Integer.parseInt(inversion.getPropietarioTipoDocId()):59);
			LOG.info("###TipoDoc:"+tipoDocumento.getTexto());
			row.getCell(0).setText(tipoDocumento.getTexto());
			row.getCell(1).setText(inversion.getPropietarioNroDoc());
			
			row = t1.createRow();
			row.getCell(0).setText("REPRESENTANTE LEGAL:");
			row.getCell(1).setText(inversion.getRepresentanteNombres()+" "+inversion.getRepresentanteApePaterno()+" "+inversion.getRepresentanteApeMaterno());
			row = t1.createRow();
			row.getCell(0).setText("DNI:");
			row.getCell(1).setText(inversion.getRepresentanteNroDoc());
		}else{
			row.getCell(1).setText(inversion.getPropietarioNombres()+" "+inversion.getPropietarioApePaterno()+" "+inversion.getPropietarioApeMaterno());
			
			row = t1.createRow();
			UtilEnum.TIPO_DOCUMENTO tipoDocumento = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(null!=inversion.getPropietarioTipoDocId()?Integer.parseInt(inversion.getPropietarioTipoDocId()):59);
			LOG.info("###TipoDoc:"+tipoDocumento.getTexto());
			row.getCell(0).setText(tipoDocumento.getTexto());
			row.getCell(1).setText(inversion.getPropietarioNroDoc());
		}		
		row = t1.createRow();
		row.getCell(0).setText("IMP. INVERSIÓN ($):");
		row.getCell(1).setText(Util.getMontoFormateado(inversion.getImporteInversion()));		
		if(inversion.getObservacion()!=null && !inversion.getObservacion().equals("")){
			row = t1.createRow();
			row.getCell(0).setText("OBSERVACIÓN:");
			row.getCell(1).setText(inversion.getObservacion());
		}		
		
		return row;
	}
	
	public void saveDocument(XWPFDocument doc, String file) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			doc.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Parametro> getParametrosOrdenIrrevocable(double sumaContratos, double sumaInversiones, double saldo, String pedidoId, String pedidoNumero,double diferenciaPrecio) {
		List<Parametro> listParam=null;		
		try{
			listParam = new ArrayList<>();
			
			Parametro parametro = new Parametro("$fecha", Util.getDateFormat(new Date(),Constantes.FORMATO_DATE_NORMAL));
			listParam.add(parametro);
			
			
			parametro = new Parametro("$hora", Util.getDateFormat(new Date(),Constantes.FORMATO_DATE_HH_MIN_SS));
			listParam.add(parametro);
			
			parametro = new Parametro("$numeroInversion", pedidoNumero);
			listParam.add(parametro);
			
			parametro = new Parametro("$tablaInversiones", "TABLA INVERSIONISTA");
			listParam.add(parametro);
						
			parametro = new Parametro("$importeInversion", Util.getMontoFormateado(sumaInversiones));
			listParam.add(parametro);
			
			parametro = new Parametro("$importeTotal", Util.getMontoFormateado(sumaContratos));
			listParam.add(parametro);
			System.out.println("diferenciaPrecio:: "+diferenciaPrecio);
			parametro = new Parametro("$diferenciaPrecio", Util.getMontoFormateado(diferenciaPrecio));
			listParam.add(parametro);
			
			parametro = new Parametro("$otrosIngresos", Util.getMontoFormateado(new Double(0.0)));
			listParam.add(parametro);
			
			parametro = new Parametro("$saldoInversion", Util.getMontoFormateado(saldo));
			listParam.add(parametro);
			
			parametro = new Parametro("$texto1", "TEXTO 1");
			listParam.add(parametro);
			
			parametro = new Parametro("$tablaFirmas", "TABLA FIRMAS");
			listParam.add(parametro);
			
			parametro = new Parametro("$guionFirmaPandero", "___________________________");
			listParam.add(parametro);
			
			parametro = new Parametro("$firmaPandero", "PANDERO S.A. EAFC");
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
				System.out.println("inversion.getDocumentosRequeridos():: "+inversion.getDocumentosRequeridos());
				System.out.println("listaDocuSelec:: "+listaDocuSelec.length);
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
	
	/*Prueba de concepto*/
	public static void convertDocxToPdf(String filePath,String filePdf){
		try{
			
	        FileInputStream fInputStream = new FileInputStream(new File(filePath));
	        XWPFDocument document = new XWPFDocument(fInputStream);
	
	        File outFile = new File(filePdf);
	        outFile.getParentFile().mkdirs();
	
	        OutputStream out = new FileOutputStream(outFile);
	        PdfOptions options = PdfOptions.create().fontEncoding("iso-8859-15");
	        PdfConverter.getInstance().convert(document, out, options);
	    
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public static XWPFDocument replaceTextCartaValidacion(XWPFDocument doc, List<Parametro> params, List<ObservacionInversion> listObservacion) {
		//************************************************ TEXTO *********************************************
		for(int i=0; i<doc.getParagraphs().size(); i++){
			XWPFParagraph p=doc.getParagraphs().get(i);
			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {
				for (XWPFRun r : runs) {
					String text = r.getText(0);
					if (text != null) {
						if (null != params) {
							for (Parametro param : params) {
								if (null != param) {
									if (text.contains(param.getKey())) {
										text = text.replace(param.getKey(), param.getValue());
										r.setText(text, 0);
									}
									if(text.contains("$TablaObservaciones")){
										text = text.replace("$TablaObservaciones", "");
										r.setText(text, 0);
										
										for( ObservacionInversion obs:listObservacion){
											
											p.insertNewRun(i);
											XWPFRun newRun = r;
											CTRPr rPr = newRun.getCTR().isSetRPr() ? newRun.getCTR().getRPr() : newRun.getCTR().addNewRPr();
											rPr.set(r.getCTR().getRPr());
											newRun.setText(StringUtils.isEmpty(obs.getObservacion())?"":"- "+obs.getObservacion());
											newRun.addCarriageReturn();
										}
										
									}
								}
							}
						}
					}
				}
			}
		}
				
		//************************************************ TABLA *********************************************
		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {
						for (XWPFRun r : p.getRuns()) {
							String text = r.getText(0);
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
			
			//************************************************ HEADER *********************************************
			XWPFHeader header = policy.getDefaultHeader();
			if (null != header) {
				for (XWPFParagraph p : header.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);
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
			//*******************************************************************************************************

			//************************************************ FOOTER *********************************************
			XWPFFooter footer = policy.getDefaultFooter();
			if (null != footer) {
				LOG.info("---------------------------------------FOOTER----------------------------------------");
				for (XWPFParagraph p : footer.getParagraphs()) {
					List<XWPFRun> runs = p.getRuns();
					if (runs != null) {
						for (XWPFRun r : runs) {
							String text = r.getText(0);
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
	
	/**
	  * @param contratos
	  * @param asociado
	  * @param tipoInmueble
	  * @param libreGravamen
	  * @param partidaRegistrar
	  * @param importeInversion
	  * @param areaTotal
	  * @param tipoInversion
	  * @param nroInversion
	  * @return	: String
	  * @date	: 15 de dic. de 2016
	  * @time	: 10:26:25 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Generar el html para ser enviado al generar una carta de validación cuando existen requisitos como NO CONFORME
	 */
	public static String getHtmlCartaValidacionNoConforme(String contratos,String asociado,String tipoInmueble,String libreGravamen,String partidaRegistrar,String importeInversion,String areaTotal,String tipoInversion,String nroInversion,List<ObservacionInversion> listObs){
		StringBuilder strHtml= new StringBuilder("");
		
		strHtml.append("<head>");
		strHtml.append("</head>");
		strHtml.append("<body>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>CC</td>");
				strHtml.append("<td>:"+contratos+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Asociado</td>");
				strHtml.append("<td>:"+asociado+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Se ha finalizado la verificación de la inversión inmobiliaria Nro. "+nroInversion+" quedando algunas revisiones en estado NO CONFORME, se solicita emitir la carta de validación de datos para ser remitido al Asociado.</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		
		strHtml.append("</br>");
		
		if(null!=listObs){
			strHtml.append("<table>");
			for(int i=0; i<listObs.size();i++){
				strHtml.append("<tr>");
					strHtml.append("<td>Observación Nro. "+(i+1)+"</td>");
					strHtml.append("<td>:"+listObs.get(i).getObservacion()+"</td>");
				strHtml.append("</tr>");
			}
			strHtml.append("</table>");
		}
		
		strHtml.append("</body>");
		strHtml.append("</html>");
		
		return strHtml.toString();
	}
	
	 /**
	  * @param contratos
	  * @param asociado
	  * @param tipoInmueble
	  * @param libreGravamen
	  * @param partidaRegistrar
	  * @param importeInversion
	  * @param areaTotal
	  * @param tipoInversion
	  * @param nroInversion
	  * @return	: String
	  * @date	: 15 de dic. de 2016
	  * @time	: 11:05:22 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : 	Generar el html para ser enviado al generar una carta de validación cuando el estado es CONFORME
	 */
	public static String getHtmlCartaValidacionConforme(String contratos,String asociado,String tipoInmueble,String libreGravamen,String partidaRegistrar,String importeInversion,String areaTotal,String tipoInversion,String nroInversion){
		StringBuilder strHtml= new StringBuilder("");
		
		strHtml.append("<head>");
		strHtml.append("</head>");
		strHtml.append("<body>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>CC</td>");
				strHtml.append("<td>:"+contratos+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Asociado</td>");
				strHtml.append("<td>:"+asociado+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Se ha finalizado la verificación de la inversión inmobiliaria Nro. "+nroInversion+" quedando todas las revisiones en estado CONFORME, se solicita continuar con: el registro de comprobante(s) de pago emitidos por el proveedor/vendedor del inmueble; la actualización del saldo de la deuda; la liquidación de los fondos disponibles.</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inversión</td>");
				strHtml.append("<td>:"+tipoInversion+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inmueble</td>");
				strHtml.append("<td>:"+tipoInmueble+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Libre de gravamen</td>");
				strHtml.append("<td>:"+libreGravamen+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Área total (m2)</td>");
				strHtml.append("<td>:"+areaTotal+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Partida registral</td>");
				strHtml.append("<td>:"+partidaRegistrar+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Importe de inversión (US$)</td>");
				strHtml.append("<td>:"+importeInversion+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</body>");
		strHtml.append("</html>");
		
		return strHtml.toString();
	}
	
	public static String getHtmlConstanciaDesembolsoParcial(String nroInversion,String contratos,String asociado,String tipoInversion,String tipoInmueble,String libreGravamen,String areaTotal,String partidaRegistrar,String importeInversion,String importeDesembolsoParcial){
		StringBuilder strHtml= new StringBuilder("");
		
		strHtml.append("<head>");
		strHtml.append("</head>");
		strHtml.append("<body>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>CC</td>");
				strHtml.append("<td>:"+contratos+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Asociado</td>");
				strHtml.append("<td>:"+asociado+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Se adjunta la constancia de desembolso parcial de la inversión Nro. "+nroInversion+". Solicitar la firma del Asociado.</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</br>");
		strHtml.append("<table>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inversión</td>");
				strHtml.append("<td>:"+tipoInversion+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Tipo de inmueble</td>");
				strHtml.append("<td>:"+tipoInmueble+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Libre de gravamen</td>");
				strHtml.append("<td>:"+libreGravamen+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Área total (m2)</td>");
				strHtml.append("<td>:"+areaTotal+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Partida registral</td>");
				strHtml.append("<td>:"+partidaRegistrar+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
				strHtml.append("<td>Importe de inversión (US$)</td>");
				strHtml.append("<td>:"+importeInversion+"</td>");
			strHtml.append("</tr>");
			strHtml.append("<tr>");
			strHtml.append("<td>Importe de desembolso parcial (US$)</td>");
			strHtml.append("<td>:"+importeDesembolsoParcial+"</td>");
			strHtml.append("</tr>");
		strHtml.append("</table>");
		strHtml.append("</body>");
		strHtml.append("</html>");
		
		return strHtml.toString();
	}
	
	public static XWPFDocument replaceParamsDocumentoDesembolso(XWPFDocument doc, List<Parametro> params,List<Asociado> asociados) {

		LOG.info("---------------------------------------TEXTO----------------------------------------");
		for(int i=0; i<doc.getParagraphs().size(); i++){
			XWPFParagraph p=doc.getParagraphs().get(i);
			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {
				for (XWPFRun r : runs) {
					String text = r.getText(0);

					if (text != null) {
						if(text.contains("$firmas")){
							
							text = text.replace("$firmas", "");
							r.setText(text, 0);
							
							for(Asociado asociado:asociados){
								
								p.insertNewRun(i);
								XWPFRun newRun = r;
								CTRPr rPr = newRun.getCTR().isSetRPr() ? newRun.getCTR().getRPr() : newRun.getCTR().addNewRPr();
								rPr.set(r.getCTR().getRPr());
								newRun.setText("___________________________");
								newRun.addCarriageReturn();
								
								p.insertNewRun(i);
								XWPFRun newRun2 = r;
								CTRPr rPr2 = newRun2.getCTR().isSetRPr() ? newRun2.getCTR().getRPr() : newRun2.getCTR().addNewRPr();
								rPr2.set(r.getCTR().getRPr());
								newRun2.setText("Asociado: " + asociado.getNombreCompleto());
								newRun2.addCarriageReturn();
								
								p.insertNewRun(i);
								XWPFRun newRun3 = r;
								CTRPr rPr3 = newRun3.getCTR().isSetRPr() ? newRun3.getCTR().getRPr() : newRun3.getCTR().addNewRPr();
								rPr3.set(r.getCTR().getRPr());
								newRun3.setText(asociado.getTipoDocumentoIdentidad()+": " + asociado.getNroDocumentoIdentidad());
								newRun3.addCarriageReturn();
							}
							
							
						}else{
							if (null != params) {
								for (Parametro param : params) {
									if (null != param) {
										LOG.info("text :"+text+", param.getKey():"+param.getKey());
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

		LOG.info("---------------------------------------TABLA----------------------------------------");

//		for (XWPFTable tbl : doc.getTables()) {
//			for (XWPFTableRow row : tbl.getRows()) {
//				for (XWPFTableCell cell : row.getTableCells()) {
//					for (XWPFParagraph p : cell.getParagraphs()) {
//						for (XWPFRun r : p.getRuns()) {
//							String text = r.getText(0);
//							if (text != null) {
//								if (null != params) {
//									for (Parametro param : params) {
//										if (null != param) {
//											if (text.contains(param.getKey())) {
//												text = text.replace(param.getKey(), param.getValue());
//												r.setText(text, 0);
//											}
//										}
//									}
//								}
//							}
//
//						}
//					}
//				}
//			}
//		}
		
		return doc;
	}
}
