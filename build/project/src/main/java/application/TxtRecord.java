package application;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public class TxtRecord {
	public final static int numberOfItemsInRecord=3;
	public final static int SONGNAME=1;
	public final static int SONGPATH=2;
	public final static int SONGSIZE=3;
	public final static String SEPARATOR=":ccc:";
	
	private String record;	//rekord stanowi pola oddzielone separatorem, zapisane w stringu
	
	public TxtRecord(String record) {
		this.record=record;
	}
	
	//tworzenie rekordu poprzez podanie samych pól
	public TxtRecord(String... items) {
		record="";
		if(items.length==0) {
			createRecord("","","");
		}else if(items.length==numberOfItemsInRecord) {
			createRecord(items);
		}
	}
	
	@Override
	public String toString() {
		return record;
	}
	
	//ustawienie w rekordzie danego pola
	public void setInRecord(final int itemName, String newValue) {
		if(itemName>0 && itemName<=numberOfItemsInRecord) {
			String[] tmp=record.split(SEPARATOR);	//rozdziel rekord ze wzglêdu na separator
			switch(itemName) {	//przypisz now¹ wartoœæ wybranemu polu
				case SONGNAME: tmp[0]=newValue; break;
			  	case SONGPATH: tmp[1]=newValue; break;
			  	case SONGSIZE: tmp[2]=newValue; break;
			}
			record="";	//wyzeruj rekord
			createRecord(tmp);	//stwórz nowy rekord na podstawie tablicy, ze zmienionym polem
		}
	}
	
	//pobranie danego pola z rekordu
	public String getFromRecord(final int itemName) {
		String[] tmp=record.split(SEPARATOR);
		switch(itemName) {	//wybierz wskazane pole
			case SONGNAME: return tmp[0];
		  	case SONGPATH: return tmp[1];
		  	case SONGSIZE: return tmp[2];
		  	default: return "";
		}
	}
	
	//tworzenie rekordu z tablicy pól ->sk³adanie poprzez konkatenacjê pól i separatora
	private void createRecord(String... tab) {
		Preconditions.checkNotNull(tab);
		record=Arrays.stream(tab)
				.collect(Collectors.joining(SEPARATOR));
	}
}
