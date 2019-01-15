package application;

import java.io.IOException;

import com.google.common.base.Preconditions;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.media.MediaPlayer;
import package1.VirtualRecord;
import package2.Adder;
import package2.AdderImpl;


public class ListManager {
	private int playingSongIndex=-1;
	private int playingListIndex=-1;
	private int selectedSongIndex;
	private int selectedListIndex;
	private int filteredIndex;
	private boolean filterOn=false;
	
	private boolean isTrackDeleted=false;
	
	private Accordion root;
	private SampleController controller;
	private TrackContext trackContext=new TrackContext(this,controller);
	private ListContext listContext=new ListContext(this,controller);
	private SongList playingListView;
	private SongList selectedListView;
	
	private Adder adder;
	
	public ListManager(Accordion root, SampleController controller) {
		this.root=root;
		this.controller=controller;
		clearPanes();
		adder=new AdderImpl(this.controller.getStage());
	}
	
	public void clearPanes() {
		root.getPanes().clear();
	}
	
	public Adder getAdder() {
		return adder;
	}
	 
	public int getPlayingListIndex() {
		return playingListIndex;
	}
	
	public void removePane(int index) {
		root.getPanes().remove(index);
	}
	
	public void addPaneToAccordion(TitledPane pane) {
		root.getPanes().add(pane);
	}
	
	public int getAccordionSize() {
		return root.getPanes().size();
	}
	
	//wype�nienie ca�ej listy piosenkami
	public void fillSongList() throws IOException {
		adder.scanFolderForTxtFiles();
		for(int i=0;i<adder.getNumberOfVirtualLists();i++)
			addPane(i);
	}
	
	//dodanie Pane a w nim listy z utworami
	private void addPane(int index) throws IOException {
		SongList list=new SongList(adder.getVirtualListName(index));
		for(int i=0;i<adder.getVirtualList(index).getNumberOfVirtualRecords();i++) {
			list.addSongToListView((i+1)+". "+adder.getVirtualList(index).getVirtualRecord(i).getFromRecord(VirtualRecord.SONGNAME)+" :: "+
					adder.getVirtualList(index).getVirtualRecord(i).getFromRecord(VirtualRecord.SONGSIZE));
			
		}
		addPaneToAccordion(list.getHeader());
	}
	
	//listenery dla wszyskitich pan�w w accordion
	public void addListenersToPanes() {
		for(int i=0;i<getAccordionSize();i++) {
			addListenerToList(i);
			addListenerToPane(i);
		}
	}
	
	public Node getPaneContent(int index) {
		Preconditions.checkElementIndex(index, getAccordionSize());
		return root.getPanes().get(index).getContent();
	}
	
	public TitledPane getPaneFromAccordion(int index) {
		Preconditions.checkElementIndex(index, getAccordionSize());
		return root.getPanes().get(index);
	}
	
	private void addListenerToList(int index) {
		getPaneContent(index).setOnMouseClicked(val->{
			if(trackContext.isShowing())
				trackContext.hide();
			selectedListIndex=index;
			selectedListView=(SongList)getPaneContent(selectedListIndex); //pobierz wskazan� list�
			
			if(filterOn) {
				String tmp=selectedListView.getSelectedItem();
				filteredIndex=Integer.valueOf(tmp.substring(0, tmp.indexOf("."))).intValue()-1;
			}
			selectedSongIndex=selectedListView.getSelectedItemIndex();
			
			if(val.getButton().equals(MouseButton.PRIMARY) && val.getClickCount()==2) {
				playSong(selectedListIndex,selectedSongIndex);
			}else if(val.getButton().equals(MouseButton.SECONDARY) && filterOn==false){ //je�li prawym na piosenk�
				trackContext.show(getPaneFromAccordion(index), val.getScreenX(), val.getScreenY());
			}
		});
	}
	
	private void addListenerToPane(int index) {
		getPaneFromAccordion(index).setOnMouseClicked(val->{
			if(listContext.isShowing())
				listContext.hide();
			selectedListIndex=index;
			if(val.getButton().equals(MouseButton.SECONDARY) && filterOn==false) {
				listContext.show(getPaneFromAccordion(index),val.getScreenX(), val.getScreenY());
			}
		});
	}
	
	public void playSong(int selectedListIndex, int selectedSongIndex) {
		playingSongIndex=selectedSongIndex; //aktualizacja indeksu graj�cej piosenki
		playingListIndex=selectedListIndex;
		playingListView=(SongList)getPaneContent(playingListIndex); //pobierz wskazan� list�
		try {
			changeSong();
		} catch (IOException e1) {
			controller.infoBar.setText("Error:"+e1);
		}	
	}
	
	private void changeSong() throws IOException{
		playingListView.getSelectionModel().select(playingSongIndex);
		if(filterOn) {
			String tmp=playingListView.getSelectedItem();
			filteredIndex=Integer.valueOf(tmp.substring(0, tmp.indexOf("."))).intValue()-1;
			setNewSongPath(filteredIndex);
		}else
			setNewSongPath(playingSongIndex);
		controller.prepareMedia(controller.url); //przygotuj nowy utwor
		controller.shiftingTextInit(playingListView.getSelectedItem());
		root.setExpandedPane(getPaneFromAccordion(playingListIndex));
		controller.player.play();
	}
	
	private void setNewSongPath(int songIndex) {
		controller.url=adder.getVirtualList(playingListIndex)
				.getVirtualRecord(songIndex)
				.getFromRecord(VirtualRecord.SONGPATH);
	}
	
