package com.poojak.aifactory;

public class IntentRouter {
    static boolean isMakeAgent(String msg) {
        String s = msg.toLowerCase();
        return s.contains("make an ai") || s.contains("ai bana") || s.contains("ai bna") || s.contains("create ai") || s.contains("agent bana") || s.contains("agent bna");
    }
    static Agent createAgentFromText(String msg, String provider) {
        String s = msg.toLowerCase();
        if (s.contains("marketing") || s.contains("business") || s.contains("caption") || s.contains("sales")) {
            return new Agent("Business Helper AI", "Digital marketing, captions, ads, customer replies, posters, pricing and sales strategy.", provider);
        }
        if (s.contains("study") || s.contains("exam") || s.contains("notes")) {
            return new Agent("Study Helper AI", "Notes, quizzes, summaries, exam revision and doubt solving.", provider);
        }
        if (s.contains("apk") || s.contains("android") || s.contains("app")) {
            return new Agent("APK Maker AI", "Android app ideas, screens, code structure, features and APK build planning.", provider);
        }
        return new Agent("Custom Helper AI", "Helps the user with custom tasks based on their request: " + msg, provider);
    }
}
