package digitalinnovation.one;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ClientHttpExample {

    static ExecutorService executor = Executors.newFixedThreadPool(6, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            System.out.println("New thread created: " + (thread.isDaemon() ? "Daemon" : "") + "Thread Group: " + thread.getThreadGroup());
            return thread;
        }
    });

    public static void main(String[] args) throws IOException, InterruptedException {
        connectHttp2Client();

    }

    private static void connectHttp2Client() {
        System.out.println("Running HTTP/2 example ...");
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .proxy(ProxySelector.getDefault())
                    .build();

            long start = System.currentTimeMillis();

            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://http2.akamai.com/demo/h2_demo_frame.html"))
                    .build();

            HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response Headers: " + response.headers());
            String responseBody = response.body();
            System.out.println("Response Body: " + responseBody);

            List<Future<?>> future = new ArrayList<>();

            responseBody
                    .lines()
                    .filter(line -> line.trim().startsWith("<img height"))
                    .map(line -> line.substring(line.indexOf("src='") + 5, line.indexOf("'/>")))
                    .forEach(image -> {
                        Future<?> imgFuture = executor.submit(() -> {
                            HttpRequest imgRequest = HttpRequest.newBuilder()
                                    .uri(URI.create("https://http1.akamai.com"+image))
                                    .build();

                            try {
                                HttpResponse<String> imageResponse = httpClient.send(imgRequest, HttpResponse.BodyHandlers.ofString());
                                System.out.println("Image loaded: " + image + ", status code: " + imageResponse.statusCode());
                            } catch (IOException | InterruptedException e) {
                                System.out.println("Error message during request to recover image " + image);
                            }
                        });
                        future.add(imgFuture);
                        System.out.println("Future images submitted to image: " + image);
                    });

            future.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            long end = System.currentTimeMillis();
            System.out.println("Total time loading: " + (end - start) + " ms");

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public static void connectAndPrintURL() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET().uri(URI.create("https://ricky.com.br"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response Headers: " + response.headers());
        System.out.println("Response Body" + response.body());
    }
}
