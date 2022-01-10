package project.stats;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import project.model.DatiUSA;
import project.model.DatiHospital;

/**
 * in tale classe definiremo qualche statistica da allegare alle varie rotte del connection
 * 
 * @author Enrico Maria Sardellini
 */
public class Statistics implements Statistics_interface{

	/**
	 * Valori semplificati a costanti, anche se costanti non sono, per la determinazione delle varie statistiche:
	 * uno è la popolazione degli USA(popolation_USA) e le altre due sono i letti di terapia intensiva totali(ICU_total)
	 * e i letti degli ospedali totali(beds_total)
	 * 
	 * @link https://globalepidemics.org/hospital-capacity-2/
	 */
	static final int population_USA = 330000000; 
	static final int ICU_total= 84750; 
	static final int beds_total = 737567;
	
	/**
	 * costruttore delle classe
	 */
	public Statistics() {};
		
	/**
	 * questo metodo determina le caratteristiche della settimana e del mese;
	 * tutte le percentuali sono arrotondate al secondo decimale
	 * 
	 * @author Enrico Maria Sardellini
	 * @param vett1
	 * @param vett2
	 * @param array
	 * @param i
	 * @param dayfinal
	 * @see project.model.DatiUSA
	 * @see project.model.DatiHospital
	 * 
	 */
	public void StatsLong(ArrayList<DatiUSA> vett1, ArrayList<DatiHospital> vett2, JSONArray array, Integer i, Integer dayfinal) {
		
		/**
		 * ho creato 5 oggetti JSONObject per poter distinguere le varie statistiche e separarle in macroaree:
		 * 
		 * -obj1: settimana/mese, numero di morti
		 * -obj2: dati relativi ai positivi
		 * -obj3: dati relativi ai negativi
		 * -obj4: dati relativi alle ospedallizazioni
		 * -obj5: dati relativi alle terapie intensive
		 */
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		JSONObject obj4 = new JSONObject();
		JSONObject obj5 = new JSONObject();
			
		/**
		 * a seconda che dayfinal sia uguale o no a 7 sta a significare
		 * che stiamo lavorando con i dati di un mese o di una settimana
		 * 
		 */
		if(dayfinal == 7) obj1.put("Week", vett1.get(i).getDay() + "-" + vett1.get(i-6).getDay());
		else obj1.put("Month", vett1.get(i).getDay() + "-" + vett1.get(i-dayfinal+1).getDay());
			
		/**
		 *il for ci serve per avere il numero dei nuovi positivi/negativi/morti 
		 *nella durata di tempo prevista da dayfinal 
		 */
		long positive=0, negative=0, death=0;
		for (int j=0; j<dayfinal; j++) {
			positive += vett1.get(i-j).getPositiveIncrease();
		    negative += vett1.get(i-j).getNegativeIncrease();
		    death += vett1.get(i-j).getDeathIncrease();
		}	
			
		if (dayfinal ==7) obj1.put("Death in the week", "+" + death);
		else obj1.put("Death in the months", "+" + death);
		array.add(obj1);
		
		/**
		 * percentuale dei positivi nel giorno iniziale e finale,
		 * il numero dei nuovi positivi e la media dei nuovi positivi
		 * 
		 */
		double percPos1 = ((double)vett1.get(i).getPositive()/(double)population_USA)*100; 
		percPos1 = Math.round(percPos1*100.0)/100.0;
		double percPos2 = ((double)vett1.get(i-dayfinal).getPositive()/(double)population_USA)*100;
		percPos2 = Math.round(percPos2*100.0)/100.0;
		double mediaPos = (double)positive/(double)dayfinal;
		mediaPos= Math.round(mediaPos*100.0)/100.0;
		if(dayfinal ==7) {
			obj2.put("Positive percentage in the week", "from " + percPos1 + "% to "+ percPos2 + "%");
			obj2.put("Positive increase in the week", "+" + positive);
			obj2.put("Average positive increase in the week", mediaPos + " a day");
		}
		else {
			obj2.put("Positive percentage in the month", "from " + percPos1 + "% to "+ percPos2 + "%");
			obj2.put("Positive increase in the month", "+" + positive);
			obj2.put("Average positive increase in the month", mediaPos + " a day");
		}
		array.add(obj2);
		
		/**
		 * percentuale dei negativi nel giorno iniziale e finale,
		 * il numero dei nuovi negativi e la media dei nuovi negativi
		 */
		double percNeg1 = ((double)vett1.get(i).getNegative()/(double)population_USA)*100; 
		percNeg1 = Math.round(percNeg1*100.0)/100.0;
		double percNeg2 = ((double)vett1.get(i-dayfinal).getNegative()/(double)population_USA)*100;
		percNeg2 = Math.round(percNeg2*100.0)/100.0;
		double mediaNeg = (double)negative/(double)dayfinal;
		mediaNeg = Math.round(mediaNeg*100.0)/100.0;
		if(dayfinal ==7) {
			obj3.put("Negative percentage in the week", "from " + percNeg1 + "% to " + percNeg2 + "%");
			obj3.put("Negative increase in the week", "+" + negative);
			obj3.put("Average negative increase in the week", mediaNeg + " a day");
		}
		else {
			obj3.put("Negative percentage in the month", "from " + percNeg1 + "% to " + percNeg2 + "%");
			obj3.put("Negative increase in the month", "+" + negative);
			obj3.put("Average negative increase in the month", mediaNeg + " a day");
		}
		array.add(obj3);
			
		/**
		 * percentuale delle ospedalizzazioni nel giorno
		 * iniziale e finale e andamento delle nuove ospedalizzazioni
		 */
		double percHos1 = ((double)vett2.get(i).getHospitalized()/(double)beds_total)*100; 
		percHos1 = Math.round(percHos1*100.0)/100.0;
		double percHos2 = ((double)vett2.get(i-dayfinal).getHospitalized()/(double)beds_total)*100;
		percHos2 = Math.round(percHos2*100.0)/100.0;
		if(dayfinal ==7) {
				obj4.put("Hospitalized percentage in the week", "from " + percHos1 + "% to " + percHos2 + "%");
				if((vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()) <=0 ) obj4.put("Hospitalized in the week", (vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()));
				else obj4.put("Hospitalized in the week", "+" + (vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()));
		}
		else {
				obj4.put("Hospitalized percentage in the month","from " + percHos1 + "% to " + percHos2 + "%");
				if((vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()) <= 0 ) obj4.put("Hospitalized in the month", (vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()));
				else obj4.put("Hospitalized in the month", "+" + (vett2.get(i-dayfinal).getHospitalized()-vett2.get(i).getHospitalized()));
		}
		array.add(obj4);
		
		/**
		 * percentuale delle terapie intensive nel giorno 
		 * iniziale e finale e andamento delle nuove terapie intensive
		 */
		double percIcu1 = ((double)vett2.get(i).getIntensive_care()/(double)ICU_total)*100; 
		percIcu1 = Math.round(percIcu1*100.0)/100.0;
		double percIcu2 = ((double)vett2.get(i-dayfinal).getIntensive_care()/(double)ICU_total)*100;
		percIcu2 = Math.round(percIcu2*100.0)/100.0;
		if(dayfinal ==7) {
				obj5.put("Percentage of intensive care in the week", "from " + percIcu1 + "% to " + percIcu2 + "%");
				if(vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care() <= 0) obj5.put("Intensive care in the week", (vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care()));
				else obj5.put("Intensive care in the week", "+" + (vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care()));
		}
		else {
			obj5.put("Percentage of intensive care in the month", "from " + percIcu1 + "% to " + percIcu2 + "%");
			if(vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care() <= 0) obj5.put("Intensive care in the month", (vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care()));
			else obj5.put("Intensive care in the month", "+" + (vett2.get(i-dayfinal).getIntensive_care()-vett2.get(i).getIntensive_care()));
		}
		array.add(obj5);
	}

