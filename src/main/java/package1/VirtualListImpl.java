package package1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import application.files.FileService;
import application.files.FileServiceImpl;

public class VirtualListImpl implements VirtualList{
	private FileService fileService;
	
	private List<VirtualRecord> records;
	private String listName;
	
	public static VirtualListImpl create(String listName) throws IOException {
		return new VirtualListImpl(Preconditions.checkNotNull(listName));
	}
	
	public static VirtualListImpl createNotReal() throws IOException {
		return new VirtualListImpl();
	}
	
	private VirtualListImpl() {
		listName="";
		fileService=new FileServiceImpl();
		records=new ArrayList<>();
	}
	
	private VirtualListImpl(String listName) throws IOException {
		Preconditions.checkNotNull(listName);
		this.listName=listName;
		fileService=new FileServiceImpl();
		records=new ArrayList<>();
		fileService.createTxtFile(listName);
	}
		
	public VirtualListImpl withName(String listName) throws IOException {
		Preconditions.checkNotNull(listName);
		Path path=Paths.get(FileService.pathToMainFolder+"/"+this.listName+".txt");
		if(Files.exists(path))
			fileService.renameTxtFile(this.listName, listName);
		this.listName=listName;
		return this;
	}
	
	@Override
	public void deleteVirtualList() throws IOException {
		fileService.deleteTxtFile(listName);
	}
	
	@Override
	public VirtualListImpl fileToVirtualList(String fileName) throws IOException {
		Preconditions.checkNotNull(fileName);
		List<String> content=fileService.readTxtFile(fileName);
		records=content.stream()
				.map(line->new VirtualRecord(line.split(VirtualRecord.SEPARATOR)))
				.collect(Collectors.toList());
		return this;
	}
	
	@Override
	public VirtualRecord getVirtualRecord(int index) {
		Preconditions.checkElementIndex(index, records.size());
		return records.get(index);
	}
	
	@Override
	public String getVirtualListName() {
		return listName;
	}
	
	@Override
	public void addVirtualRecord(VirtualRecord... records) throws IOException {
		Preconditions.checkNotNull(records);
		for(int i=0;i<records.length;i++) {
			this.records.add(records[i]);
			fileService.writeTxtFile(listName, records[i].toString()+"\n");
		}
	}
	
	@Override
	public void clearVirtualList() throws IOException{
		records.clear();
		fileService.clearTxtFile(listName);
	}
	
	@Override
	public int getNumberOfVirtualRecords(){
		return records.size();
	}

	@Override
	public void deleteVirtualRecordFromVirtualList(int index) throws IOException{
		Preconditions.checkElementIndex(index, records.size());
		records.remove(index);
		fileService.deleteRecordFromTxtFile(listName, index);
	}
}
