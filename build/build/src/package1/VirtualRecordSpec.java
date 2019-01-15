package package1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VirtualRecordSpec {
	private VirtualRecord fakeRecord;
	
	@BeforeEach
	void init() {
		fakeRecord=new VirtualRecord("cos","tam","jest");
	}
	
	@Test
	@DisplayName("exception is thrown when, no items where passed to contructor")
	void test1() {
		assertThrows(IllegalArgumentException.class, ()->new VirtualRecord());
	}
	
	@Test
	@DisplayName("exception is thrown when number of passed elements is not equal to"
			+ " those specified by class")
	void test2() {
		assertThrows(IllegalArgumentException.class, ()->new VirtualRecord("one"));
		assertThrows(IllegalArgumentException.class, ()->new VirtualRecord("one","two"));
		assertThrows(IllegalArgumentException.class, ()->new VirtualRecord("one","two","three","four"));
	}
	
	@Test
	@DisplayName("when right number of arguments is passed, record is correct")
	void test3() {
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "tam"+VirtualRecord.SEPARATOR+"jest");
	}
	
	@Test
	@DisplayName("items in record are correct")
	void test4() {
		assertThat(fakeRecord.getFromRecord(VirtualRecord.SONGNAME)).isEqualTo("cos");
		assertThat(fakeRecord.getFromRecord(VirtualRecord.SONGPATH)).isEqualTo("tam");
		assertThat(fakeRecord.getFromRecord(VirtualRecord.SONGSIZE)).isEqualTo("jest");
	}
	
	@Test
	@DisplayName("empty string is returned, when passed itemName is not right")
	void test5() {
		assertThat(fakeRecord.getFromRecord(-234)).isEqualTo("");
		assertThat(fakeRecord.getFromRecord(0)).isEqualTo("");
		assertThat(fakeRecord.getFromRecord(4)).isEqualTo("");
		assertThat(fakeRecord.getFromRecord(2334)).isEqualTo("");
	}
	
	@Test
	@DisplayName("when itemName is not right, setting method doesn't change record")
	void test6() {
		fakeRecord.setInRecord(-23, "nowa");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "tam"+VirtualRecord.SEPARATOR+"jest");
		fakeRecord.setInRecord(0, "nowa");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "tam"+VirtualRecord.SEPARATOR+"jest");
		fakeRecord.setInRecord(4, "nowa");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "tam"+VirtualRecord.SEPARATOR+"jest");
		fakeRecord.setInRecord(1323, "nowa");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "tam"+VirtualRecord.SEPARATOR+"jest");
	}
	
	@Test
	@DisplayName("items in record, are correcttly changed")
	void test7() {
		fakeRecord.setInRecord(VirtualRecord.SONGPATH, "sdajhsdhasjdh");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "sdajhsdhasjdh"+VirtualRecord.SEPARATOR+"jest");
		fakeRecord.setInRecord(VirtualRecord.SONGSIZE, "bxcznxcbiqwd");
		assertThat(fakeRecord.toString()).isEqualTo("cos"+VirtualRecord.SEPARATOR+
		                                            "sdajhsdhasjdh"+VirtualRecord.SEPARATOR+"bxcznxcbiqwd");
		fakeRecord.setInRecord(VirtualRecord.SONGNAME, "123712637yajdhe");
		assertThat(fakeRecord.toString()).isEqualTo("123712637yajdhe"+VirtualRecord.SEPARATOR+
		                                            "sdajhsdhasjdh"+VirtualRecord.SEPARATOR+"bxcznxcbiqwd");
	}
	
	@Test
	@DisplayName("method equals throws exception, when passed object is null")
	void test8() {
		assertThrows(NullPointerException.class, ()->fakeRecord.equals(null));
	}
	
	@Test
	@DisplayName("method equals false, when passed object is not type of "
			+ "VirtualRecord")
	void test9() {
		assertThat(fakeRecord.equals(Integer.valueOf(2323))).isFalse();
	}
	
	@Test
	@DisplayName("method equals false, when two records contains different items")
	void test10() {
		VirtualRecord tmp=new VirtualRecord("dajshdjhasd","asjdahjsdhj","sahdjashd");
		assertThat(fakeRecord.equals(tmp)).isFalse();
	}
	
	@Test
	@DisplayName("method equals false, when two records contains one different items")
	void test11() {
		VirtualRecord tmp=new VirtualRecord("cos","asjdahjsdhj","jest");
		assertThat(fakeRecord.equals(tmp)).isFalse();
	}
	
	@Test
	@DisplayName("method equals true, when two records contains same items")
	void test12() {
		VirtualRecord tmp=new VirtualRecord("cos","tam","jest");
		assertThat(fakeRecord.equals(tmp)).isTrue();
	}

}
