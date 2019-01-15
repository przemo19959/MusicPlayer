package Database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.files.FileService;
import application.files.FileServiceImpl;

class FinderImplSpec {
	private static FileService finder;
	
	
	@BeforeAll
	@DisplayName("preparing testing enviroment,"
			+ " by creating fake file")
	static void init() throws IOException {
		finder=new FileServiceImpl();
		finder.createTxtFile("fake");
	}
	
	@BeforeEach
	@DisplayName("clearing")
	void setup() throws IOException {
		finder.clearTxtFile("fake");
	}
	
	@Test
	@DisplayName("file exists")
	void test1() {
		Path path=Paths.get(FileService.pathToMainFolder+"/fake.txt");
		assertThat(Files.exists(path)).isTrue();
	}
	
	@Test
	@DisplayName("after creating file is empty")
	void test2() throws IOException {
		assertThat(finder.readTxtFile("fake")).isEmpty();
	}
	
	@Test
	@DisplayName("after writing 2 lines, resulting list size is equal to 2")
	void test3() throws IOException {
		String input="dashdahsdjhasjdaasjdajsd\nhajshdajshdajshdjashdjashd";
		finder.writeTxtFile("fake", input);
		List<String> result=finder.readTxtFile("fake");
		assertThat(result.size()).isEqualTo(2);
	}
	
	@Test
	@DisplayName("after writing 3 lines, content is correct")
	void test4() throws IOException {
		String input="dashdahsdjhasjdaasjdajsd\nhajshdajshdajshdjashdjashd\najsdkjasdhas";
		finder.writeTxtFile("fake", input);
		List<String> result=finder.readTxtFile("fake");
		assertThat(result.get(0)).isEqualTo("dashdahsdjhasjdaasjdajsd");
		assertThat(result.get(1)).isEqualTo("hajshdajshdajshdjashdjashd");
		assertThat(result.get(2)).isEqualTo("ajsdkjasdhas");
	}
	
	@Test
	@DisplayName("after writing 2 lines and clearing, file is empty")
	void test5() throws IOException {
		String input="zmbcxnzxbcnzxcnb\nuwiaueydasjhd9128y3";
		finder.writeTxtFile("fake", input);
		finder.clearTxtFile("fake");
		assertThat(finder.readTxtFile("fake")).isEmpty();
	}
	
	@Test
	@DisplayName("deleting selected record, works fine")
	void test6() throws IOException{
		String input="ahsdjkhasjdkhasjdh\nashdjahsdjahsdjashd\nasjdhasjdhashdasjhdajhsd\nashdjahsdjahsdjahsdjh\nasjdhakhjsdasjhdjashd";
		finder.writeTxtFile("fake", input);
		finder.deleteRecordFromTxtFile("fake", 2);
		assertThat(finder.readTxtFile("fake")).isEqualTo(Arrays.asList("ahsdjkhasjdkhasjdh","ashdjahsdjahsdjashd","ashdjahsdjahsdjahsdjh","asjdhakhjsdasjhdjashd"));
	}
	
	@Test
	@DisplayName("write function appends, rather than clearing and writing clean file from beginning")
	void test7() throws IOException {
		finder.writeTxtFile("fake", "ajsdajsdkjaskdj\n");
		finder.writeTxtFile("fake", "zcxncbxncbasd\n");
		finder.writeTxtFile("fake", "aasdasdasd");
		List<String> content=finder.readTxtFile("fake");
		assertThat(content.get(0)).isEqualTo("ajsdajsdkjaskdj");
		assertThat(content.get(1)).isEqualTo("zcxncbxncbasd");
		assertThat(content.get(2)).isEqualTo("aasdasdasd");
	}
	
	@Test
	@DisplayName("rename function works fine")
	void test8() throws IOException {
		finder.renameTxtFile("fake", "asdasjhdashd");
		Path path=Paths.get(FileService.pathToMainFolder+"/asdasjhdashd.txt");
		assertThat(Files.exists(path)).isTrue();
		finder.renameTxtFile("asdasjhdashd", "fake");
	}
	
	@Test
	@DisplayName("deleting not existing file generates exception")
	void test9() {
		assertThrows(IllegalArgumentException.class, ()->finder.deleteTxtFile("sjhdasd"));
	}
	
	@AfterAll
	@DisplayName("clean-up by deleting fake files")
	static void finish() throws IOException{
		finder.deleteTxtFile("fake");
	}
}
