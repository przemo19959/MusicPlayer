package package2;

import java.io.IOException;

import package1.VirtualList;

/**
 * This interface contains methods for virtual list management and general adding new files
 * utilies.
 * @author hex
 * @version 1.0
 */
public interface Adder {
	/**
	 * This method initialize DirectoryChooser object, using that object user, can choose
	 * new folder from local computer file system. If folder is chosen, then private method
	 * is started. That method scans specified folder for file with format .mp3 or .wav. Next
	 * based on fact wether chosen folder exists file in main folder is updated or new file is
	 * created in parallel with with new virtual list.
	 * @throws IOException
	 */
	public void chooseFolder() throws IOException;
	
	/**
	 * This method gets name of virtual list
	 * @param index of virtual list
	 * @return name of specified list
	 */
	public String getVirtualListName(int index);
	
	/**
	 * This method gets virtual list
	 * @param index of virtual list
	 * @return virtual list specified by index
	 */
	public VirtualList getVirtualList(int index);
	
	/**
	 * This method deletes virtual list(in parallel from main folder).
	 * @param index of list to be deleted
	 * @throws IOException
	 */
	public void deleteVirtualList(int index) throws IOException;
	
	/**
	 * This method returns number of virtual lists, which is number of
	 * files in main folder.
	 * @return number of virtual lists
	 */
	public int getNumberOfVirtualLists();
	
	/**
	 * This method scans mainFolder for already existing files at start-up. For every
	 * file with txt format, new virtual list is created by calling 
	 * <strong>VirtualList.fileToVirtualList()</strong>
	 * @throws IOException
	 */
	public void scanFolderForTxtFiles() throws IOException;
}
