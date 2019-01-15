package package1;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * This class represents virtual record. At this moment it contains 3 fields, each about one audio file. It is song name, song path and song size.
 * Each virtual record coresponds to one audio file, which was found in chosen directory. 
 * @author hex
 *
 */
public class VirtualRecord {
	public final static int numberOfItemsInRecord=3;
	public final static int SONGNAME=1;
	public final static int SONGPATH=2;
	public final static int SONGSIZE=3;
	public final static String SEPARATOR=":ccc:";
	
	private String record;	//rekord stanowi pola oddzielone separatorem, zapisane w stringu
	
	/**
	 * This method creates virtual record. 
	 * @param items from which record is created
	 * @throws IllegalArgumentException when number of passed items, is not equal to constant <strong>numberOfItemsInRecord</strong>
	 */
	public VirtualRecord(String... items) {
		Preconditions.checkArgument(items.length==numberOfItemsInRecord,"number of passed"
				+ "items, must be equal to constant specified by that class");
		record="";
		createRecord(items);
	}
	
	@Override
	public String toString() {
		return record;
	}
	
	/**
	 * This method sets given item in virtual records. This gives chance to modify already created records.
	 * Number of itemName must be in bounds (0,<strong>numberOfItemsInRecord</strong>>, otherwise record won't be modified.
	 * @param itemName item to be set
	 * @param newValue new value to be written
	 */
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
	
	/**
	 * This method lets to read any item from record. Number of itemName must be in bounds (0,<strong>numberOfItemsInRecord</strong>>,
	 * otherwise empty string is returned.
	 * @param itemName item to be read
	 * @return value of specified item
	 */
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
	
	@Override
	public boolean equals(Object obj) {
		Preconditions.checkNotNull(obj,"object passed for equal method must not be null");
		if(!(obj instanceof VirtualRecord))
			return false;
		VirtualRecord that=(VirtualRecord)obj;
		for(int i=1;i<=numberOfItemsInRecord;i++) {
			if(!that.getFromRecord(i).equals(this.getFromRecord(i)))
				return false;
		}
		return true;
	}
}
