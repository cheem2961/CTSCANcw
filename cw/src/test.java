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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class test extends Application {
	short cthead[][][]; //store the 3D volume data set
	float grey[][][]; //store the 3D volume data set converted to 0-1 ready to copy to the image
	short min, max; //min/max value in the 3D volume data set

	int currZSlice=128;
	int currYSlice=128;

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

		//We need 3 things to see an image
		//1. We need to create the image
		WritableImage sliceZImage = new WritableImage(256, 256); //allocate memory for the image
		GetZSlice(currZSlice, sliceZImage); //make the image - in this case go get the slice and copy it into the image

		WritableImage sliceYImage = new WritableImage(256, 256); //allocate memory for the image
		GetYSlice(currYSlice, sliceYImage); //make the image - in this case go get the slice and copy it into the image
		//2. We link a view in the GUI to that image
		ImageView sliceXView = new ImageView(sliceZImage); //and then see 3. below
		ImageView sliceYView = new ImageView(sliceYImage); //and then see 3. below

		// Do the same for MIP
		WritableImage MIPZImage = new WritableImage(256, 256);
		GetZMIP(MIPZImage);
		ImageView MIPZView = new ImageView(MIPZImage);

		//Create the simple GUI
		Slider sliceZSlider = new Slider(0, 255, currZSlice);

		Slider sliceYSlider = new Slider(0, 255, currYSlice);
		
		sliceZSlider.valueProperty().addListener(new ChangeListener<Number>() { 
			public void changed(ObservableValue <? extends Number >  
					observable, Number oldValue, Number newValue) { 

				currZSlice = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
		        GetZSlice(currZSlice, sliceZImage); //go get the slice image
				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            } 
        });

		sliceYSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <? extends Number >
										observable, Number oldValue, Number newValue) {

				currYSlice = newValue.intValue();
				//System.out.println(currZSlice);
				//We update our Image
				GetYSlice(currYSlice, sliceYImage); //go get the slice image
				//Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
			}
		});

		//Add all the GUI elements
		//I'll start a grid for you
		GridPane grid = new GridPane();
        grid.add(sliceZSlider, 0, 0); // Slider at column 0, row 0

		grid.add(sliceYSlider, 1, 0); // Slider at column 0, row 0
        grid.setHgap(10);
        grid.setVgap(10);

        //3. (referring to the 3 things we need to display an image)
      	//we need to add it to the grid
		grid.add(sliceXView, 0, 1); // Slider at column 0, row 1
		grid.add(sliceYView, 1, 1);
		grid.add(MIPZView, 0, 2); // Slider at column 0, row 1
		

		// Create a scene and set the stage
        Scene scene = new Scene(grid, 800, 840);
        stage.setTitle("CT Data Viewer");
        stage.setScene(scene);
        stage.show();
    }
    

	//Function to read in the cthead data set
	public void ReadData() throws IOException {
		//If you've put the test.java in a directory called "src" and put the dataset in the parent directory, then this will be the correct path
		File file = new File("CThead-256cubed.bin");
		//Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find the equivalent in Java)
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		
		int i, j, k; //loop through the 3D data set
		
		min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
		short read; //value read in
		int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around
		
		cthead = new short[256][256][256]; //allocate the memory - note this is fixed for this data set
		grey= new float[256][256][256];
		//loop through the data reading it in
		for (k=0; k<256; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					//because the Endianess is wrong, it needs to be read byte at a time and swapped
					b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
					b2=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
					read=(short)((b2<<8) | b1); //and swizzle the bytes around
					if (read<min) min=read; //update the minimum
					if (read>max) max=read; //update the maximum
					cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
				}
			}
		}
		System.out.println(min+" "+max); //diagnostic - for CThead-256cubed.bin this should be -1897, 3029
		//(i.e. there are 4927 levels of grey, and now we will normalise them to 0-1 for display purposes
		//I know the min and max already, so I could have put the normalisation in the above loop, but I put it separate here
		for (k=0; k<256; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					grey[k][j][i]=((float) cthead[k][j][i]-(float) min)/((float) max-(float) min);
				}
			}
		}
		//At this point, cthead is the original dataset
		//and grey is 0-1 float data that can be displayed by Java
	}

	public void GetZMIP(WritableImage image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();

		//Get an interface to write to that image memory
		PixelWriter image_writer = image.getPixelWriter();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				//Implement MIP here
				
				//But I'll just make a white colour and copy it into the image
				Color color=Color.color(1.0, 1.0, 1.0);
				
				//Apply the new colour
				image_writer.setColor(x, y, color);
			}
		}
	}

	public void GetZSlice(int slice, WritableImage image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
		float val;

		//Get an interface to write to that image memory
		PixelWriter image_writer = image.getPixelWriter();

		//Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				//I'm going to get the middle slice as an example
				val = grey[slice][y][x];

				//Or uncomment this to make a grey image dependent on the slider value so you can see how the GUI updates
				//val = (float) slice / 255.f;

				Color color=Color.color(val, val, val);
				//Apply the new colour
				image_writer.setColor(x, y, color);
			}
		}
	}
	public void GetYSlice(int slice, WritableImage image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		float val;

		//Get an interface to write to that image memory
		PixelWriter image_writer = image.getPixelWriter();

		//Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				//I'm going to get the middle slice as an example
				val = grey[y][slice][x];

				//Or uncomment this to make a grey image dependent on the slider value so you can see how the GUI updates
				//val = (float) slice / 255.f;

				Color color=Color.color(val, val, val);
				//Apply the new colour
				image_writer.setColor(x, y, color);
			}
		}
	}
	

	
    public static void main(String[] args) {
        launch();
    }

}