package com.pandero.ws.business;

public interface GarantiaBusiness {

	public String crearGarantiaSAF(String pedidoCaspioId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String uso, String usuarioId) throws Exception;
	public String editarGarantiaSAF(String garantiaId,String partidaRegistral,String fichaConstitucion,
			String fechaConstitucion, String montoPrima, String modalidad, String uso, String usuarioId) throws Exception;
	public String eliminarGarantia(String garantiaId, String usuarioId) throws Exception;
	
}
