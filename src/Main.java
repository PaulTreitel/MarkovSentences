
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class Main {
    
    final private static int LINELEN = 12;
    
    public static void main(String[] args) {
        //String filename = "small.txt";
        //String filename = "caesar.txt";
        String filename = "kolhakavod.txt";
        for (int i = 0; i < 1; i++)
            System.out.println(markov(filename, 2, 45));
        //System.out.println(markov2(filename, 3, 10));
    }
    
    public static String markov(String filename, int keySize, int outputSize) {
        String[] words = getWords(filename);
        // Map is interface class of Key:Value pairs
        // HashMap is an implementation of Map
        Map<String, List<String>> dict = new HashMap<>();
        createDictionary(words, keySize, dict);
        return generateSentence(dict, keySize, outputSize);
    }
    
    private static void createDictionary(String[] words, int keySize, Map<String, List<String>> dict) {
        for (int i = 0; i < words.length-keySize; i++) {
            String key = "";
            for (int j = 0; j < keySize; j++)
                key = (key +" "+ words[i+j]).trim();
            String value = words[i+keySize];
            
            if (dict.containsKey(key)) {
                dict.get(key).add(value);
            } else {
                List<String> list = new ArrayList<>();
                list.add(value);
                dict.put(key, list);
            }
        }
    }
    
    private static String generateSentence(Map<String, List<String>> dict, 
                                           int keySize, int outputSize) {
        String out = "Kol Hakavod";
        Random r = new Random();
        int rn = r.nextInt(dict.size());
        String keyAsString = (String) dict.keySet().toArray()[rn];
        String[] keyAsArray = keyAsString.split(" ");
        String[] names = getNames();
        
        //printDict(dict);
        
        for (int outLength = 1; outLength < outputSize;) {
            if (!dict.containsKey(keyAsString)) {
                System.out.println("Key not Found:"+ keyAsString);
                return out.trim();
            }
            List<String> nextWords = dict.get(keyAsString);
            String nextWord;
            if (nextWords.size() == 1) {
                nextWord = nextWords.get(0);
                if (nextWord.equals(""))
                    return out.trim();
            } else {
                rn = r.nextInt(nextWords.size());
                nextWord = nextWords.get(rn);
            }
            if (outLength % LINELEN != 0)
                out += " ";
            if (nextWord.equals("Kol"))
                return out.trim();
            else if (!nextWord.equals("NAME"))
                out += nextWord;
            else
                out += getRandomName(names);
            outLength++;
            
            if (outLength % LINELEN == 0)
                out += "\n";
            if (outLength > outputSize)
                return out.trim();
            keyAsString = newKey(keySize, keyAsArray, nextWord);
            keyAsArray = keyAsString.split(" ");
           
        }
        return out.trim();
    }
    
    private static void printDict(Map<String, List<String>> dict) {
        for (String key: dict.keySet()) {
            System.out.println("Key = "+ key +"\n===");
            for (String val: dict.get(key))
                System.out.print(val);
            System.out.println();
        }
    }
    
    private static String getRandomName(String[] names) {
        int idx = (int) (names.length*Math.random());
        return names[idx];
    }
    
    private static String[] getNames() {
        String[] names = {};
        try {
            FileReader f = new FileReader("names.txt");
            BufferedReader b = new BufferedReader(f);
            String line;
            while ((line = b.readLine()) != null) {
                names = add(names, line.trim().split("\\W+"));
            }
        } catch (IOException e) {
            System.out.println("Fatal error: file not found");
        }
        return names;   
    }
    
    private static String newKey(int keySize, String[] keyAsArray, String nextWord) {
        String keyAsString = "";
        for (int i = 1; i < keySize; i++)
            keyAsString += " "+keyAsArray[i];
        keyAsString += " "+nextWord;
        return keyAsString.trim();
    }
    
    private static String[] add(String[] base, String[] add) {
        String[] newArr = new String[base.length+add.length];
        System.arraycopy(base, 0, newArr, 0, base.length);
        System.arraycopy(add, 0, newArr, base.length, add.length);
        return newArr;
    }
    
    private static String[] getWords(String filename) {
        String[] words = {};
        try {
            FileReader f = new FileReader(filename);
            BufferedReader b = new BufferedReader(f);
            String line;
            while ((line = b.readLine()) != null) {
                words = add(words, line.trim().split("\\W+"));
            }
        } catch (IOException e) {
            System.out.println("Fatal error: file not found");
        }
        return words;
    }
    
    public static String markov2(String filename, int keySize, int outputSize) {
        String[] words = getWords(filename);
        Map<String, List<String>> dict = new HashMap<>();
        
        for (int i = 0; i < words.length-keySize; i++) {
            String key = "";
            for (int j = i; j > i - keySize; j--) {
                if (j >= 0)
                    key = words[j] +" "+ key;
                else
                    key = "* " + key;
            }
            String value = words[i+1];
            //ToDo Rewrite the rest of this to use new key system
            if (dict.containsKey(key)) {
                dict.get(key).add(value);
            } else {
                List<String> list = new ArrayList<>();
                list.add(value);
                dict.put(key, list);
            }
        }
        return generateSentence(dict, keySize, outputSize);
    }
}
