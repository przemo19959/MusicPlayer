package package2;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import application.files.FileService;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import package1.VirtualList;
import package1.VirtualListImpl;
import package1.VirtualRecord;

public class AdderImpl implements Adder {
	private Stage stage;
	private DirectoryChooser directoryChooser;
	private File folder;
	private VirtualList list;
	private List<VirtualList> virtualLists;
	
	public AdderImpl(Stage stage){
		this.stage=stage;
		virtualLists=new ArrayList<>();
	}
	
	@Override
	public void deleteVirtualList(int index) throws IOException {
		Preconditions.checkElementIndex(index, virtualLists.size());
		virtualLists.get(index).deleteVirtualList();
		virtualLists.remove(index);
	}
	
	@Override
	public int getNumberOfVirtualLists() {
		return virtualLists.size();
	}
	
	@Override
	public VirtualList getVirtualList(int index) {
		Preconditions.checkElementIndex(index, virtualLists.size());
		return virtualLists.get(index);
	}
	
	@Override
	public String getVirtualListName(int index) {
		Preconditions.checkElementIndex(index, virtualLists.size());
		return virtualLists.get(index).getVirtualListName();
	}
	
	@Override
	public void scanFolderForTxtFiles() throws IOException {
		try(DirectoryStream<Path> stream=Files.newDirectoryStream(Paths.get(FileService.pathToMainFolder), "*.txt")){
			for(Path path:stream) {
				String fileName=path.getFileName().toString().replace(".txt", "");
				virtualLists.add(VirtualListImpl.createNotReal()
				                 .withName(fileName)
				                 .fileToVirtualList(fileName));
			}
		}
	}
	
	@Override
	public void chooseFolder() throws IOException {
		directoryChooser=new DirectoryChooser();
		directoryChooser.setTitle("Wybierz folder z plikami audio");
		folder=directoryChooser.showDialog(stage); //pobierz nowy folder do przeszukania
		if(folder!=null)//jeœli wybrano folder
			findSongsInChosenFolder();
	}
		
	private void findSongsInChosenFolder() throws IOException {
		try(DirectoryStream<Path> stream=Files.newDirectoryStream(folder.toPath(), "*.{mp3,wav}")){
			virtualLists.stream()
				.filter(list->list.getVirtualListName().equals(folder.getName()))
				.findFirst()
				.ifPresent(res->list=res);
			if(list==null) {
				list=VirtualListImpl.create(folder.getName());	//jeœli znajdzie pliki z takim rozszerzeniem w wybranym folderze
				virtualLists.add(list);
			}else
				list.clearVirtualList();
			for(Path path:stream) {
				final BasicFileAttributes attr=Files.readAttributes(path, BasicFileAttributes.class);  //w celu pobrania informacji o pliku audio
				list.addVirtualRecord(new VirtualRecord(path.getFileName().toString(),path.toAbsolutePath().toString(),
									 	String.format("%.2f",(attr.size()/Math.pow(1024, 2)))+"[MB]"));	//dodaj itemy do rekordu wirtualnego
			}
			list=null;	//inaczej, zapamiêtywana jest nazwa listy z poprzedniego dodania, i wtedy ca³y
			//czas aktualizuje jedn¹ listê, nie dodaj¹ nowej.
		}
	}
}
