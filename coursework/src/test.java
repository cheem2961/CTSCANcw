/*
CS-256 Getting started code for the assignment
I do not give you permission to post this code online
Do not copy code
Do not use libraries to do the Slicing, MIP or Volume Rendering. That code must be written by yourself
You may use libraries / IDE to achieve a better GUI
*/
import java.io.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class test extends Application {
	short cthead[][][]; //dataset
	float grey[][][]; //normalised
	short min, max;

	int currZSlice=128; //image start slices
	int currYSlice=128;
	int currXSlice=128;

	float sigma1 = 0; //opacity start slices
	float sigma2 = 0;
	float sigma3 = 0;

    @Override
    public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("CThead Viewer");
		
		try {
			ReadData();
		} catch (IOException e) {
			System.out.println("Error: The CThead file is not in the working directory");
			System.out.println("Working Directory = " + System.getProperty("user.dir"));
			return;
		}

		WritableImage sliceZImage = new WritableImage(256, 256);
		GetSlice(currZSlice, sliceZImage, "Z");

		WritableImage sliceYImage = new WritableImage(256, 256);
		GetSlice(currYSlice, sliceYImage, "Y");

		WritableImage sliceXImage = new WritableImage(256, 256);
		GetSlice(currXSlice, sliceXImage, "X");

		ImageView sliceZView = new ImageView(sliceZImage);
		ImageView sliceYView = new ImageView(sliceYImage);
		ImageView sliceXView = new ImageView(sliceXImage);

		// Do the same for MIP
		WritableImage MIPZImage = new WritableImage(256, 256);
		GetMIP(MIPZImage, "Z");
		ImageView MIPZView = new ImageView(MIPZImage);

		WritableImage MIPYImage = new WritableImage(256, 256);
		GetMIP(MIPYImage, "Y");
		ImageView MIPYView = new ImageView(MIPYImage);

		WritableImage MIPXImage = new WritableImage(256, 256);
		GetMIP(MIPXImage, "X");
		ImageView MIPXView = new ImageView(MIPXImage);



		// Do the same for VR
		WritableImage VRZImage = new WritableImage(256, 256);
		GetVR(VRZImage, "Z",100);
		ImageView VRZView = new ImageView(VRZImage);

		WritableImage VRYImage = new WritableImage(256, 256);
		GetVR(VRYImage, "Y",100);
		ImageView VRYView = new ImageView(VRYImage);

		WritableImage VRXImage = new WritableImage(256, 256);
		GetVR(VRXImage, "X",100);
		ImageView VRXView = new ImageView(VRXImage);


		//create sliders
		Slider sliceZSlider = new Slider(0, 255, currZSlice);

		Slider sliceYSlider = new Slider(0, 255, currYSlice);

		Slider sliceXSlider = new Slider(0, 255, currXSlice);



		Slider sigma1Slider = new Slider(0, 100, sigma1);

		Slider sigma2Slider = new Slider(0, 100, sigma2);

		Slider sigma3Slider = new Slider(0, 100, sigma3);
		
		sliceZSlider.valueProperty().addListener(new ChangeListener<Number>() { 
			public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) { 

				currZSlice = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
		        GetSlice(currZSlice, sliceZImage, "Z"); //go get the slice image
				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            } 
        });

		sliceYSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				currYSlice = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
				GetSlice(currYSlice, sliceYImage, "Y"); //go get the slice image
				//Because sliceYView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});

		sliceXSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				currXSlice = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
				GetSlice(currXSlice, sliceXImage, "X"); //go get the slice image
				//Because sliceXView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});





		sigma1Slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				sigma1 = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
				GetVR(VRZImage, "Z", sigma1);

				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});

		sigma2Slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				sigma2 = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
				GetVR(VRYImage, "Y", sigma2);
				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});

		sigma3Slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				sigma3 = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image

				GetVR(VRXImage, "X", sigma3);
				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});

		//gridpane to display the stuff
		GridPane grid = new GridPane();
        grid.add(sliceZSlider, 0, 0); // Slider at column 0, row 0
		grid.add(sliceYSlider, 1, 0); // Slider at column 0, row 0
		grid.add(sliceXSlider, 2, 0); // Slider at column 0, row 0

		grid.add(sigma1Slider, 0, 4); // Slider at column 0, row 0
		grid.add(sigma2Slider, 1, 4); // Slider at column 0, row 0
		grid.add(sigma3Slider, 2, 4); // Slider at column 0, row 0

        grid.setHgap(2);
        grid.setVgap(2);

      	//add it to the grid
		grid.add(sliceZView, 0, 1);
		grid.add(sliceYView, 1, 1);
		grid.add(sliceXView, 2, 1);

		grid.add(MIPZView, 0, 2);
		grid.add(MIPYView, 1, 2);
		grid.add(MIPXView, 2, 2);

		grid.add(VRZView, 0, 3);
		grid.add(VRYView, 1, 3);
		grid.add(VRXView, 2, 3);

		//scene
        Scene scene = new Scene(grid, 800, 840);
        stage.setTitle("CT Data Viewer");
        stage.setScene(scene);
        stage.show();
    }
    

	//read in the cthead data
	public void ReadData() throws IOException {
		//adrian change to coursework luca change to CTSCANcw
		File file = new File("CTSCANcw/coursework/src/CThead-256cubed.bin");
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		
		int i, j, k;
		
		min = Short.MAX_VALUE; max = Short.MIN_VALUE; //set to extreme values
		short read; //read in
		int b1, b2;
		
		cthead = new short[256][256][256];
		grey= new float[256][256][256];
		//read it through a loop
		for (k=0; k<256; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					b1=((int)in.readByte()) & 0xff;
					b2=((int)in.readByte()) & 0xff;
					read=(short)((b2<<8) | b1);
					if (read<min) min=read;
					if (read>max) max=read;
					cthead[k][j][i]=read;
				}
			}
		}
		System.out.println(min+" "+max);

		for (k=0; k<256; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					grey[k][j][i]=((float) cthead[k][j][i]-(float) min)/((float) max-(float) min);
				}
			}
		}
	}

	public void GetVR(WritableImage image, String axis, float L) {
		//width and height of the image
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();

		PixelWriter image_writer = image.getPixelWriter();

		//normalisation
		L /= 100f;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {


				float[] Ca = {0.0f, 0.0f, 0.0f};
				for (int z = 0; z < width; z++) {

					float CTval = 0.0f;

					//select axis
					if (axis == "Z") {
						CTval = cthead[width - z - 1][y][x];
					} else if (axis == "Y") {
						CTval = cthead[y][width - z - 1][x];
					} else {
						CTval = cthead[y][x][width - z - 1];
					}

					//do the colour / opacity banding from the description
					if (CTval >= -300 && CTval <= 49) {
						float sigma = 0.12f;
						Ca = new float[]{
								sigma * L * 0.82f + Ca[0] * (1 - sigma),
								sigma * L * 0.49f + Ca[1] * (1 - sigma),
								sigma * L * 0.18f + Ca[2] * (1 - sigma)};
					} else if ((CTval >= 50 && CTval <= 299) || (CTval < -300)) {
						float sigma = 0.0f;
						Ca = new float[]{
								sigma * L * 0.0f + Ca[0] * (1 - sigma),
								sigma * L * 0.0f + Ca[1] * (1 - sigma),
								sigma * L * 0.0f + Ca[2] * (1 - sigma)};
					} else {
						float sigma = 0.8f;
						Ca = new float[]{
								sigma * L * 1.0f + Ca[0] * (1 - sigma),
								sigma * L * 1.0f + Ca[1] * (1 - sigma),
								sigma * L * 1.0f + Ca[2] * (1 - sigma)};
					}

				}
				Color color = Color.color(Ca[0], Ca[1], Ca[2]);
				image_writer.setColor(x, y, color);
			}
		}
	}

	public void GetMIP(WritableImage image, String axis) {
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();

		PixelWriter image_writer = image.getPixelWriter();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				float total = 0;
				//get max val along ray
				for (int z = 0; z < width; z++) {
					if (axis == "Z") {
						total = Math.max(total, grey[z][y][x]);
					} else if (axis == "Y") {
						total = Math.max(total, grey[y][z][x]);
					} else {
						total = Math.max(total, grey[y][x][z]);
					}
				}

				Color color = Color.color(total, total, total);
				image_writer.setColor(x, y, color);
			}
		}
	}

	public void GetSlice(int slice, WritableImage image, String axis) {
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		float val;

		PixelWriter image_writer = image.getPixelWriter();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				//choose axis
				if (axis == "Z") {
					val = grey[slice][y][x];
				} else if (axis == "Y") {
					val = grey[y][slice][x];
				} else {
					val = grey[y][x][slice];
				}

				//val = (float) slice / 255.f;

				Color color=Color.color(val, val, val);
				image_writer.setColor(x, y, color);
			}
		}
	}

    public static void main(String[] args) {
        launch();
    }

}