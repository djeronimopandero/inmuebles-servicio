import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pandero.ws.bean.ObservacionInversion;

public class Test {

	public static void main(String[] args) {
		
		//Lima, 16 de agosto del 2016
		
//		String fecha = getFechaFormateada(new Date(),"MMMM dd, yyyy");
//		System.out.println(""+fecha);
		
//		String fecha2 = getFechaFormateada(new Date(),"dd 'de' MMMM 'del' yyyy");
//		System.out.println(""+fecha2);
		
//		179000
		
//		double money = 179000;
//		NumberFormat formatter = NumberFormat.getCurrencyInstance();
//		String moneyString = formatter.format(money);
//		System.out.println(moneyString);
		
//		double amt = 19500.25;    
//		NumberFormat formatter2 = NumberFormat.getCurrencyInstance();
//		System.out.println(formatter2.format(amt));
		
//		DecimalFormat dFormat = new DecimalFormat("####,###,###.00");
//		DecimalFormat dFormat = new DecimalFormat("####,####");
//		DecimalFormat dFormat = new DecimalFormat("###,###.###");
//		DecimalFormat dFormat = new DecimalFormat("###,###.###");
//		System.out.println(dFormat.format(amt));
		
		
//		Locale locale  = new Locale("en", "UK");
//		String pattern = "####,###,###.00";
//		DecimalFormat decimalFormat = (DecimalFormat)
//		NumberFormat.getNumberInstance(locale);
//		decimalFormat.applyPattern(pattern);
//
//		String format = decimalFormat.format(1956500.50);
//		System.out.println(format);
		
		List<ObservacionInversion> listObs=new ArrayList<>();
		System.out.println(listObs.size());
		
	}
	
	public static String getFechaFormateada(Date date, String format){
		StringBuilder sb=new StringBuilder();
		sb.append("Lima, ");
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String fecha=sdf.format(date);
		sb.append(fecha);
		return sb.toString();
	}
	
}
