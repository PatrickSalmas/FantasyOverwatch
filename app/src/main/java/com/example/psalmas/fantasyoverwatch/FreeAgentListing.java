package com.example.psalmas.fantasyoverwatch;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Updates verified player stats from overbuff.com
public class FreeAgentListing extends Activity {
    static ArrayList<String> playerLinks = new ArrayList<String>();   //Web links of each player from Overbuff
    String statsFile = "statsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        new getPlayerLinks().execute();
        new getInfo().execute();

    }

    //Retrieves all the weblinks of all Verified players from
    //https://www.overbuff.com/verified?mode=competitive
    public class getPlayerLinks extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String TD_DATA_VALUE = "<td data-value=";
            String PREFIX = "https://www.overbuff.com/players/pc/";
            String SUFFIX = "/heroes?mode=competitive";
            ArrayList<String> exceptions404 = new ArrayList<String>(Arrays.asList("Kiki-OLD"));
            try {
                Document documentOverbuff =
                        Jsoup.connect("https://www.overbuff.com/verified?mode=competitive").get();
                Pattern namePattern = Pattern.compile("(<td data-value=)(\")([a-zA-Z])([^ ]+)(>)");
                Matcher nameMatcher = namePattern.matcher(documentOverbuff.outerHtml());
                while(nameMatcher.find()) {
                    String playerName = nameMatcher.group().substring(nameMatcher.group().indexOf(TD_DATA_VALUE) + TD_DATA_VALUE.length()+1,
                            nameMatcher.group().indexOf("\">"));
                    if (exceptions404.contains(playerName))
                        continue;

                    playerLinks.add(PREFIX + playerName + SUFFIX);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    //Accesses each player's webpage (https://www.overbuff.com/verified?mode=competitive)
    //and retrieves their username and win record
    public class getInfo extends AsyncTask<Void,Void,Void> {
        ArrayList<String> offenseChars = new ArrayList<String>(Arrays.asList("Doomfist","Genji","McCree","Pharah",
                "Reaper","Soldier:","Sombra","Tracer"));
        ArrayList<String> tankChars = new ArrayList<String>(Arrays.asList("D.Va","Orisa","Reinhardt","Roadhog","Winston",
                "Zarya"));
        ArrayList<String> supportChars = new ArrayList<String>(Arrays.asList("Ana","Lúcio","Mercy","Symmetra","Zenyatta"));
        ArrayList<String> defenseChars = new ArrayList<String>(Arrays.asList("Bastion","Hanzo","Junkrat","Mei","Torbjörn",
                "Widowmaker"));


        @Override
        protected Void doInBackground(Void... params) {
            String textHeroes;
            String heroName, gamesPlayedStr;
            heroName = gamesPlayedStr = null;
            String SCORE = "Score";   //Deliminator constant for matching
            String PC = "/pc/";       //Deliminator for retrieving player name from link
            int linkCount = 0;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = openFileOutput(statsFile, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedWriter writeToStats = new BufferedWriter(new OutputStreamWriter(fileOutputStream));


            while (linkCount < playerLinks.size()) {

                try {
                    Document documentHeroes = Jsoup.connect(playerLinks.get(linkCount)).get();
                    String playerName = playerLinks.get(linkCount).substring(playerLinks.get(linkCount).indexOf(PC)+PC.length(),
                            playerLinks.get(linkCount).indexOf("/heroes"));
                    textHeroes = documentHeroes.text();
                    Pattern topHeroPattern = Pattern.compile("[A-Z]([a-zCúö0-9:. ]+)(day|days|hours) ([0-9]+)");
                    Matcher topHeroMatcher = topHeroPattern.matcher(textHeroes);

                    Pattern noDataPattern = Pattern.compile("/gFinal Medals /g Gold /g Silver /g Bronze /g Cards © 2017 Elo Entertainment");
                    Matcher noDataMatcher = noDataPattern.matcher(textHeroes);

                    boolean hasData=false;
                    if(!noDataMatcher.find()) {
                        hasData=true;
                    }

                    float totalGames, offenseGames, defenseGames, supportGames, tankGames;
                    totalGames = offenseGames = defenseGames = supportGames = tankGames = 0;

                    while (topHeroMatcher.find() && hasData) {
                        Pattern removePattern = Pattern.compile("[0-9]+ (day|days|hours) ");
                        String heroData = topHeroMatcher.group();
                        Matcher removeMatcher = removePattern.matcher(heroData);
                        String removeSubstr = null;
                        if (removeMatcher.find()) {
                            removeSubstr = removeMatcher.group();
                        }
                        heroName = heroData.substring(0, heroData.indexOf(removeSubstr)).trim();
                        gamesPlayedStr = heroData.substring(heroData.indexOf(removeSubstr) + removeSubstr.length()).trim();
                        if (offenseChars.contains(heroName) || defenseChars.contains(heroName) || tankChars.contains(heroName) || supportChars.contains(heroName)) {
                            if (offenseChars.contains(heroName)) {
                                offenseGames += Integer.parseInt(gamesPlayedStr);
                                totalGames += Integer.parseInt(gamesPlayedStr);
                            } else if (defenseChars.contains(heroName)) {
                                defenseGames += Integer.parseInt(gamesPlayedStr);
                                totalGames += Integer.parseInt(gamesPlayedStr);
                            } else if (supportChars.contains(heroName)) {
                                supportGames += Integer.parseInt(gamesPlayedStr);
                                totalGames += Integer.parseInt(gamesPlayedStr);
                            } else if (tankChars.contains(heroName)) {
                                tankGames += Integer.parseInt(gamesPlayedStr);
                                totalGames += Integer.parseInt(gamesPlayedStr);
                            }
                        }
                    }

                    String role = null;
                    if (hasData==false) {
                        role = "---";
                    } else if (offenseGames / totalGames >= .67) {
                        role = "offense";
                    } else if (defenseGames / totalGames >= .67) {
                        role = "defense";
                    } else if (supportGames / totalGames >= .67) {
                        role = "support";
                    } else if (tankGames / totalGames >= .67) {
                        role = "tank";
                    } else {
                        role = "flex";
                    }

                    String statLine = playerName + " " + role;
                    writeToStats.write(statLine);
                    writeToStats.newLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                linkCount++;
            }
            try {
                writeToStats.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                FileInputStream fileInputStream = openFileInput(statsFile);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.i("statLine", "" + line);
                }
                bufferedReader.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
