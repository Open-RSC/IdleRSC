package callbacks;

import bot.Main;
import controller.Controller;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import orsc.mudclient;

public class SleepCallback {
  private static String previousSleepWord = "";
  private static int currentFatigue = 100;

  /**
   * Sleep hook which is called by the patched jar whenever the client goes to sleep.
   *
   * @param packet -- the raw packet data which contains the sleep image
   * @param length -- the length of the raw packet data
   */
  public static void sleepHook(byte[] packet, int length) {
    if (packet[0] == 117) {
      saveSleepImage(packet, packet.length);

      Main.log("HC.bmp saved.");
      if (currentFatigue == 0) handleSleep();
      // Main.log("Waiting for fatigue to reach 0...");

    } else {
      Main.log("Packet received was not a legitimate sleep image!");
    }
  }

  /**
   * Fatigue hook which is called by the patched jar whenever the client receives an update on
   * fatigue.
   *
   * @param fatigue -- the current fatigue
   */
  public static void fatigueHook(int fatigue) {
    Main.log("Current fatigue in sleep: " + fatigue);
    currentFatigue = fatigue;

    if (fatigue == 0) {
      handleSleep();
    }
  }
  /** Handles the sleep (fatigue) functionality. Tries to interface with OCR or upload captcha */
  private static void handleSleep() {
    Controller controller = Main.getController();
    mudclient mud = null;

    if (controller == null) return;

    mud = controller.getMud();

    if (Main.config.isLocalOcr()) {
      Main.log("Local OCR specified...");
      controller.sleep(1000); // give OCR time to catch up.

      try {
        String guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));
        int fileReadAttempts = 0;
        while (guess.equals(previousSleepWord) && fileReadAttempts < 5) {
          Main.log("Sleep word has not updated... is OCR running?");
          guess = new String(Files.readAllBytes(new File("./slword.txt").toPath()));
          controller.sleep(1000);
          fileReadAttempts++;
        }

        if (fileReadAttempts == 10) {
          Main.log("OCR is not running or not functioning properly!");
          return;
        }
        Main.log("guess: " + guess);
        // controller.chatMessage(guess);
        controller.sendSleepWord(guess);
        previousSleepWord = guess;
      } catch (IOException e) {
        Main.log(
            "error reading slword.txt! Ensure sleeper has access to write slword.txt and correct directory is set.");
        e.printStackTrace();
      }
    } else {
      Main.log("Uploading CAPTCHA...");
      String result = uploadCaptcha("hc.bmp");
      if (result == null) {
        Main.log("Error uploading CAPTCHA!");
        Main.log("Sleeping 10 seconds and trying again...");
        controller.sleep(10000);
        handleSleep();
      }
      Main.log("guess: " + result);
      // controller.chatMessage(result);
      controller.sendSleepWord(result);
      controller.sleep(1000);
    }
  }
  /**
   * Uploads a captcha file to the server and returns the result. Error: remote server currently is
   * not active to process Captcha
   *
   * @param captchaFile the path of the captcha file to upload
   * @return the result of the upload operation
   */
  public static String uploadCaptcha(String captchaFile) {

    try {
      String url = "http://idlersc.com:8080/captcha";
      String charset = "UTF-8";
      String param = "fileupload";
      File binaryFile = new File(captchaFile);
      String boundary =
          Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
      String CRLF = "\r\n"; // Line separator required by multipart/form-data.

      URLConnection connection = new URL(url).openConnection();
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

      try (OutputStream output = connection.getOutputStream();
          PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true)) {
        // Send normal param.
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
        writer.append(CRLF).append(param).append(CRLF).flush();

        // Send binary file.
        writer.append("--" + boundary).append(CRLF);
        writer
            .append(
                "Content-Disposition: form-data; name=\"fileupload\"; filename=\""
                    + binaryFile.getName()
                    + "\"")
            .append(CRLF);
        writer
            .append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName()))
            .append(CRLF);
        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(binaryFile.toPath(), output);
        output.flush(); // Important before continuing with writer!
        writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

        // End of multipart/form-data.
        writer.append("--" + boundary + "--").append(CRLF).flush();
      }

      // Request is lazily fired whenever you need to obtain information about response.
      int responseCode = ((HttpURLConnection) connection).getResponseCode();
      if (responseCode == 200) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result;

        result = in.readLine();

        return result;
      }

      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  /**
   * Converts the given image to 1-bit per pixel (1bpp) format.
   *
   * @param o the image to be converted
   * @return the converted image in 1bpp format
   */
  private static BufferedImage convertImageTo1Bpp(
      BufferedImage
          o) { // we have to convert the sleep image to 1bpp otherwise FOCR will freak out and cause
    // an 8 hour investigation into why it broke
    BufferedImage img =
        new BufferedImage(o.getWidth(), o.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    for (int y = 0; y < img.getHeight(); y++)
      for (int x = 0; x < img.getWidth(); x++) img.setRGB(x, y, o.getRGB(x, y));

    return img;
  }
  /**
   * Saves a sleep image.
   *
   * @param data the byte array containing the image data
   * @param length the length of the image data
   */
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
