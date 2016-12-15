package com.pandero.ws.bean;

public class EmailBean {
	
	private String emailFrom; 
	private String emailTo;
	private String subject;
	private String documento; 
	private String textoEmail;
	private boolean isFormatHtml;
	private boolean enviarArchivo;
	
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getTextoEmail() {
		return textoEmail;
	}
	public void setTextoEmail(String textoEmail) {
		this.textoEmail = textoEmail;
	}
	public boolean isFormatHtml() {
		return isFormatHtml;
	}
	public void setFormatHtml(boolean isFormatHtml) {
		this.isFormatHtml = isFormatHtml;
	}
	public boolean isEnviarArchivo() {
		return enviarArchivo;
	}
	public void setEnviarArchivo(boolean enviarArchivo) {
		this.enviarArchivo = enviarArchivo;
	}
	
}