	public void addNewList() throws IOException {
		adder.chooseFolder();
		if(adder.getNumberOfVirtualLists()>getAccordionSize()) { //je�li wybrano 
			addPane(adder.getNumberOfVirtualLists()-1);
			addListenerToList(adder.getNumberOfVirtualLists()-1);
			addListenerToPane(adder.getNumberOfVirtualLists()-1);
		}else
			refreshSongList();	
	}
	
	//funkcja od�wierzaj�ca stan listy
	public void refreshSongList() throws IOException{
		for(int i=0;i<getAccordionSize();i++) {
			SongList tmp=(SongList)getPaneContent(i);
			if(tmp.getNumberOfItems()!=adder.getVirtualList(i).getNumberOfVirtualRecords()) {
				tmp.clearListView();
				for(int j=0;j<adder.getVirtualList(i).getNumberOfVirtualRecords();j++) {
					tmp.addSongToListView((j+1)+". "+adder.getVirtualList(i).getVirtualRecord(j).getFromRecord(VirtualRecord.SONGNAME)+" :: "+
							adder.getVirtualList(i).getVirtualRecord(j).getFromRecord(VirtualRecord.SONGSIZE));
				}
			}
		}
	}
	
	public void removeSelectedItemFromListView() {
		SongList tmp=(SongList)getPaneContent(selectedListIndex);
		tmp.removeItem(selectedSongIndex);
	}
	
	public void trackUpdateIndexesDown() {
		if(selectedSongIndex<playingSongIndex) {
			playingSongIndex--;
		}else if(selectedSongIndex==playingSongIndex) {
			playingSongIndex--;
		}
	}
	
	public void trackUpdateIndexesUp() {
		if(playingSongIndex>selectedSongIndex) {
			playingSongIndex--;
		}
	}
	
	public void moveToPreviousSong() throws IOException {
		if(playingSongIndex!=-1) { //je�li wybrano piosenk�
			if(controller.player.getStatus().equals(MediaPlayer.Status.PLAYING))
				controller.player.pause();
			if(isTrackDeleted) {
				trackUpdateIndexesUp();
				isTrackDeleted=false;
			}
			if(playingSongIndex>0) { //je�li grana jest nie pierwsza piosenka z listy
				playingSongIndex--;
			}else if(playingSongIndex==0) { //je�li cofni�cie wymaga przej�cia do poprzedniej listy
				if(playingListIndex>0 && filterOn==false) //aktualnie wybrana lista nie jest pierwsz�	
					playingListIndex--;
				else if(playingListIndex==0 && filterOn==false) //je�li wybrana lista jest pierwsza, to wybierz ostatni� list�(przewini�cie)
					playingListIndex=getAccordionSize()-1;
				playingListView=(SongList)getPaneContent(playingListIndex); //pobierz aktualnie odtwarzan� list�
				playingSongIndex=playingListView.getItems().size()-1;
			}
			playingListView=(SongList)getPaneContent(playingListIndex); //pobierz aktualnie odtwarzan� list�
			changeSong();
		}
	}
	
	public void moveToNextSong() throws IOException{
		if(playingSongIndex!=-1) { //je�li wybrano piosenk�(na samym pocz�tku-dwukrotne klikni�cie)
			if(controller.player.getStatus().equals(MediaPlayer.Status.PLAYING))
				controller.player.pause();
			if(isTrackDeleted) {
				trackUpdateIndexesDown();
				isTrackDeleted=false;
			}
			if(playingSongIndex<playingListView.getNumberOfItems()-1) { //je�li grana jest przedostatnia piosenka lub wcze�niejsza
				playingSongIndex++;
			}else if(playingSongIndex==playingListView.getNumberOfItems()-1) { //je�li ostatnia piosenka z listy
				if(playingListIndex<getAccordionSize()-1 && filterOn==false) 	//je�li nie ostatnia lista z ca�ej g��wnej listy
					playingListIndex++; //kolejna lista
				else if(playingListIndex==getAccordionSize()-1 && filterOn==false) //je�li ostatnia lista
					playingListIndex=0; //na pocz�tek g��wnej listy
				playingSongIndex=0; 
			}
			playingListView=(SongList)getPaneContent(playingListIndex); //pobierz aktualnie odtwarzan� list�
			changeSong();
		}
	}
	
	public void deleteListContextHelper() throws IOException {
		if(getAccordionSize()>0) { //je�li s� listy do usuni�cia							
			adder.deleteVirtualList(selectedListIndex);
			removePane(selectedListIndex);
			addListenersToPanes();
			if(getAccordionSize()==0) {
				playingSongIndex=playingListIndex=-1;
			}else {
				if(playingListIndex>selectedListIndex) {
					playingListIndex--;
				}else if(playingListIndex==selectedListIndex) {
					playingSongIndex=-1;
				}
			}
		}
	}
	
	public void deleteTrackContextHelper() throws IOException {
		adder.getVirtualList(selectedListIndex)
			.deleteVirtualRecordFromVirtualList(selectedSongIndex);
		removeSelectedItemFromListView();
		isTrackDeleted=true;
	}
	
	public void playTrackContextHelper() {
		playSong(selectedListIndex,selectedSongIndex);
	}
	
	public void playListContextHelper() {
		playSong(selectedListIndex,0);
	}
	
	public void find(String input) throws IOException {
		filterOn=(input.equals(""))?false:true;
		if(!input.equals(""))
			playingSongIndex=-1;
		refreshSongList();
		for(int i=0;i<getAccordionSize();i++) {
			SongList tmp=(SongList)getPaneContent(i);
			tmp.filterListView(input);
			if(tmp.getItems().size()==0)
				tmp.setDisable(true);
			else
				tmp.setDisable(false);
		}
	}
}
