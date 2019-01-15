package application;

import java.io.IOException;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TrackContext extends ContextMenu {
	private final static String ITEM1="Play Song";
	private final static String ITEM2="Delete Song";
	
	private MenuItem[] items;
	private String[] itemsNames= {ITEM1,ITEM2};
	private ListManager listManager;
	private SampleController controller;
	
	public TrackContext(ListManager listManager, SampleController controller) {
		this.listManager=listManager;
		this.controller=controller;
		items=new MenuItem[itemsNames.length];
		addItemsToContext();
		addListenerToContext();
	}
	
	private void addItemsToContext() {
		for(int i=0;i<items.length;i++)
			items[i]=new MenuItem(itemsNames[i]); //wype³nij tablicê nazwami elementów
		this.getItems().addAll(items); //dodaj do kontekstu.
	}
	
	private void addListenerToContext() {
		for(int i=0;i<items.length;i++) {
			if(items[i].getText().equals(ITEM1)) {
				this.getItems().get(i).setOnAction(val->{
					listManager.playTrackContextHelper();
				});
			}else if(items[i].getText().equals(ITEM2)) {
				this.getItems().get(i).setOnAction(val->{
					try {
						listManager.deleteTrackContextHelper();
					} catch (IOException e) {
						controller.infoBar.setText("Error:"+e);
					}
				});
			}
		}
	}
}
