import okhttp3.*;

import java.io.IOException;

public class OkHttpDemo {
    private static String url = "http://localhost:8801/test";
    public static void main(String[] args) {
        final OkHttpClient client = new OkHttpClient();

        //create a request
        final Request request = new Request.Builder().get().url(url).build();

        //fire a AsyncCall request
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed requesting" + e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

}
