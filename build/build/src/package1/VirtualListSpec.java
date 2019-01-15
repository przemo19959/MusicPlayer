package package1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.files.FileService;
import application.files.FileServiceImpl;

class VirtualListSpec {
	private static FileService mockfinder;
	private VirtualListImpl mockList;
	
	@BeforeAll
	static void init() {
		mockfinder=new FileServiceImpl();
	}
	
	@Test
	@DisplayName("exception is thrown, when null list name is passed")
	void test1() {
		assertThrows(NullPointerException.class, ()->VirtualListImpl.create(null));
	}
	
	@Test
	@DisplayName("virtual list is created properly")
	void test2() throws IOException {
		mockList=VirtualListImpl.create("cos");
		assertThat(Files.exists(Paths.get(FileService.pathToMainFolder+"/cos.txt"))).isTrue();
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(0);	//automatycznie sprawdzenie czy records null
		mockList.deleteVirtualList();
	}
	
	@Test
	@DisplayName("setting list works fine")
	void test3() throws IOException {
		mockList=VirtualListImpl.create("asadsdasdasd");
		assertThat(mockList.getVirtualListName()).isEqualTo("asadsdasdasd");
		mockList.withName("asdljaskdjhahsjd");
		assertThat(mockList.getVirtualListName()).isEqualTo("asdljaskdjhahsjd");
		mockList.deleteVirtualList();
	}
	
	@Test
	@DisplayName("creating by NotReal create default values")
	void test4() throws IOException {
		mockList=VirtualListImpl.createNotReal();
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(0);
		assertThat(mockList.getVirtualListName()).isEqualTo("");
	}
	
	@Test
	@DisplayName("creating by NotReal does not create real file")
	void test5() throws IOException {
		mockList=VirtualListImpl.createNotReal().withName("aksjdalidjh");
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(0);
		assertThat(mockList.getVirtualListName()).isEqualTo("aksjdalidjh");
		Path path=Paths.get(FileService.pathToMainFolder+"/aksjdalidjh.txt");
		assertThat(Files.exists(path)).isFalse();
	}
	
	@Test
	@DisplayName("getting records from existing file works correcty")
	void test6() throws IOException {
		mockList=VirtualListImpl.create("ahdajsd");
		VirtualRecord record1=new VirtualRecord("ahdsj","asndjashd","sdjashdjahsd");
		VirtualRecord record2=new VirtualRecord("czxncznx","wduahsdasj","zcxncb");
		VirtualRecord record3=new VirtualRecord("auwe7hds","adjsdkj","wjd aksjd");
		mockList.addVirtualRecord(record1,record2,record3);
		
		VirtualList tmp=VirtualListImpl.createNotReal().withName("ashdahsgd").fileToVirtualList("ahdajsd");
		assertThat(tmp.getVirtualRecord(0).equals(record1)).isTrue();
		assertThat(tmp.getVirtualRecord(1).equals(record2)).isTrue();
		assertThat(tmp.getVirtualRecord(2).equals(record3)).isTrue();
		mockList.deleteVirtualList();
	}
	
	@Test
	@DisplayName("clearing, clears file and virtual list")
	void test7() throws IOException {
		mockList=VirtualListImpl.create("ahdajsd");
		VirtualRecord record1=new VirtualRecord("ahdsj","asndjashd","sdjashdjahsd");
		VirtualRecord record2=new VirtualRecord("czxncznx","wduahsdasj","zcxncb");
		VirtualRecord record3=new VirtualRecord("auwe7hds","adjsdkj","wjd aksjd");
		mockList.addVirtualRecord(record1,record2,record3);
		assertThat(mockfinder.readTxtFile("ahdajsd")).isNotEmpty();
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(3);
		
		mockList.clearVirtualList();
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(0);
		assertThat(mockfinder.readTxtFile("ahdajsd")).isEmpty();
		mockList.deleteVirtualList();
	}
	
	@Test
	@DisplayName("deleting records, works fine")
	void test8() throws IOException {
		mockList=VirtualListImpl.create("2761372esd");
		VirtualRecord record1=new VirtualRecord("ahdsj","asndjashd","sdjashdjahsd");
		VirtualRecord record2=new VirtualRecord("czxncznx","wduahsdasj","zcxncb");
		VirtualRecord record3=new VirtualRecord("auwe7hds","adjsdkj","wjd aksjd");
		VirtualRecord record4=new VirtualRecord("asjhdas","cxzc","sd aksjd");
		VirtualRecord record5=new VirtualRecord("asdasd","czc","wjd sdasd");
		mockList.addVirtualRecord(record1,record2,record3,record4,record5);
		
		mockList.deleteVirtualRecordFromVirtualList(2);
		mockList.deleteVirtualRecordFromVirtualList(2);
		assertThat(mockList.getNumberOfVirtualRecords()).isEqualTo(3);
		assertThat(mockList.getVirtualRecord(0).equals(record1)).isTrue();
		assertThat(mockList.getVirtualRecord(1).equals(record2)).isTrue();
		assertThat(mockList.getVirtualRecord(2).equals(record5)).isTrue();
		
		mockList.deleteVirtualList();
	}
}
