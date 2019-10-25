package chatty.gui.components;

import chatty.HTTPURLConnector;

import chatty.gui.MainGui;
import chatty.util.settings.Settings;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static chatty.gui.GuiUtil.makeGbc;

public class BotControlDialog extends JDialog {

    private final JTextField api_key_FLD = new JTextField();
    private final JTextField workspace_FLD = new JTextField();

    private final JButton restart_botButton = new JButton("BOT");
    private final JButton restart_queueButton = new JButton("QUEUE");
    private final JButton restart_giveawayButton = new JButton("GIVEAWAY");
    private final JButton restart_analyticsButton = new JButton("ANALYTICS");
    private final JButton restart_coinsButton = new JButton("COINS");

    private final JButton save_to_settings = new JButton("Save keys");

    private MainGui maingui;

    String workspace = "";
    String apikey = "";

    public BotControlDialog(final MainGui owner) {
        super(owner);
        maingui = owner;
        String username = owner.client.getUsername();

        if (username == null || username.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "Bitte verbinde dich mit einem Twitch Channel um deine Identität zu bestätigen!", "Unbekannter Account", JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            return;
        }
        if (!username.trim().equalsIgnoreCase("recklessGreed")) {
            JOptionPane.showMessageDialog(null, "Diese Funktion ist aktuell nur von einem Techniker des Mod4You Teams nutzbar.", "Unbekannter Account", JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            return;
        }


        setTitle("Mod4You Bot Control");
        setResizable(false);
        setModal(true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc;

        Settings settings = owner.getSettings();
        if (!settings.getSettingNames().contains("m4y_botcontrol_workspace")) {
            JOptionPane.showMessageDialog(null, "1", "1", 1);
            settings.addString("m4y_botcontrol_workspace", "");}
        if (!settings.getSettingNames().contains("m4y_botcontrol_apikey")) {settings.addString("m4y_botcontrol_apikey", "");}

        workspace = settings.getString("m4y_botcontrol_workspace");
        apikey = settings.getString("m4y_botcontrol_apikey");
        workspace_FLD.setText(workspace);
        api_key_FLD.setText(apikey);


        gbc = makeGbc(0,0,1,1);
        add(new JLabel("Workspace: "), gbc);

        gbc = makeGbc(1,0,3,1);
        workspace_FLD.setPreferredSize(new Dimension(300,20));
        add(workspace_FLD, gbc);

        gbc = makeGbc(0,1,1,1);
        add(new JLabel("API Key: "), gbc);

        gbc = makeGbc(1,1,3,1);
        api_key_FLD.setPreferredSize(new Dimension(300,20));
        add(api_key_FLD, gbc);

        gbc = makeGbc(0,2,1,1);
        restart_botButton.setPreferredSize(new Dimension(100,20));
        add(restart_botButton, gbc);

        gbc = makeGbc(1,2,1,1);
        restart_queueButton.setPreferredSize(new Dimension(100,20));
        add(restart_queueButton, gbc);

        gbc = makeGbc(2,2,1,1);
        restart_giveawayButton.setPreferredSize(new Dimension(100,20));
        add(restart_giveawayButton, gbc);

        gbc = makeGbc(3,2,1,1);
        restart_coinsButton.setPreferredSize(new Dimension(100,20));
        add(restart_coinsButton, gbc);

        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workspace = workspace_FLD.getText();
                apikey = api_key_FLD.getText();
                if (e.getSource() == restart_analyticsButton) {
                    botRestartMethod(workspace, apikey, "analytics");
                    setVisible(false);
                } else if (e.getSource() == restart_botButton) {
                    botRestartMethod(workspace, apikey, "bot");
                    setVisible(false);
                }
                else if (e.getSource() == restart_coinsButton) {
                    botRestartMethod(workspace, apikey, "coins");
                    setVisible(false);
                }
                else if (e.getSource() == restart_giveawayButton) {
                    botRestartMethod(workspace, apikey, "giveaway");
                    setVisible(false);
                }
                else if (e.getSource() == restart_queueButton) {
                    botRestartMethod(workspace, apikey, "queue");
                    setVisible(false);
                }
            }

        };

        restart_analyticsButton.addActionListener(listener);
        restart_botButton.addActionListener(listener);
        restart_coinsButton.addActionListener(listener);
        restart_giveawayButton.addActionListener(listener);
        restart_queueButton.addActionListener(listener);



        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        pack();
    }

    private boolean botRestartMethod(String workspace, String apikey, String name) {
        String[] pods = getAllPods(workspace, apikey);
        maingui.getSettings().setString("m4y_botcontrol_workspace" , workspace);
        maingui.getSettings().setString("m4y_botcontrol_apikey" , apikey);
        maingui.getSettings().saveSettingsToJson();
        if (pods == null) {
            return false;
        }
        int podnumber = 0;
        switch (name) {
            case "queue":
                podnumber = 0;

                break;
            case "bot":
                podnumber = 1;

                break;
            case "giveaway":
                podnumber = 2;

                break;
            case "coins":
                podnumber = 3;

            default:
                break;
        }
        try {

            String response =
                    new HTTPURLConnector().send("DELETE",
                            "https://kube01.m4u.tech/v3/project/" + workspace + "/pods/" + pods[podnumber],
                            "Authorization", "Bearer " + apikey);
            if (response == null)
            {
                JOptionPane.showMessageDialog(null, "Result ist null", "BCD: Result", 0);
            }
            else if (response.equalsIgnoreCase("error")) {
                //Toast.makeText(MainActivity.this, "Es ist ein Fehler aufgetreten\nBitte Einstellungen prüfen!", Toast.LENGTH_LONG).show();
            }

            return true;
        } catch (Exception e) {return false;}
    }

    private String[] getAllPods(String workspace, String apiKey) {
        String[] returner = new String[4];
        try {
            String response = new HTTPURLConnector().send("GET", "https://kube01.m4u.tech/v3/project/" + workspace + "/pods/", "Authorization", "Bearer " + apiKey);
            //Log.d("getAllPods","DEBUG: " +  response);
            if (response == null || response.equalsIgnoreCase("error"))
            {
                //Toast.makeText(MainActivity.this, "VPN Aktiviert?", Toast.LENGTH_LONG).show();
                return null;
            }
            JSONParser parser = new JSONParser();
            JSONObject outer = (JSONObject) parser.parse(response);
            JSONArray podList = (JSONArray) outer.get("data");
            for(int i = 0; i < podList.size(); i++) {
                String pod_id = (String)((JSONObject)podList.get(i)).get("id");
                //Log.d("getAllPods", "DEBUG: " + pod_id);
                if (pod_id.startsWith("bot:bot-queue")) {
                    returner[0] = pod_id;
                }
                else if (pod_id.startsWith("bot:bot")) {
                    returner[1] = pod_id;
                }
                else if (pod_id.startsWith("bot:giveaway-processor")) {
                    returner[2] = pod_id;
                }
                else if (pod_id.startsWith("bot:coins")) {
                    returner[3] = pod_id;
                }

            }
        }
        //catch (InterruptedException irex) {/*Log.e("ERROR", "DEBUG: " + irex.getLocalizedMessage());*/}
        //catch (ExecutionException exex) {/*Log.e("ERROR", "DEBUG: " + exex.getLocalizedMessage());*/}
        catch (ParseException e) {
            e.printStackTrace();
        }


        return returner;
    }
}
