import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by trungle.bka@gmail.com <br/>
 * User: trung le    <br/>
 * Date: 2019/11/13    <br/>
 * Time: 8:17 AM    <br/>
 */
public class WsAsrClient extends WebSocketListener {
  private static final int NORMAL_CLOSURE_STATUS = 1000;
  private OkHttpClient client = new OkHttpClient();
  WebSocket webSocket;
  private boolean userCloseTriggered = false;

  int sampleRate, sampleSize, channels;
  String token;
  WsAsrListener listener;

  public WsAsrClient(int sampleRate, int sampleSize, int noOfChannel, String token, WsAsrListener listener) {
    this.sampleRate = sampleRate;
    this.sampleSize = sampleSize;
    this.channels = noOfChannel;
    this.token = token;
    this.listener = listener;
  }

  private String buildRequestURL() {
    return "https://viettelgroup.ai/voice/api/asr/v1/rest/decode_file?content-type=audio/x-raw,+layout=(string)interleaved,+rate=(int)"
            + this.sampleRate + ",+format=(string)S" + this.sampleSize + "LE,+channels=(int)1&token=" + this.token;
  }

  public void sendBytes(byte[] audioBytes) {
    webSocket.send(ByteString.of(audioBytes));
  }

  public void connect() {
    Request request = new Request.Builder().url(buildRequestURL()).build();
    webSocket = client.newWebSocket(request, listener);
    client.dispatcher().executorService().shutdown();
  }

  public void shutdown() {
    if (webSocket != null) {
      webSocket.send(ByteString.of("EOS".getBytes()));
      webSocket.close(NORMAL_CLOSURE_STATUS, "User shutdown");
    }

  }

  public static void main(String[] args) throws IOException, InterruptedException {
    int sampleRate = 16000;
    int sampleSize = 16;
    int channels = 1;
    String token = "RMySK-t2ugQlhRpz3dx018Jnj9GQnrtRU2UX4qZDbV1IW5rKVMZW9uJ3-SM-JS-r";
    WsAsrListener listener = new WsAsrListener();
    WsAsrClient asrClient = new WsAsrClient(sampleRate, sampleSize, channels, token, listener);
    asrClient.connect();
    try (BufferedInputStream bi = new BufferedInputStream(Files.newInputStream(Paths.get("C:/Users/Dell/Documents/HH Project/xctcbc.wav")))) {
      byte[] buff = new byte[8000];
      while ((bi.read(buff) != -1)) {
        asrClient.sendBytes(buff);
        Thread.sleep(230);
      }
    }
  }
}
