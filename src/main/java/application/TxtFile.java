package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TxtFile extends File{
	private String tempString="";
	
	//konstruktor do tworzenia pliku, na podstawie œcie¿ki do pliku
	public TxtFile(String stringPath) {
		super(stringPath);
	}
	
	//konstruktor pliku, na podstawie nazwy rodzica i pliku
	public TxtFile(String parent, String child) {
		super(parent, child);
	}
	
	//zapis do pliku tekstowego
	public void writeRecordToTxtFile(TxtRecord[] tmp) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(this.toPath(), Charset.forName("UTF-8"))){
			for(int i=0;i<tmp.length;i++) {
				writer.write(tmp[i].toString()); //pobierz i-ty rekord z tabeli i zapisz do pliku tekstowego(this)
			}
		}
	}
	
	//wyczyœæ plik tekstowy
	public void clearTxtFile() throws IOException{
		try(BufferedWriter writer = Files.newBufferedWriter(this.toPath(), Charset.forName("UTF-8"))){
			writer.write(""); //czyszczenie poprzez zapis pustego stringa
		}
	}
	
	// pobierz iloœæ wierszy(rekordów) pliku tekstowego
	public int getTextFileNumberOfRecords() throws IOException {
		int result = 0;
		try (BufferedReader reader = Files.newBufferedReader(this.toPath(),
				Charset.forName("UTF-8"))) {
//			String currentLine = null;
			result=(int)reader.lines().count();
//			while ((currentLine = reader.readLine())!= null) {
//				result++;
//			}
		}
		return result;
	}

	//usuwanie rekordu z pliku tekstowego
	public void deleteRecordFromTxtFile(int indexOfRecordToErase)
			throws IOException {
		copyContentWithoutRecord(indexOfRecordToErase);
		writeTxtFileFromString();
	}

	// funkcja zapisuj¹ca rekordy zawarte w stringu(oddzielone znakiem \n) do
	// pliku tekstowego
	private void writeTxtFileFromString() throws IOException {
		String[] tmp = tempString.split("\n");
		try (BufferedWriter writer = Files.newBufferedWriter(this.toPath(),
				Charset.forName("UTF-8"))) {
			for(int i = 0;i< tmp.length;i++) {
				writer.write(tmp[i]+ "\n"); // zapisz rekordy
			}
		}
		tempString = "";
	}

	// funkcja przepisuj¹ca zawartoœæ pliku tekstowego do stringa tymczasowego z
	// wy³¹czeniem konkretnego rekordu
	private void copyContentWithoutRecord(int indexOfRecordToErase)
			throws IOException {
//		int i = 0;
		tempString = "";
		try (BufferedReader reader = Files.newBufferedReader(this.toPath(),
				Charset.forName("UTF-8"))) {
			
			reader.lines()
				.filter(line->!line.startsWith(indexOfRecordToErase+"."))	//filtracja rekordów
				.forEach(line->tempString+=line+"\n");	//sumowanie rekordów
			
//			String currentLine = null;
//			while ((currentLine = reader.readLine())!= null) {
//				if(i!= indexOfRecordToErase)
//					tempString += currentLine+ "\n"; // dopisuj linijki z pliku
//														// tekstowego
//				i++;
//			}
			tempString = tempString.substring(0, tempString.lastIndexOf("\n")); //wytnij ostatni nowy wiersz
		}
	}
}
