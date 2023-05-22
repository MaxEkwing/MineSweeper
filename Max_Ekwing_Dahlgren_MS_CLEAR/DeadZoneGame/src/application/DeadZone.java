package application;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
public class DeadZone extends Application {
	int column; 
    int row;
    int  win = 0; 
	boolean suspect = false; 
	Button btnStart;
	int mines = 15;
	static Button[][] btn;
	Label timeLabel = new Label();
	static ArrayList<Button> minor = new ArrayList<Button>();
	GridPane gridPane = new GridPane();
	AnimationTimer clock;
	long minutes;
	long seconds;
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) {
		try { 
			btnStart = new Button("Start");
			btnStart.setMinSize(100, 25); 
			btn = new Button[8][8];
			for (int column = 0; column <= 7; column++) {
				for (int row = 0; row <= 7; row++) {
				     Button button = new Button();
				     button.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
				     button.setStyle("-fx-background-color: #3e8f04");
				     button.setPrefSize(35, 35);
				     gridPane.add(button, column, row);
				     gridPane.setHgap(2);
				     gridPane.setVgap(4);
				     btn[column][row] = button;
				     button.setOnMouseClicked(new EventHandler<MouseEvent>()  {
				    	 public void handle(MouseEvent mouseClick) {	
				    		 if(mouseClick.getButton() == (MouseButton.PRIMARY)&& mouseClick.getClickCount() == 1){
				    			 button.setStyle("-fx-background-color: #a89345");		
					        	 MineChecker();	
								 win++;
								 if (win == 49) {
									 winScreen();
									 primaryStage.close();
								     Platform.runLater(() -> new DeadZone().start(new Stage()));	
								     }
								 }
				    		 if (mouseClick.getButton() == (MouseButton.SECONDARY)&& mouseClick.getClickCount() == 1){
				    			 button.setStyle("-fx-background-color: #DB4437");
				    			 }
				    		 }
				    	 });
				     }
				}	
			Random random = new Random();
			int mineCounter = 0;
			while (mineCounter < mines) {
				int rowRandom = random.nextInt(7);
				int columnRandom = random.nextInt(7);
				Button btnHasMine = btn[columnRandom][rowRandom];
				if (!minor.contains(btnHasMine)) {
					minor.add(btnHasMine);
					mineCounter++;
				}
				btn[columnRandom][rowRandom].setOnAction(event ->{
					if (suspect == true) {
						btn[columnRandom][rowRandom].setStyle("-fx-background-color: #d9250d");
					}
					else {
				btn[columnRandom][rowRandom].setText("*");
				gameOver();
				primaryStage.close();
				Platform.runLater(() -> new DeadZone().start(new Stage()));
					}
				});	
			}
			btnStart.setOnMouseClicked(new EventHandler<MouseEvent>()  {
				public void handle(MouseEvent mouseEvent) {
						clock = new AnimationTimer() {
						long clock = System.currentTimeMillis();
						
				public void handle(long now) {
						long elapsedTime = System.currentTimeMillis() - clock;
						elapsedTime = elapsedTime + 1000; 
						minutes = (elapsedTime/60000) % 60; 
						seconds = (elapsedTime/1000) % 60;
						seconds = seconds - 1; 
						String seconds_string = String.format("%02d", seconds); 
						String minutes_string = String.format("%02d", minutes);
						timeLabel.setText(minutes_string+":"+seconds_string);	
					    btnStart.setText("Reset Game!");
					    btnStart.setOnMouseClicked(event -> {
					    	primaryStage.close();
							Platform.runLater(() -> new DeadZone().start(new Stage()));
					    	});
					    } 
						}; 
						clock.start();
				}
				});	   
			timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 35));
			timeLabel.setStyle("-fx-background-color: #fafafa");
			
			HBox topContent = new HBox();
			HBox bottomContent = new HBox();
			
			topContent.getChildren().addAll(timeLabel);
			topContent.alignmentProperty().setValue(Pos.CENTER);
			bottomContent.alignmentProperty().setValue(Pos.CENTER);
			bottomContent.getChildren().addAll(btnStart);

			gridPane.setAlignment(Pos.CENTER);		
			
			BorderPane borderPane = new BorderPane();
			borderPane.setMinSize(500, 400);
			borderPane.setTop(topContent);
			borderPane.setCenter(gridPane);
			borderPane.setBottom(bottomContent);
			borderPane.setStyle("-fx-background-color: #2e6904");
			Scene scene = new Scene(borderPane);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("DeadZone");
			primaryStage.setScene(scene);
			primaryStage.show();
			}   
		    catch(Exception e) {
			e.printStackTrace();
		}
	}
	private static void MineChecker() {

		for (int row = 0; row <= 7; row++) {
	    for (int column = 0; column <= 7; column++) {
	    	 int localmine = 0; 
	    for (Button tile : get_Localbombs(column, row)) {
		if (minor.contains(tile)) {
			localmine++;
		   }   		
		}		
	    Button mine = btn[column][row];
	    if (!minor.contains(mine))
	    	 btn[column][row].setOnAction(e -> 
	    	 mine.setText(""));
	    if (!minor.contains(mine) && localmine != 0) {
		     String finalLocalMine = Integer.toString(localmine);
		     btn[column][row].setOnAction(e -> mine.setText(finalLocalMine));
	           }
	            btn[column][row].setFont(Font.font("Arial", FontWeight.BOLD, 15));
	    	}
	    }
	}
	private static ArrayList<Button> get_Localbombs(int column, int row) {
			ArrayList<Button> SurroundingBtn = new ArrayList<Button>();
			for (int col = -1; col <= 1; col++) {
			for (int roW = -1; roW <= 1; roW++) {
			if (!(col == 0 && roW == 0)) { 
		try {
			SurroundingBtn.add(btn[column+col][row+roW]);
		}
		catch(Exception e) {
		           }
				}
			}
		}
		return SurroundingBtn;
	}
	private void winScreen() {
		JOptionPane.showMessageDialog(null, "You won!");		
	}
	private void gameOver() {
		JOptionPane.showMessageDialog(null, "BOOOOOooooooommm!");		
	}
}
