package com.hughbone.fabrilousupdater.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hughbone.fabrilousupdater.util.FabUtil;

public class CurrentMod {
    public String projectID;
    public String fileDate;
    public String modName;
    public String websiteUrl;
    public String fileName;

    CurrentMod(String hashOrResult, String platform) {
        try {
            if (platform.equals("curseforge")) {
                JsonObject json = JsonParser.parseString(hashOrResult).getAsJsonObject();

                projectID = json.get("data").getAsJsonObject().get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                fileDate = json.get("data").getAsJsonObject().get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("file").getAsJsonObject().get("fileDate").getAsString();
                fileName = json.get("data").getAsJsonObject().get("exactMatches").getAsJsonArray().get(0).getAsJsonObject().get("file").getAsJsonObject().get("fileName").getAsString();
                json = FabUtil.getJsonObject("https://api.curseforge.com/v1/mods/" + projectID);
                System.out.print(fileName);
                modName = json.get("data").getAsJsonObject().get("name").getAsString();
                websiteUrl = json.get("data").getAsJsonObject().get("links").getAsJsonObject().get("websiteUrl").getAsString() + "/files";
            } else if (platform.equals("modrinth")) {
                JsonObject json = FabUtil.getJsonObject("https://api.modrinth.com/v2/version_file/" + hashOrResult + "?algorithm=sha1");
                projectID = json.get("project_id").getAsString();
                fileDate = json.get("date_published").getAsString();
                final JsonArray filesArray = json.getAsJsonArray("files");

                // Get filename
                for (JsonElement j : filesArray) {
                    String tempFile = j.getAsJsonObject().get("filename").getAsString();
                    if (!tempFile.contains("-sources") && !tempFile.contains("-dev")) {  // If multiple files uploaded, get rid of imposter ඞ ones
                        this.fileName = j.getAsJsonObject().get("filename").getAsString();
                        break;
                    }
                }
                json = FabUtil.getJsonObject("https://api.modrinth.com/v2/project/" + projectID);
                modName = json.get("title").getAsString();
                websiteUrl = "https://www.modrinth.com/mod/" + json.get("slug").getAsString() + "/versions";
            }

            // Format Mod Name
            assert modName != null;
            modName = modName.replace("(fabric)", "");
            modName = modName.replace("(Fabric)", "");
            // Remove spaces at the end of the string
            while (Character.toString(modName.charAt(modName.length() - 1)).equals(" ")) {
                modName = modName.substring(0, modName.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
