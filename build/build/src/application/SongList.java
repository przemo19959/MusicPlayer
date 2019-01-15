package application;


import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

public class SongList extends ListView<String> {
	private TitledPane header;
	//tworzenie listy
	public SongList(String title) {
		Preconditions.checkNotNull(title);
		header=new TitledPane(title,this);
		this.setItems(FXCollections.observableArrayList());
	}
	
	public TitledPane getHeader() {
		return header;
	}
	
	//dodawanie piosenek do listy
	public void addSongToListView(String song) {
		this.getItems().add(song);
	}
	
	public void clearListView() {
		this.getItems().clear();
	}
	
	public int getNumberOfItems() {
		return this.getItems().size();
	}
	
	public int getSelectedItemIndex() {
		return this.getSelectionModel().getSelectedIndex();
	}
	
	public void removeItem(int index) {
		Preconditions.checkElementIndex(index, getNumberOfItems());
		this.getItems().remove(index);
	}
	
	public String getSelectedItem() {
		return this.getSelectionModel().getSelectedItem();
	}
	
	public void filterListView(String value) {
		this.getItems().removeAll(this.getItems().stream()
		                          .filter(item->!item.toLowerCase().contains(value.toLowerCase()))
		                          .collect(Collectors.toList()));
	}
}
