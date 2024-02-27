package callbacks;

import bot.Main;
import bot.ocrlib.*;
import controller.Controller;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;

public class SleepCallback {
  static Controller controller = Main.getController();

  private static final Path PATH_SLEEP = Paths.get("assets/sleep");
  private static final String DICT_TXT = PATH_SLEEP.resolve("dictionary.txt").toString();
  private static final String MODEL_TXT = PATH_SLEEP.resolve("model.txt").toString();
  private static final String HASHES = PATH_SLEEP.resolve("hashes.properties").toString();

  private static final String HC_NAME = "hc.bmp";
  private static final String HC_BMP = Paths.get(HC_NAME).toString();
  private static final String SLWORD_TXT = Paths.get("slword.txt").toString();

  private static final int FNV1_32_INIT = 0x811c9dc5;
  private static final int FNV1_PRIME_32 = 16777619;

  private static String sleepWord;

  private static OCRType ocrType;

  private static Properties hashes;

  private static OCR ocr;
  private static URL sleepServer;

  private static File hc;
  private static File slword;

  private static long lastModified;
  private static boolean checkLastModified;

  /**
   * Sleep hook which is called by the patched jar whenever the client goes to sleep.
   *
   * @param packet -- the raw packet data which contains the sleep image
   * @param length -- the length of the raw packet data
   */
  public static void sleepHook(byte[] packet, int length) {
    if (packet[0] == 117) {
      onSleepWord(packet, length);
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

    onSleepFatigueUpdate(fatigue);
  }

  private static void onSleepWord(final byte[] data, final int length) {
    switch (ocrType) {
      case REMOTE:
        try {
          Main.log("Uploading CAPTCHA...");
          sleepWord = uploadCaptcha(data, length);
          if (controller.getFatigue() == 0) onSleepFatigueUpdate(0);

        } catch (final Exception ex) {
          Main.log("Error uploading CAPTCHA!");
          sleepWord = "unknown";
          ex.printStackTrace();
        }
        break;
      case INTERNAL:
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(4096)) {
          saveBitmap(out, data, length);
          sleepWord = ocr.guess(SimpleImageIO.readBMP(out.toByteArray()), true);
          if (controller.getFatigue() == 0) onSleepFatigueUpdate(0);
        } catch (final IOException ex) {
          Main.log("Error solving sleep word!");
          sleepWord = "unknown";
          ex.printStackTrace();
        }
        break;
      case HASH:
        byte[] image = Arrays.copyOfRange(data, 1, length);
        int hash = hash32(image);
        sleepWord = hashes.getProperty(Integer.toString(hash));
        if (sleepWord == null) {
          sleepWord = "unknown";
          Main.log("Could not find hash: " + hash);
        }
        if (controller.getFatigue() == 0) onSleepFatigueUpdate(0);
        break;
      case MANUAL:
      default:
        break;
    }
  }

  // case EXTERNAL:
  //   try (final FileOutputStream out = new FileOutputStream(hc)) {
  //     Main.log("Saving sleep image...");
  //     saveBitmap(out, data, length);
  //     checkLastModified = true;
  //   } catch (final IOException ex) {
  //     ex.printStackTrace();
  //     sleepWord = null;
  //   }
  //   break;

  private static void onSleepFatigueUpdate(final int fatigue) {
    if (sleepWord == null) return;

    if (fatigue == 0) sendSleepWord();
  }

  private static void sendSleepWord() {
    controller.sendSleepWord(sleepWord);
    Main.log("Sent CAPTCHA: " + sleepWord);
    sleepWord = null;
  }

  public static void onGameTick() {
    if (!checkLastModified) return;
    final long modifiedTime = slword.lastModified();
    if (lastModified == modifiedTime) return;
    lastModified = modifiedTime;
    checkLastModified = false;
    sleepWord = readLine(slword);
    onSleepFatigueUpdate(controller.getFatigue());
  }

