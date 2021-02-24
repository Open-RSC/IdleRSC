package callbacks;

import bot.Main;
import controller.Controller;
import orsc.buffers.RSBuffer_Bits;
import orsc.mudclient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SleepCallback {
    private static String previousSleepWord = "";

    /**
     * Sleep hook which is called by the patched jar whenever the client goes to sleep.
     *
     * @param packet -- the raw packet data which contains the sleep image
     * @param length -- the length of the raw packet data
     */
    public static void sleepHook(byte[] packet, int length) {
        Controller controller = Main.getController();
        mudclient mud = null;


        if(controller == null)
            return;

        mud = controller.getMud();


        try {
            if (packet[0] == 117) {
                saveSleepImage(packet, packet.length);

                Main.log("HC.bmp saved.");
                Main.log("Waiting for fatigue to reach 0...");
                while (controller.getFatigueDuringSleep() != 0) controller.sleep(10);


                try {
                    String guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));
                    int fileReadAttempts = 0;
                    while (guess.equals(previousSleepWord) && fileReadAttempts < 5) {
                        Main.log("Sleep word has not updated... is OCR running?");
                        guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));
                        Thread.sleep(1000);
                        fileReadAttempts++;
                    }

                    if(fileReadAttempts == 10) {
                        Main.log("OCR is not running or not functioning properly!");
                        return;
                    }

                    Main.log("guess: " + guess);
                    controller.chatMessage(guess);
                    previousSleepWord = guess;
                } catch (IOException e) {
                    Main.log("error reading slword.txt! Ensure sleeper has access to write slword.txt and correct directory is set.");
                    e.printStackTrace();
                }
            } else {
                Main.log("Packet received was not a legitimate sleep image!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage convertImageTo1Bpp(BufferedImage o) { //we have to convert the sleep image to 1bpp otherwise FOCR will freak out and cause an 8 hour investigation into why it broke
        BufferedImage img = new BufferedImage(o.getWidth(), o.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        for(int y = 0; y < img.getHeight(); y++)
            for(int x = 0; x < img.getWidth(); x++)
                img.setRGB(x, y, o.getRGB(x, y));


        return img;
    }

    private static void saveSleepImage(byte[] data, int length) {
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
}
