import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		
//		List<ObservacionInversion> listObs=new ArrayList<>();
//		System.out.println(listObs.size());
		
//		test2();
//		
	}
	
	public static String getFechaFormateada(Date date, String format){
		StringBuilder sb=new StringBuilder();
		sb.append("Lima, ");
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String fecha=sdf.format(date);
		sb.append(fecha);
		return sb.toString();
	}
	
	public static void test2(){
		StringBuilder s1 = new StringBuilder("Java");
		 String s2 = "Love";
		 s1.append(s2);
		 s1.substring(4);
		 int foundAt = s1.indexOf(s2);
		System.out.println(foundAt);

		
	}
	
}
