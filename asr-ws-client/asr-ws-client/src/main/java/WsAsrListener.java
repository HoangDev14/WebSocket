import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.io.EOFException;

/**
 * Created by trungle.bka@gmail.com <br/>
 * User: trung le    <br/>
 * Date: 2019/11/13    <br/>
 * Time: 9:34 AM    <br/>
 */
public class WsAsrListener extends WebSocketListener {
  boolean singleUtterance;
  private static final int NORMAL_CLOSURE_STATUS = 1000;

  public WsAsrListener() {
    this.singleUtterance=true;
  }

  public WsAsrListener(boolean singleUtterance) {
    this.singleUtterance = singleUtterance;
  }

  @Override
  public void onOpen(WebSocket webSocket, Response response) {

  }

  @Override
  public void onMessage(WebSocket webSocket, String text) {
    if (singleUtterance) {
      if (text.contains("\"final\": true"))
        webSocket.send(ByteString.encodeUtf8("EOS"));
    }
    //your code here
    System.out.println("Receiving : " + text);
  }

  @Override
  public void onClosing(WebSocket webSocket, int code, String reason) {
    webSocket.close(NORMAL_CLOSURE_STATUS, null);
    System.out.println("Closing : " + code + " / " + reason);
  }

  @Override
  public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    if (t instanceof EOFException){
      System.out.println("Connection was be closed by server");
    }
    System.out.println("Error : " + t.getMessage());
    t.printStackTrace();
  }
}
