package com.poojak.aifactory;

import android.app.*;import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    LinearLayout messages, root; EditText input; Agent active; BrainSettings settings; BrainRouter brain;
    ArrayList<Agent> library = new ArrayList<>();
    int purple = Color.rgb(124,58,237);
    public void onCreate(Bundle b){ super.onCreate(b); settings=new BrainSettings(this); brain=new BrainRouter(); active=new Agent("Poojak Main AI","Helpful chatbot that answers directly and can create custom AI agents.",settings.provider()); library.add(active); buildUI(); bot("Poojak AI Factory ready. Ask anything, or say: make an AI for digital marketing."); }
    TextView tv(String t,int sp,int c){ TextView v=new TextView(this); v.setText(t); v.setTextSize(sp); v.setTextColor(c); v.setPadding(18,14,18,14); return v; }
    Button btn(String t){ Button b=new Button(this); b.setText(t); b.setAllCaps(false); return b; }
    void buildUI(){ root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(250,249,246)); setContentView(root);
        LinearLayout top=new LinearLayout(this); top.setPadding(12,18,12,8); top.setGravity(Gravity.CENTER_VERTICAL); top.setOrientation(LinearLayout.HORIZONTAL); root.addView(top,new LinearLayout.LayoutParams(-1,-2));
        TextView title=tv("⚡ Poojak AI Factory",22,Color.BLACK); top.addView(title,new LinearLayout.LayoutParams(0,-2,1)); Button settingsBtn=btn("Brain"); settingsBtn.setOnClickListener(v->showSettings()); top.addView(settingsBtn);
        ScrollView sv=new ScrollView(this); messages=new LinearLayout(this); messages.setOrientation(LinearLayout.VERTICAL); messages.setPadding(16,8,16,8); sv.addView(messages); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout bottom=new LinearLayout(this); bottom.setPadding(10,8,10,16); bottom.setGravity(Gravity.CENTER_VERTICAL); root.addView(bottom,new LinearLayout.LayoutParams(-1,-2));
        input=new EditText(this); input.setHint("Ask anything or create AI..."); input.setSingleLine(false); bottom.addView(input,new LinearLayout.LayoutParams(0,-2,1)); Button send=btn("↑"); send.setTextColor(Color.WHITE); send.setBackgroundColor(purple); send.setOnClickListener(v->send()); bottom.addView(send);
    }
    void add(String who,String text,int color){ TextView m=tv(who+": "+text,16,color); messages.addView(m,new LinearLayout.LayoutParams(-1,-2)); }
    void user(String t){ add("You",t,Color.BLACK); } void bot(String t){ add("AI",t,Color.rgb(25,25,25)); }
    void send(){ String msg=input.getText().toString().trim(); if(msg.isEmpty())return; input.setText(""); user(msg);
        if(IntentRouter.isMakeAgent(msg)){ active=IntentRouter.createAgentFromText(msg, settings.provider()); library.add(active); bot("✅ " + active.name + " created. Role: " + active.role + "\nSay a task now, or type: generate apk"); return; }
        if(msg.toLowerCase().contains("generate apk")){ bot(ChildApkPlan.plan(active)); return; }
        brain.ask(active, settings, msg, this::bot);
    }
    void showSettings(){ final String[] providers={"Demo","OpenAI","Gemini","Ollama","LM Studio","Custom"}; LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(40,20,40,0); Spinner sp=new Spinner(this); sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, providers)); box.addView(sp); EditText key=new EditText(this); key.setHint("API key (OpenAI/Gemini only)"); key.setText(settings.apiKey()); box.addView(key); EditText ep=new EditText(this); ep.setHint("Endpoint, e.g. http://192.168.1.7:11434"); ep.setText(settings.endpoint()); box.addView(ep); new AlertDialog.Builder(this).setTitle("Brain Settings").setView(box).setPositiveButton("Save",(d,w)->{settings.save(providers[sp.getSelectedItemPosition()], key.getText().toString(), ep.getText().toString()); active.provider=settings.provider(); bot("Brain set to " + settings.provider());}).setNegativeButton("Cancel",null).show(); }
}
