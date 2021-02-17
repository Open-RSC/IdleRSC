package listeners;

import controller.Controller;
import orsc.mudclient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.imageio.ImageIO;

import controller.Controller;
import orsc.mudclient;
import bot.Main;

/**
 * SleepListener is a listener which will handle sleep, should the bot or user initiate a sleep sequence.
 * 
 * @author Dvorak
 */
public class SleepListener implements Runnable {

	int count = 43;
	private mudclient mud;
	private Controller controller;
	private String previousSleepWord = "";

	public SleepListener(mudclient _mud, Controller _controller) {
		mud = _mud;
		controller = _controller;
	}

	private BufferedImage convertImageTo1Bpp(BufferedImage o) { //we have to convert the sleep image to 1bpp otherwise FOCR will freak out and cause an 8 hour investigation into why it broke
		BufferedImage img = new BufferedImage(o.getWidth(), o.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		
		for(int y = 0; y < img.getHeight(); y++)
			for(int x = 0; x < img.getWidth(); x++) 
				img.setRGB(x, y, o.getRGB(x, y));
				
		
		return img;
	}
	
	private void saveSleepImage(byte[] data, int length) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data, 1, length);
			BufferedImage fullColorImg = ImageIO.read(in);
			BufferedImage img = convertImageTo1Bpp(fullColorImg);
			ImageIO.write(img, "bmp", new File("hc.bmp"));
		} catch (Exception e) {
			System.out.println("Error saving CAPTCHA image!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				byte[] packet = mud.packetHandler.getPacketsIncoming().dataBuffer;

				if (packet[0] == 117) {
					Main.log("got sleep packet!");
					saveSleepImage(packet, packet.length);
					System.out.println("image saved");

					Main.log("Waiting for fatigue to reach 0...");
					while (controller.getFatigueDuringSleep() != 0) controller.sleep(10);


					try {
						String guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));

						while (guess.equals(previousSleepWord)) {
							Main.log("Sleep word has not updated... is OCR running?");
							guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));
							Thread.sleep(1000);
						}

						Main.log("guess: " + guess);
						controller.chatMessage(guess);
						previousSleepWord = guess;
					} catch (IOException e) {
						Main.log("error reading slword.txt! Ensure sleeper has access to write slword.txt and correct directory is set.");
						e.printStackTrace();
					}
				}

				Thread.sleep(10);
				//TODO: convert from polling to callback based sleeping
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}