/*
 * 
 */
package application;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Accordion;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;

public class SampleController {
	
	public String url="";
	@FXML Label infoBar;
	MediaPlayer player;
	@FXML Slider timeProgress;
	@FXML BorderPane mainPane;
	@FXML HBox menuBox;
	@FXML Label currentTimeLabel;
	@FXML Label durationLabel;
	
	XYChart.Series<String, Number> dataSeries1=new XYChart.Series<>();
	@FXML BarChart<String,Number> barChart;
	
	private int numberOfBands=128;
	private double effectiveNumberOfBands=numberOfBands/2.0;
	@FXML NumberAxis magnitudeValue;

	private Stage stage;
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private Media media;
	private AudioSpectrumListener audioSpectrumListener;
	private Timeline tim1;
	private Timeline shiftingLabelTimeline;
	
	private boolean changeTime=false;
	private int timeValue=0;

	@FXML Accordion songsFull;
	@FXML Text msg;
	@FXML Pane bottomPane;
	private double msgWidth;
	@FXML Circle circleButton1;
	@FXML Group playGroup;
	@FXML Group pauseGroup;
	@FXML Circle circleButton2;
	@FXML Group stopGroup;
	@FXML Circle circleButton3;
	@FXML Group previousGroup;
	@FXML Circle circleButton4;
	@FXML Circle circleButton5;
	@FXML Group nextGroup;
	@FXML Group importGroup;
	@FXML Circle circleButton6;
	@FXML Slider volumneSlider;
	@FXML ProgressBar progressBar;
	@FXML TextField findField;
	private ListManager listManager;
	
	private StringConverter<Double> stringConverter = new StringConverter<Double>() {
		@Override
		public String toString(Double object) {
			long seconds=0,minutes=0,remainingseconds=0;
			seconds=object.longValue();
			minutes = TimeUnit.SECONDS.toMinutes(seconds);
            remainingseconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);
            return String.format("%2d", minutes) + ":" + String.format("%02d", remainingseconds);
		}
		
