package com.poojak.aifactory;

public class ChildApkPlan {
    public static String plan(Agent a) {
        return "Child APK build plan for " + a.name + "\n" +
                "1. Inject agent name and role into Android template.\n" +
                "2. Include brain router settings.\n" +
                "3. Build with Gradle/Android SDK on build server.\n" +
                "4. Sign APK with keystore.\n" +
                "5. Output: " + a.name.replace(" ", "_") + ".apk";
    }
}
