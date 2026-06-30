package com.poojak.aifactory;

import android.content.Context;
import android.content.SharedPreferences;

public class BrainSettings {
    private final SharedPreferences sp;
    public BrainSettings(Context c) { sp = c.getSharedPreferences("brain", Context.MODE_PRIVATE); }
    public String provider() { return sp.getString("provider", "Demo"); }
    public String apiKey() { return sp.getString("apiKey", ""); }
    public String endpoint() { return sp.getString("endpoint", "http://127.0.0.1:11434"); }
    public void save(String p, String key, String ep) { sp.edit().putString("provider", p).putString("apiKey", key).putString("endpoint", ep).apply(); }
}
