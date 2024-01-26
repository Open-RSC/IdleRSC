package callbacks;

import bot.Main;
import bot.ocrlib.*;
import controller.Controller;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class SleepCallback {
  static Controller controller = Main.getController();

  private static String guess = "";
  private static String previousSleepWord = "";
  private static int currentFatigue = 100;
  private static int currentRemoteAttempts = 1;
  private static int maxRemoteAttempts = 3;

  private static OCR ocr;
  private static boolean hasSavedImage = false;
  private static boolean isValidUrl;

  private static final Path DICT_TXT = Paths.get("assets/sleep/dictionary.txt");
  private static final Path MODEL_TXT = Paths.get("assets/sleep/model.txt");
  private static final Path HC_BMP = Paths.get("hc.bmp");
  private static String URL = Main.config.getRemoteOcrUrl();

  /**
   * Sleep hook which is called by the patched jar whenever the client goes to sleep.
   *
   * @param packet -- the raw packet data which contains the sleep image
   * @param length -- the length of the raw packet data
   */
  public static void sleepHook(byte[] packet, int length) {
    if (packet[0] == 117) {
      // ! TODO: DO NOT LEAVE THIS ENABLED! TESTING ONLY! WILL BE REMOVED EVENTUALLY!
      boolean IS_TESTING = false;
      if (IS_TESTING) {
        Main.config.setLocalOcr(false);
        String TEMP_URL = "https://idlersc.com/captcha";
        URL = TEMP_URL;
      }

      currentRemoteAttempts = 1;
      hasSavedImage = false;
      saveSleepImage(packet, packet.length);
      isValidUrl = isValidOCRServer(URL);

      if (currentFatigue == 0 && hasSavedImage) handleSleep();
      if (currentFatigue > 0) Main.log("Waiting for fatigue to reach 0...");

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
    if (fatigue > 0) Main.log("Current fatigue in sleep: " + fatigue);
    currentFatigue = fatigue;

    if (fatigue == 0) {
      handleSleep();
    }
  }

  /** Handles the sleep (fatigue) functionality. Tries to interface with OCR or upload captcha */
  private static void handleSleep() {
    if (controller == null) return;

    int fileReadAttempts = 0;
    if (Main.config.isLocalOcr() || !isValidUrl) {
      controller.sleep(1000); // give OCR time to catch up.

      try (final BufferedReader mr = new BufferedReader(new FileReader(MODEL_TXT.toString()));
          final BufferedReader dr = new BufferedReader(new FileReader(DICT_TXT.toString()))) {
        ocr = new OCR(new DictSearch(dr), mr);
      } catch (final IOException | OCRException e) {
        e.printStackTrace();
      }

      while ((guess.equals(previousSleepWord)) && fileReadAttempts < 1) {
        try {
          if (Files.exists(HC_BMP) && controller.isSleeping()) {
            byte[] data = Files.readAllBytes(HC_BMP);
            guess = ocr.guess(SimpleImageIO.readBMP(data), true);
            if (!guess.equals("unknown") && guess.length() > 0) {
              controller.sendSleepWord(guess);
              System.out.println("Local OCR Guess: " + guess);
            }
            previousSleepWord = guess;
          }
        } catch (final IOException ex) {
          ex.printStackTrace();
          guess = "";
        }
        fileReadAttempts++;
      }

      try {
        Files.deleteIfExists(HC_BMP);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      Main.log("Uploading sleep image to remote OCR server: " + Main.config.getRemoteOcrUrl());
      String result = uploadCaptcha(Main.config.getRemoteOcrUrl(), "hc.bmp");
      if (result == null) {
        if (currentRemoteAttempts < maxRemoteAttempts) {
          currentRemoteAttempts++;
          Main.log("Error uploading CAPTCHA!");
          Main.log("Sleeping 10 seconds and trying again...");
          controller.sleep(10000);
        } else {
          Main.log("Too many remote OCR attempts.");
          Main.log("Falling back to local OCR.");
          isValidUrl = false;
        }
        handleSleep();
      }
      if (result != null) {
        Main.log("Remote OCR Guess: " + result);
        controller.sendSleepWord(result);
        controller.sleep(1000);
      }
    }
  }

  /**
   * Uploads a captcha file to the server and returns the result. Error: remote server currently is
   * not active to process Captcha
   *
   * @param captchaFile the path of the captcha file to upload
   * @return the result of the upload operation
   */
  public static String uploadCaptcha(String url, String captchaFile) {
    try {
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
   * Checks that the given url is valid
   *
   * @param url String -- URL to the remote OCR server
   * @return boolean
   */
  private static boolean isValidOCRServer(String url) {
    if (!Main.config.isLocalOcr()) {
      if (url.length() > 0) {
        try {
          new URL(url).toURI();
          return true;
        } catch (MalformedURLException | URISyntaxException e) {
          Main.log("Remote OCR URL: '" + url + "' is not a valid url.");
          Main.log("Falling back to local OCR.");
          return false;
        }
      } else {
        Main.log("No remote OCR URL was set for " + controller.getPlayerName());
        Main.log("Falling back to local OCR.");
        return false;
      }
    }
    return false;
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
      Main.log("\nSaved sleep image");
      hasSavedImage = true;
    } catch (Exception e) {
      System.out.println("Error saving CAPTCHA image!");
      e.printStackTrace();
    }
  }
}