  public static void setOCRType(final OCRType type) {
    ocrType = type;
    // force hashes for coleslaw to prevent unnecessary SVM memory usage
    // todo: Investigate fixing SVM Heap size issue directly. Null the array after using?
    if (Main.config != null
        && Main.config.getInitCache().equalsIgnoreCase("coleslaw")
        && type.equals(OCRType.INTERNAL)) ocrType = OCRType.HASH;

    Main.log("Setting up " + ocrType.getName() + " OCR.");

    switch (ocrType) {
      case REMOTE:
        String url = Main.config.getOCRServer();
        if (url.isEmpty()) {
          Main.log("No remote OCR URL was set for " + controller.getPlayerName());
          Main.log("Falling back to image hashes.");
        }
        try {
          sleepServer = new URL(url);
          sleepServer.toURI();
          break;
        } catch (MalformedURLException | URISyntaxException e) {
          sleepServer = null;
          Main.log("Remote OCR URL: '" + url + "' is not a valid url.");
          Main.log("Falling back to Image hashes.");
        }
      case HASH:
        try (FileInputStream fs = new FileInputStream(HASHES)) {
          hashes = new Properties();
          hashes.load(fs);
          break;
        } catch (final IOException e) {
          e.printStackTrace();
          Main.log("Issue detected with Image Hashes.");
          Main.log("Falling back to internal.");
        }
      case INTERNAL:
        try (final BufferedReader mr = new BufferedReader(new FileReader(MODEL_TXT));
            final BufferedReader dr = new BufferedReader(new FileReader(DICT_TXT))) {
          ocr = new OCR(new DictSearch(dr), mr);
          break;
        } catch (final IOException | OCRException e) {
          e.printStackTrace();
          Main.log("Issue detected with Internal Sleeper Num3l.");
          Main.log("Falling back to manual.");
        }
        // case EXTERNAL:
        //   hc = new File(HC_BMP);
        //   slword = new File(SLWORD_TXT);
        //   lastModified = slword.lastModified();
        //   break;
      case MANUAL:
      default:
        break;
    }
  }

  /**
   * FNV1a 32 bit variant.
   *
   * @param data - input byte array
   * @return - hashcode
   */
  private static int hash32(byte[] data) {
    return hash32(data, data.length);
  }

  /**
   * FNV1a 32 bit variant.
   *
   * @param data - input byte array
   * @param length - length of array
   * @return - hashcode
   */
  private static int hash32(byte[] data, int length) {
    int hash = FNV1_32_INIT;
    for (int i = 0; i < length; i++) {
      hash ^= (data[i] & 0xff);
      hash *= FNV1_PRIME_32;
    }

    return hash;
  }

  /**
   * Uploads a captcha file to the server and returns the result. Error: remote server currently is
   * not active to process Captcha
   *
   * @param data -- the raw packet data which contains the sleep image
   * @param length -- the length of the raw packet data
   * @return the result of the upload operation
   */
  private static String uploadCaptcha(final byte[] data, final int length) throws Exception {
    String charset = "UTF-8";
    String param = "fileupload";
    // File binaryFile = new File(captchaFile);
    String boundary =
        Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
    String CRLF = "\r\n"; // Line separator required by multipart/form-data.

    URLConnection connection = sleepServer.openConnection();
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
              "Content-Disposition: form-data; name=\"fileupload\"; filename=\"" + HC_NAME + "\"")
          .append(CRLF);
      writer
          .append("Content-Type: " + URLConnection.guessContentTypeFromName(HC_NAME))
          .append(CRLF);
      writer.append("Content-Transfer-Encoding: binary").append(CRLF);
      writer.append(CRLF).flush();
      saveBitmap(output, data, length);
      // Files.copy(binaryFile.toPath(), output);
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

  private static String readLine(final File file) {
    try {
      return new String(Files.readAllBytes(file.toPath()));
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private static void saveBitmap(final OutputStream out, final byte[] data, int length)
      throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(data, 1, length);
    BufferedImage fullColorImg = ImageIO.read(in);
    BufferedImage img = convertImageTo1Bpp(fullColorImg);
    ImageIO.write(img, "bmp", out);
  }
}
