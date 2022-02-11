package model;
import java.util.ArrayList;
import java.util.Random;

class Language {
	private static final ArrayList<Character> consonants=new ArrayList<>();
	private static final ArrayList<Character> vowels=new ArrayList<>();
	private final String name;
	private final Random r;
	
	static {
		for(char c:new char[] {'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z'}) {
			consonants.add(c);
		}
		for(char v:new char[] {'a','e','i','o','u'}) {
			vowels.add(v);
		}
	}
	Language(Random r){
		this.r=r;
		name=generateWord();
	}
	
	String generateWord() {
		String word="";
		word+=getConsonant();
		word+=getVowel();
		word+=getConsonant();
		return word;// finish later
	}
	
	// word types
	static String asEmpire(String w) {
		return w+" Empire";
	}
	static String asKingdom(String w) {
		return "Kingdom of "+asCountry(w);
	}
	static String asCountry(String w) {
		if(w.charAt(w.length()-1)=='a') {
			return w;
		}
		return w+(isVowel(w.charAt(w.length()-1))?"land":"ia");
	}
	static String asRace(String w) {
		String c=asCountry(w);
		return c+(c.charAt(c.length()-1)=='a'?"n":"er");
	}
	
	// getters
	public String getName() {return name;}
	
	// vowels and consonants
	private static boolean isVowel(char c) {
		return vowels.contains(c);
	}
	private char getVowel() {
		return vowels.get(r.nextInt(vowels.size()));
	}
	private char getConsonant() {
		return consonants.get(r.nextInt(consonants.size()));
	}
}
