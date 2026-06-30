package com.poojak.aifactory;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class BrainRouter {
    public interface Callback { void done(String text); }
    private final OkHttpClient client = new OkHttpClient();
    private final Handler main = new Handler(Looper.getMainLooper());

    public void ask(Agent agent, BrainSettings settings, String userMessage, Callback cb) {
        String provider = settings.provider();
        if (provider.equals("OpenAI")) openAI(agent, settings.apiKey(), userMessage, cb);
        else if (provider.equals("Gemini")) gemini(agent, settings.apiKey(), userMessage, cb);
        else if (provider.equals("Ollama") || provider.equals("LM Studio") || provider.equals("Custom")) local(agent, settings.endpoint(), userMessage, cb);
        else demo(agent, userMessage, cb);
    }

    private void demo(Agent a, String msg, Callback cb) {
        String s = msg.toLowerCase();
        String out;
        if (s.contains("website")) out = "Website plan: Home, Services, Pricing, Contact, WhatsApp button, Instagram links, and lead form. I can create page copy and section layout next.";
        else if (s.contains("caption")) out = "Caption idea: Make your brand look premium, trusted and unforgettable. DM now for details.";
        else out = "Here is a direct answer as " + a.name + ": " + msg;
        cb.done(out);
    }

    private void openAI(Agent a, String key, String msg, Callback cb) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4.1-mini");
            body.put("input", a.systemPrompt + "\nUser: " + msg);
            Request req = new Request.Builder().url("https://api.openai.com/v1/responses")
                    .addHeader("Authorization", "Bearer " + key).addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json"))).build();
            run(req, cb);
        } catch(Exception e){ cb.done("OpenAI error: " + e.getMessage()); }
    }

    private void gemini(Agent a, String key, String msg, Callback cb) {
        try {
            JSONObject part = new JSONObject().put("text", a.systemPrompt + "\nUser: " + msg);
            JSONObject content = new JSONObject().put("parts", new JSONArray().put(part));
            JSONObject body = new JSONObject().put("contents", new JSONArray().put(content));
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + key;
            Request req = new Request.Builder().url(url).addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json"))).build();
            run(req, cb);
        } catch(Exception e){ cb.done("Gemini error: " + e.getMessage()); }
    }

    private void local(Agent a, String endpoint, String msg, Callback cb) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", "llama3.2");
            body.put("prompt", a.systemPrompt + "\nUser: " + msg);
            body.put("stream", false);
            String url = endpoint.endsWith("/api/generate") ? endpoint : endpoint.replaceAll("/$", "") + "/api/generate";
            Request req = new Request.Builder().url(url).addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json"))).build();
            run(req, cb);
        } catch(Exception e){ cb.done("Local brain error: " + e.getMessage()); }
    }

    private void run(Request req, Callback cb) {
        client.newCall(req).enqueue(new Callback2(cb));
    }
    private class Callback2 implements okhttp3.Callback {
        private final Callback cb;
        Callback2(Callback cb){this.cb=cb;}
        public void onFailure(Call call, IOException e){ main.post(() -> cb.done("Brain connection failed: " + e.getMessage())); }
        public void onResponse(Call call, Response r) throws IOException {
            String raw = r.body()==null ? "" : r.body().string();
            String out = parseText(raw);
            main.post(() -> cb.done(out));
        }
    }
    private String parseText(String raw) {
        try {
            JSONObject j = new JSONObject(raw);
            if (j.has("output_text")) return j.getString("output_text");
            if (j.has("response")) return j.getString("response");
            if (j.has("candidates")) return j.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
        } catch(Exception ignored) {}
        return raw.length() > 1500 ? raw.substring(0,1500) : raw;
    }
}
