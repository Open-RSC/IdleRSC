package listeners;

import controller.Controller;
import orsc.mudclient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import controller.Controller;
import orsc.mudclient;
import bot.Main;

public class SleepListener implements Runnable {

	private mudclient mud;
	private Controller controller;

	public SleepListener(mudclient _mud, Controller _controller) {
		mud = _mud;
		controller = _controller;
	}

	private void saveSleepImage(byte[] data, int length) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data, 1, length);
			BufferedImage img = ImageIO.read(in);
			ImageIO.write(img, "bmp", new File("hc.bmp"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			//Long previousHash = null;

			byte[] packet = mud.packetHandler.getPacketsIncoming().dataBuffer; //does this crash the client?
			//crc.update(x);
			int sleepDataLength = packet.length - 1;

			if(packet[0] == 117) {
//				Main.log("got sleep packet! " + crc.getValue());
				byte[] sleepData = Arrays.copyOfRange(packet, 1, sleepDataLength);
				Main.log("got sleep packet!");
				saveSleepImage(packet, packet.length);
				System.out.println("image saved");

//				String result = LeoSleep.getImageString();
//
//				while(controller.getFatigueDuringSleep() != 0) {
//					System.out.println(controller.getFatigueDuringSleep());
//					try {
//						Thread.sleep(10);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				controller.chatMessage(result);


			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

}