		@Override
		public Double fromString(String string) {
			return null;
		}
	};
	
	@FXML void initialize() {
		listManager=new ListManager(songsFull, this);
		try {
			listManager.fillSongList();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		listManager.addListenersToPanes();

		tim1=new Timeline(new KeyFrame(Duration.millis(1000), val->{
			if(player!=null && player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
				double value=player.getCurrentTime().toSeconds();
				timeProgress.setValue(value);
				progressBar.setProgress((timeProgress.getValue()/timeProgress.getMax()));
				currentTimeLabel.setText(stringConverter.toString(value));
			}
		}));
		tim1.setCycleCount(Animation.INDEFINITE);
		tim1.play();
		
		findField.textProperty().addListener(listener->{
			try {
				listManager.find(findField.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		audioSpectrumListener=new AudioSpectrumListener() {
			@Override
			public void spectrumDataUpdate(double timestamp, double duration,
					float[] magnitudes, float[] phases) {
				IntStream stream=IntStream.range(0, magnitudes.length/2);
				stream.forEach(i->{
					float value=decibelsToGain(magnitudes[i]-player.getAudioSpectrumThreshold());
					dataSeries1.getData().get(i).setYValue(value);
				});					
			}
		};
			
		//obs³uga wyœwietlania g³oœnoœci
		volumneSlider.setOnMouseDragged(val->{
			player.setVolume(volumneSlider.getValue());
			infoBar.setText("Volume: "+String.format("%.2f",volumneSlider.getValue()*100)+"%");
		});
		
		volumneSlider.setOnMouseClicked(val->{
			player.setVolume(volumneSlider.getValue());
			infoBar.setText("Volume: "+String.format("%.2f",volumneSlider.getValue()*100)+"%");
		});
	
		Circle[] circleTable= {circleButton1,circleButton2,circleButton3,circleButton4,circleButton5,circleButton6};
		Group[] groupTable= {playGroup,pauseGroup,stopGroup,previousGroup,nextGroup,importGroup};
		
		//obs³uga zmiany zabarwienia przycisków po najechaniu myszk¹ na nie
		//obs³uga zmiany promienia przycisków po naciœniêciu
		IntStream.range(0, groupTable.length).forEach(i->{
			groupTable[i].setOnMouseEntered(val->circleTable[i].setFill(Color.ORANGE));
			groupTable[i].setOnMouseExited(val->circleTable[i].setFill(Color.DARKORANGE));
			groupTable[i].setOnMousePressed(val->circleTable[i].setRadius(circleTable[i].getRadius()-2));
		});
		
		durationLabel.setText("3:00");
		barChart.getData().add(dataSeries1);
		
		IntStream.range(0, (int)effectiveNumberOfBands).forEach(i->dataSeries1.getData().add(new XYChart.Data<String, Number>(""+i, 0)));
		
		//obs³uga przyciœniêcia paska postêpu czasu
		timeProgress.setOnMousePressed(val->{
			tim1.stop();
		});
		
		
		timeProgress.setOnMouseReleased(val->{
			timeValue=(int)timeProgress.getValue();
			progressBar.setProgress((timeProgress.getValue()/timeProgress.getMax()));
			changeTime=true;
			player.pause();
		});
		
		timeProgress.setOnMouseDragged(val->{
			if(player!=null) {
				currentTimeLabel.setText(stringConverter.toString(timeProgress.getValue()));
				progressBar.setProgress((timeProgress.getValue()/timeProgress.getMax()));
			}
		});
		
		mainPane.widthProperty().addListener((obs,old, next)->{
			menuBox.setPrefWidth(next.doubleValue());
			bottomPane.setPrefWidth(next.doubleValue());
		});
		
		playGroup.setOnMouseReleased(val->{
			if(player.getStatus().equals(MediaPlayer.Status.STOPPED) ||
					player.getStatus().equals(MediaPlayer.Status.PAUSED)) {
				player.play();
			}
			circleButton1.setRadius(circleButton1.getRadius()+2);
		});
		
		pauseGroup.setOnMouseReleased(ev->{
			if(player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
				player.pause();
			}
			circleButton2.setRadius(circleButton2.getRadius()+2);
		});
				
		nextGroup.setOnMouseReleased(val->{
			try {
				listManager.moveToNextSong();
			} catch (IOException e) {
				infoBar.setText("Error: "+e);
			}
			circleButton5.setRadius(circleButton5.getRadius()+2);
		});
		
		previousGroup.setOnMouseReleased(val->{
			try {
				listManager.moveToPreviousSong();
			} catch (IOException e) {
				infoBar.setText("Error: "+e);
			}
			circleButton4.setRadius(circleButton4.getRadius()+2);
		});
		
		stopGroup.setOnMouseReleased(ev->{
			if(player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
				player.stop();
			}
			circleButton3.setRadius(circleButton3.getRadius()+2);
		});
		
		importGroup.setOnMouseReleased(ev->{
			try {
				listManager.addNewList();
			} catch (IOException e) {
				infoBar.setText("Error: "+e);
			}
			circleButton6.setRadius(circleButton6.getRadius()+2);
		});
	}
	
	public void prepareMedia(String path) throws MalformedURLException  {
		if(media!=null)
			media=null;
		media=new Media(Paths.get(path).toUri().toURL().toString());
		infoBar.setText("Successfully opened file");
		if(player!=null) {
			timeProgress.setDisable(true);
			volumneSlider.setDisable(true);
			player.dispose();
		}
		player=new MediaPlayer(media); //utworz media z pliku
		timeProgress.setDisable(false);
		volumneSlider.setDisable(false);
		
		//gdy player jest w stanie gotowoœci
		player.setOnReady(()->{
			double seconds=player.getStopTime().toSeconds(); //czas trwania pliku audio
			int minutes=(int)(seconds/60.0); //policz ile to minut
			int remainingSeconds=(int)seconds%60; //ile sekund reszty
			durationLabel.setText(minutes+":"+remainingSeconds); //wyœwietl ca³kowity czas
			timeProgress.setMax(seconds); //ustaw slider na wartoœæ czasu trwania pliku audio
		});
				
		//obs³uga playera po pauzie
		player.setOnPaused(()->{
			if(changeTime) {
				changeTime=false;
				timeProgress.setValue(timeValue);
				player.seek(Duration.seconds(timeValue));
				player.play();
				tim1.play();
			}else {
				setGroupDisableState(false, true, true);
			}
		});
		
		//obs³uga playara po w³¹czeniu odtwarzania
		player.setOnPlaying(()->{
			setGroupDisableState(true, false, false);
			if(tim1.getStatus().equals(Animation.Status.STOPPED))
				tim1.play();
		});
		
		//obs³uga playara po zastopowaniu odtwarzania
		player.setOnStopped(()->{
			setGroupDisableState(false, true, true);
		});
		
		//obs³uga zakoñczenia odtwarzania pliku
		player.setOnEndOfMedia(()->{
			try {
				listManager.moveToNextSong();
			} catch (IOException e) {
				infoBar.setText("Error: "+e);
			}
		});
		
		//konfiguracja widma audio
		player.setAudioSpectrumThreshold(-80);
		magnitudeValue.setUpperBound(700);
		magnitudeValue.setTickUnit(magnitudeValue.getUpperBound()/10);
		player.setAudioSpectrumNumBands(numberOfBands);
		player.setAudioSpectrumListener(audioSpectrumListener);
	}
	
	public void setGroupDisableState(boolean playGroup, boolean pauseGroup, boolean stopGroup) {
		this.playGroup.setDisable(playGroup);
		this.stopGroup.setDisable(stopGroup);
		this.pauseGroup.setDisable(pauseGroup);
	}

	public void shiftingTextInit(String...message) {
		if(shiftingLabelTimeline!=null) {
			shiftingLabelTimeline.pause();	//jeœli timeline dzia³a, to zapauzuj animacjê
			shiftingLabelTimeline=null;
		}
		if(message.length>0)
			msg.setText(".:: "+songsFull.getPanes().get(listManager.getPlayingListIndex()).getText()+" :: "+message[0]+" ::.");
	    msgWidth = msg.getLayoutBounds().getWidth();
	    KeyValue initKeyValue = new KeyValue(msg.xProperty(), bottomPane.getWidth());
	    KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);
	    KeyValue endKeyValue = new KeyValue(msg.xProperty(), -msgWidth);
	    int t=(int)((bottomPane.getWidth()+msg.getLayoutBounds().getWidth())/55.0);	
	    KeyFrame endFrame = new KeyFrame(Duration.seconds(t), endKeyValue);
	    shiftingLabelTimeline = new Timeline(initFrame, endFrame);
	    shiftingLabelTimeline.setCycleCount(Timeline.INDEFINITE);
	    shiftingLabelTimeline.play();
	}
	
	private float decibelsToGain(float valueInDecibels) {
		float result=(float)Math.pow(10, (valueInDecibels/20));
		return result;
	}
}
