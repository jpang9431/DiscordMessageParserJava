import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;

public class App {
    private static final String[] charactersToRemove = { ",", ".", "\"", "?", "!", "*", "(", ")", "<", ">" };
    private static final HashMap<String, String> sepcialSequencesToReplace = new HashMap<>();
    private static final String sepcialCharacterReplaceFilePath = "";
    private static final String outputFilePath = "src\\output.txt";
    private static final String inputDiscordTextFilePath = "";


    public static void setMaps() {
        try {
            updateHashMap(sepcialCharacterReplaceFilePath,
                    sepcialSequencesToReplace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateHashMap(String path, HashMap<String, String> map) {
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split(",");
                map.put(parts[0], parts[1]);
                // System.out.println(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Word implements Comparable<Word> {
        Integer count = 1;
        String word = "";

        Word(String word) {
            this.word = word;
        }

        @Override
        public int compareTo(Word obj) {
            return count.compareTo(obj.count);
        }

        @Override
        public String toString() {
            return "<" + word + "|" + count + ">";
        }
    }

    public static void updateWordMap(HashMap<String, Word> wordMap, ArrayList<Word> wordsList, String word) {
        if (!wordMap.containsKey(word)) {
            Word tempWord = new Word(word);
            wordMap.put(word, tempWord);
            wordsList.add(tempWord);
        }
        wordMap.get(word).count++;
    }

    private static void updateWord(String word, HashMap<String, Word> wordMap, ArrayList<Word> wordsList) {
        if (sepcialSequencesToReplace.containsKey(word)) {
            word = sepcialSequencesToReplace.get(word);
        } else if (word.contains("https://")) {
            word = "https://";
        }
        for (String character : charactersToRemove) {
            if (word.contains(character)) {
                word = word.replace(character, "");
                if (character.equals("<") || character.equals(">")) {
                    if (!word.contains("@")) {
                        updateWordMap(wordMap, wordsList, character);
                    }
                } else {
                    updateWordMap(wordMap, wordsList, character);
                }
            }
        }
        updateWordMap(wordMap, wordsList, word);
    }

    public static void updateMapAndArray(HashMap<String, Word> wordMap, ArrayList<Word> wordsList, String line) {
        String[] words = line.split(" ");
        for (String word : words) {
            updateWord(word, wordMap, wordsList);
        }
    }

    public static void countWordsForAll() {
        try {
            File file = new File(inputDiscordTextFilePath);
            Scanner reader = new Scanner(file);
            HashMap<String, Word> wordMap = new HashMap<>();
            ArrayList<Word> wordList = new ArrayList<>();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                int index = line.indexOf(",");
                updateMapAndArray(wordMap, wordList, line.substring(index + 1));
            }
            reader.close();
            wordList.sort(Comparator.reverseOrder());
            FileWriter output = new FileWriter(outputFilePath);
            output.write(wordList.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void countWords() {
        try {
            File file = new File("src\\discordMessages.txt");
            Scanner reader = new Scanner(file);
            HashMap<String, HashMap<String, Word>> userWordMap = new HashMap<>();
            HashMap<String, ArrayList<Word>> userWordList = new HashMap<>();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                int index = line.indexOf(",");
                String name = line.substring(0, index);
                if (!userWordMap.containsKey(name)) {
                    userWordMap.put(name, new HashMap<>());
                    userWordList.put(name, new ArrayList<>());
                }
                updateMapAndArray(userWordMap.get(name), userWordList.get(name), line.substring(index + 1));
            }
            FileWriter output = new FileWriter(outputFilePath);
            for (Map.Entry<String, ArrayList<Word>> entry : userWordList.entrySet()) {
                entry.getValue().sort(Comparator.reverseOrder());
                String line = entry.getKey() + " | " + entry.getValue().toString();
                output.write(line);
            }
            output.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void graphPings() {

    }

    public static void main(String[] args) throws Exception {
        setMaps();
        //countWords();
        //countWordsForAll();
    }
}