	/**
	 * metodo che mostra i valori percentuali dei due giorni 
	 * mettendoli a confronto
	 * 
	 * tutte le percentuali sono arrotondate al secondo decimale
	 * 
	 * @author Enrico Maria Sardellini
	 * @param vett1
	 * @param vett2
	 * @param array
	 * @param day1
	 * @param day2
	 * @see project.model.DatiUSA
	 * @see project.model.DatiHospital
	 */
	public void Stats2day(ArrayList<DatiUSA> vett1, ArrayList<DatiHospital> vett2, JSONArray array, String day1, String day2) {
		JSONObject obj = new JSONObject();
		
		int i=0, j=0;
		for(i=0; i<vett1.size(); i++) {
			if(day1.equals(vett1.get(i).getDay())) break; 
		}
		for(j=0; j<vett1.size(); j++) {
			if(day2.equals(vett1.get(j).getDay())) break; 
		}
		if(i>j) obj.put("Days", day1 + " and " + day2);
		else obj.put("Days", day2 + " and " + day1);
		
		/**
		 * variazione percentuale dei positivi
		 */
		double percPos1 = ((double)vett1.get(i).getPositive()/(double)population_USA)*100;
		double percPos2 = ((double)vett1.get(j).getPositive()/(double)population_USA)*100;
		percPos1 = Math.round(percPos1*100.0)/100.0;
		percPos2 = Math.round(percPos2*100.0)/100.0;
		if (i>j) obj.put("Positive percentage", "from " + percPos1 + "% to " + percPos2 + "%");
		else obj.put("Positive percentage", "from " + percPos2 + "% to " + percPos1 + "%");
		
		/**
		 * variazione percentuale dei negativi
		 */
		double percNeg1 = ((double)vett1.get(i).getNegative()/(double)population_USA)*100;
		double percNeg2 = ((double)vett1.get(j).getNegative()/(double)population_USA)*100;
		percNeg1 = Math.round(percNeg1*100.0)/100.0;
		percNeg2 = Math.round(percNeg2*100.0)/100.0;
		if (i>j) obj.put("Negative percentage", "from " + percNeg1 + "% to " + percNeg2 + "%");
		else obj.put("Negative percentage", "from " + percNeg2 + "% to " + percNeg1 + "%");
		
		/**
		 * variazione percentuale delle ospedalizzazioni
		 */
		double percHos1 = ((double)vett2.get(i).getHospitalized()/(double)beds_total)*100;
		double percHos2 = ((double)vett2.get(j).getHospitalized()/(double)beds_total)*100;
		percHos1 = Math.round(percHos1*100.0)/100.0;
		percHos2 = Math.round(percHos2*100.0)/100.0;
		if (i>j) obj.put("Hospitalized percentage", "from " + percHos1 + "% to " + percHos2 + "%");
		else obj.put("Hospitalized percentage", "from " + percHos2 + "% to " + percHos1 + "%");
		
		/**
		 * variazione percentuale delle terapie intensive
		 */
		double percIcu1 = ((double)vett2.get(i).getIntensive_care()/(double)ICU_total)*100;
		double percIcu2 = ((double)vett2.get(j).getIntensive_care()/(double)ICU_total)*100;
		percIcu1 = Math.round(percIcu1*100.0)/100.0;
		percIcu2 = Math.round(percIcu2*100.0)/100.0;
		if (i>j) obj.put("Intensive care percentage", "from " + percIcu1 + "% to " + percIcu2 + "%");
		else obj.put("Intensive care percentage", "from " + percIcu2 + "% to " + percIcu1 + "%");
		array.add(obj);
	};

	/**
	 * metodo che determina quanti sono i giorno di quel colore 
	 * 
	 * @author Enrico Maria Sardellini
	 * @see project.model.DatiHospital.getColour()
	 * @param vett2
	 * @param colour
	 */
	public int StatsColour(ArrayList<DatiHospital> vett2, String colour) {
			
		int contatore=0; 
			
		for(int i=0; i<vett2.size(); i++) {
			if(colour.equals(vett2.get(i).getColour())) contatore++;
		}
		
		return contatore;
	}
}
