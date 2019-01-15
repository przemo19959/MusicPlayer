package package1;

import java.io.IOException;

/**
 * This interface contains all method for managing virtual lists in application. Each file located under
 * <strong>FileService.mainPathToFolder</strong> contains data about added audio files to application. After start-up
 * of application those file are read and virtual lists are created, so that every time info about track is needed it is 
 * taken form those lists rather than form files.
 * @author hex
 * @version 1.0
 *
 */
public interface VirtualList {
	/**
	 * This method clears virtual list, and in parallel it clears coresponding file
	 * @throws IOException
	 */
	public void clearVirtualList() throws IOException;
	
	/**
	 * This method returns number of records in virtual list. This number corresponds to number
	 * of audio files of that list
	 * @return
	 */
	public int getNumberOfVirtualRecords();
	
	/**
	 * This method deletes file coresponding to that list
	 * @throws IOException
	 */
	public void deleteVirtualList() throws IOException;
	
	/**
	 * This method deletes item of given index from list and in parallel form coresponding file
	 * @param index index of record to be removed form list
	 * @throws IOException
	 */
	public void deleteVirtualRecordFromVirtualList(int index) throws IOException;
	
	/**
	 * This method adds record to this list, and in parallel those items are added to 
	 * corresponding file
	 * @param records
	 * @throws IOException
	 */
	public void addVirtualRecord(VirtualRecord... records) throws IOException;
	
	/**
	 * This method gets name of virtual list, which coresponds to file name.
	 * @return
	 */
	public String getVirtualListName();
	
	/**
	 * This method gets virtual record from virtual list
	 * @param index index of record
	 * @return virtual record specified by index
	 */
	public VirtualRecord getVirtualRecord(int index);
	
	/**
	 * This method creates virtual list from already existing file. This method is called by application
	 * at start-up to create virtual list from files located in main folder. In run-time it is possible to
	 * add new virtual list, but then new file is created and audio files are imported from
	 * chosen directory, in that case this method is not called.
	 * @param fileName file name from which data is imported
	 * @return fully working virtual list
	 * @throws IOException
	 */
	public VirtualListImpl fileToVirtualList(String fileName) throws IOException;
}
