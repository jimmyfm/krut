package tests;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import krut.Run_KRUT;

public class WavReader
{
/*Play a *.wav or *.au file
@param args args[0] on command line is name of file to play
*/


public static void main(String[] args)
{
	String soundName = "Blip.wav";

	readWav(soundName);

}

public static void readWav(String soundName) {
	try
{


InputStream resourceAsStream = Run_KRUT.class.getResourceAsStream(soundName);
AudioInputStream ais = AudioSystem.getAudioInputStream(resourceAsStream);
//AudioInputStream a;


 int frameSample;
 int timeofFrame;
 int N;
 int runTimes;
 int bps;
 int channels;
 double times;
 int bufSize;
 int frameSize;
 int frameRate;
 long length; 

length = resourceAsStream.available();

AudioFormat af = ais.getFormat();
DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);

if (!AudioSystem.isLineSupported(info))
{
System.out.println("unsupported line");
System.exit(0);
}

frameRate = (int)af.getFrameRate();
System.out.println("Frame Rate: " + frameRate);

frameSize = af.getFrameSize();
System.out.println("Frame Size: " + frameSize);

bufSize = frameRate * frameSize / 10;
System.out.println("Buffer Size: " + bufSize);

channels = af.getChannels();
System.out.println("Channels : " + channels);

bps = af.getSampleSizeInBits();
System.out.println("Bits per sample : " + bps);

times = (double)(length / (frameRate * channels * bps / 8));
System.out.println("Duration of the songs : " + times +" seconds");

timeofFrame = 20; //20ms
frameSample = (timeofFrame * frameRate) / 1000;
N = frameSample;
runTimes = (int) (times * 1000) / 20;

int[][] freq = new int[runTimes][N];

BufferedInputStream bis = null;
DataInputStream dis = null;


// Here BufferedInputStream is added for fast reading.
bis = new BufferedInputStream(resourceAsStream);
dis = new DataInputStream(bis);

// dis.available() returns 0 if the file does not have more lines.
while (dis.available() != 0) {

// this statement reads the line from the file and print it to the console.
for (int i = 0; i < 1; i++)
for(int j=0;j<N;j++)
{
freq[i][j] = (int)dis.readByte();

}
}
System.out.println(freq[0][0]);

// dispose all the resources after using them.
resourceAsStream.close();
bis.close();
dis.close();

}
catch (FileNotFoundException e)
{
e.printStackTrace();
}


catch (Exception e)
{
}
}
}