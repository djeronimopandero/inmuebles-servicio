package com.pandero.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

/**
 * The Class DocumentGenerator.
 */
public class DocumentGenerator {

	/**
	 * Generate odt from odt template.
	 *
	 * @param pathTemplate
	 *            the path template
	 * @param pathOutput
	 *            the path output
	 * @param contexto
	 *            the contexto
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws XDocReportException
	 *             the x doc report exception
	 * @throws XDocConverterException
	 *             the x doc converter exception
	 */
	public static File generateOdtFromOdtTemplate(File pathTemplate, File pathOutput, Map<String, Object> contexto)
			throws IOException, XDocReportException, XDocConverterException {

		InputStream in = new FileInputStream(pathTemplate);

		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

		IContext context = report.createContext();
		context.putMap(contexto);

		OutputStream out = new FileOutputStream(pathOutput);
		report.process(context, out);
		in.close();
		out.close();
		return pathOutput;
	}

	/**
	 * Generate PDF from ODT.
	 *
	 * @param pathTemplate
	 *            the path template
	 * @param pathOutput
	 *            the path output
	 * @param contexto
	 *            the contexto
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws XDocReportException
	 *             the x doc report exception
	 * @throws XDocConverterException
	 *             the x doc converter exception
	 */
	public static File generatePdfFromOdt(File pathTemplate, File pathOutput, Map<String, Object> contexto)
			throws IOException, XDocReportException, XDocConverterException {

		InputStream in = new FileInputStream(pathTemplate);

		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

		IContext context = report.createContext();
		context.putMap(contexto);

		Options options = Options.getTo(ConverterTypeTo.PDF);

		OutputStream out = new FileOutputStream(pathOutput);
		report.convert(context, options, out);
		in.close();
		out.close();
		return pathOutput;
	}

	/**
	 * Generate ods from ods template.
	 *
	 * @param pathTemplate
	 *            the path template
	 * @param pathOutput
	 *            the path output
	 * @param contexto
	 *            the contexto
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws XDocReportException
	 *             the x doc report exception
	 */
	public static File generateOdsFromOdsTemplate(File pathTemplate, File pathOutput, Map<String, Object> contexto)
			throws IOException, XDocReportException {
		InputStream in = new FileInputStream(pathTemplate);
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

		IContext context = report.createContext();
		context.putMap(contexto);

		OutputStream out = new FileOutputStream(pathOutput);
		report.process(context, out);
		in.close();
		out.close();
		return pathOutput;

	}

	/**
	 * Generate pdf from ods.
	 *
	 * @param pathTemplate
	 *            the path template
	 * @param pathOutput
	 *            the path output
	 * @return the file
	 * @throws ConnectException
	 *             the connect exception
	 */
	public static File generatePdfFromOds(File pathTemplate, File pathOutput) throws ConnectException {
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		connection.connect();
		try {
			DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
			converter.convert(pathTemplate, pathOutput);
		} catch (Exception e) {
			throw e;
		} finally {
			connection.disconnect();
		}

		return pathOutput;

	}

	/**
	 * Merge pdf files.
	 *
	 * @param listPdfFile
	 *            the list pdf file
	 * @param pathOutput
	 *            the path output
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public static File mergePdfFiles(List<File> listPdfFile, File pathOutput) throws IOException, DocumentException {
		OutputStream out = new FileOutputStream(pathOutput);
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, out);
		document.open();
		PdfContentByte cb = writer.getDirectContent();

		for (File pdfFile : listPdfFile) {
			InputStream in = new FileInputStream(pdfFile);
			PdfReader reader = new PdfReader(in);
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				document.newPage();
				PdfImportedPage page = writer.getImportedPage(reader, i);
				cb.addTemplate(page, 0, 0);
			}
			in.close();
		}

		out.flush();
		document.close();
		out.close();
		return pathOutput;
	}


	/**
	 * Instantiates a new PDF generator.
	 */
	private DocumentGenerator() {

	}

}
