package ethan.gateway.outbound.okhttp;

import ethan.gateway.router.MyRandomHttpRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {
    private List<String> backendServers;

     public OkhttpOutboundHandler(List<String> backendServers) {

         this.backendServers = backendServers;
     }

     public void handle(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {

         String url = new MyRandomHttpRouter().route(backendServers) + httpRequest.uri();
         final OkHttpClient client = new OkHttpClient();

         //create a request
         Request.Builder requestBuilder = new Request.Builder();

         //copy Headers
         Iterator<Map.Entry<String, String>> entryIterator = httpRequest.headers().iteratorAsString();
         while(entryIterator.hasNext()) {
             Map.Entry<String, String> next = entryIterator.next();
             requestBuilder.addHeader(next.getKey(), next.getValue());
         }

         final Request request = requestBuilder.url(url).build();

         //fire a AsyncCall request
         client.newCall(request).enqueue(new Callback() {
             public void onFailure(Call call, IOException e) {
                 System.out.println("Failed requesting" + e.getMessage());
             }

             public void onResponse(Call call, Response response) throws IOException {
                 //System.out.println(response.body().string());
                 processResponse(httpRequest, ctx, response);
             }
         });

     }

     public void processResponse(FullHttpRequest httpRequest, ChannelHandlerContext ctx, Response response) {
         FullHttpResponse fullHttpResponse = null;
         try {
             byte[] body = response.body().bytes();
             fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
             fullHttpResponse.headers().set("Content-Type", "application/json");
             fullHttpResponse.headers().setInt("Content-Length", Integer.parseInt(response.header("Content-Length")));
         } catch (IOException e) {
             e.printStackTrace();
             fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
             exceptionCaught(ctx, e);
         } finally {
             if (httpRequest != null) {
                 if (!HttpUtil.isKeepAlive(httpRequest)) {
                     ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                 } else {
                     ctx.write(fullHttpResponse);
                 }
             }

             ctx.flush();
             //close okhttp connection
             response.close();
         }

     }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }



}
