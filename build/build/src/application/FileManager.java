package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import application.TxtFile;
import application.TxtRecord;

public class FileManager {
	
	private Stage stage;
	private DirectoryChooser directoryChooser; 
	private File songFolder; 
	private File folder;
	private TxtFile newFile;
	
	public final static String pathToMainFolder="G:/java-workspace/MPproject/songFiles"; //œcie¿ka do folderu glownego
	
	private List<TxtFile> textFiles=new ArrayList<>();	//lista plików tekstowych
	private List<TxtRecord> tmp=new ArrayList<>();
	
	private StringConverter<Double> stringConverter = new StringConverter<Double>() {
		@Override
		public String toString(Double object) {
			long seconds=0,minutes=0,remainingseconds=0;
			seconds=object.longValue();
			minutes = TimeUnit.SECONDS.toMinutes(seconds);
            remainingseconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);
            return String.format("%2d", minutes) + ":" + String.format("%02d", remainingseconds);
		}
		
		@Override
		public Double fromString(String string) {
			return null;
		}
	};

	public StringConverter<Double> getStringConverter() {
		return stringConverter;
	}

	//konstruktor
	public FileManager(Stage stage){
		this.stage=stage;
		songFolder=new File(pathToMainFolder); //folder z plikami tekstowymi z utworami audio
		textFiles.clear();	//wyczyœæ listê plików tekstowych
	}
	
	//tworzenie pliku .txt w folderze glownym
	public void createTxtFileInFolder() throws IOException{
		textFiles.add(newFile); //dodaj do list plików		
		Files.createFile(newFile.toPath());
	}
		
	public void deleteTxtFileInFolder(File file) throws IOException{
		textFiles.remove(new TxtFile(file.toURI().toString()));
		Files.delete(file.toPath());
	}
		
	//funkcja skanuj¹ca folder w poszukiwaniu plikow .txt, jeœli takie istniej¹ uzupe³nia tablicê ich nazwami
	public void scanFolderForTextFiles() throws IOException {
		try(DirectoryStream<Path> stream=Files.newDirectoryStream(songFolder.toPath(), "*.txt")){
			for(Path path:stream) {
				if(!textFiles.contains(new TxtFile(path.toString())))
					textFiles.add(new TxtFile(path.toString())); //dodaj pliki tekstowe
			}
		}
	}
	
	//odczyt danego typu informacji z danego rekordu z danego pliku
	public String readRecord(File file, int index, int type) throws IOException {
		String result="";
	    try(BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"))){
	      String currentLine = null;
	      for(int i=0;(currentLine = reader.readLine()) != null;i++) {
	    	  if(i==index) {
	    		  TxtRecord tmp=new TxtRecord(currentLine);
	    		  result=tmp.getFromRecord(type);
	    		  break;
	    	  }
	      }
	    }
	    return result;
	}
	
	//getter do listy plikow .txt
	public List<TxtFile> getTextFiles() {
		return textFiles;
	}

	//dodawanie nowego/aktualizacja starego pliku tekstowego z piosenkami 
	public void findNewSongs() throws IOException,UnsupportedAudioFileException {
		directoryChooser=new DirectoryChooser();
		directoryChooser.setTitle("Wybierz folder z plikami audio");
		folder=directoryChooser.showDialog(stage); //pobierz nowy folder do przeszukania
		if(folder!=null) { //jeœli wybrano folder
			newFile=new TxtFile(songFolder+"/"+folder.getName()+".txt");
			if(textFiles.contains(newFile)) { //jeœli wybrano ju¿ istniej¹cy folder(w celu aktualizaji)
				textFiles.get(textFiles.indexOf(newFile)).clearTxtFile();
			}else { //jeœli wybrano nowy folder
				createTxtFileInFolder(); //utwórz nowy plik tekstowy
			}
			findSongs();
			textFiles.get(textFiles.indexOf(newFile)).writeRecordToTxtFile(tmp.toArray(new TxtRecord[tmp.size()]));
		}
	}
		
	//funkcja wyszukuj¹ca pliki audio(rozszerzenie .mp3 lub .wav w wybranym folderze
	private void findSongs() throws IOException {
		tmp.clear();
		try(DirectoryStream<Path> stream=Files.newDirectoryStream(folder.toPath(), "*.{mp3,wav}")){
			for(Path path:stream) {
				final BasicFileAttributes attr=Files.readAttributes(path, BasicFileAttributes.class);  //w celu pobrania informacji o pliku audio
				tmp.add(new TxtRecord(path.getFileName().getFileName().toString(),path.toAbsolutePath().toString(),
									 	String.format("%.2f",(attr.size()/Math.pow(1024, 2)))+"[MB]\n"));
			}
		}
	}
}
