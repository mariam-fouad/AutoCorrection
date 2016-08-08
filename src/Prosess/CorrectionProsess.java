package Prosess;

import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import ConfMatrix.matrix;
import Corpus.ArabicCorpus;
import Corpus.EnglishCorpus;
import POS.posTagger;


public class CorrectionProsess {
	ArrayList<SimpleType> bestFreqWords = new ArrayList<>();
	HashMap <String , ArrayList<SimpleType>> arabicSegmentTag = new HashMap <String , ArrayList<SimpleType>> ();

	public ArrayList <SimpleType> englishTagger (String sent)
	{
		ArrayList <SimpleType> tagedSent = new ArrayList<>();
		MaxentTagger tagger = new MaxentTagger(
				"english-bidirectional-distsim.tagger");
		String sepret []=tagger.tagString(sent).split("\\s+");
		for (int j=0;j<sepret.length;j++)
		{
			SimpleType s = new SimpleType();
			String wordTag []=sepret[j].split("_");
			if (wordTag.length==2)
			{
		    s.word=wordTag[0] ;
			s.Tag=wordTag[1];
			}
			tagedSent.add(s);
		   
		}
		
		return tagedSent;
		
	}
	public ArrayList <SimpleType> arabicTagger (String sent)
	{
		ArrayList <SimpleType> tagedSent = new ArrayList<>();
		MaxentTagger tagger = new MaxentTagger(
				"arabic-accurate.tagger");
		String sentSegment = ArabicSegment(sent);
		String sepret []=tagger.tagString(sentSegment).split("\\s+");
		for (int j=0;j<sepret.length;j++)
		{
			SimpleType s = new SimpleType();
			String wordTag []=sepret[j].split("/");
			if (wordTag.length==2)
			{
		    s.word=wordTag[0] ;
			s.Tag=wordTag[1];
			}
			tagedSent.add(s);
		   
		}
		String [] originalWord = sent.split(" ");
		for (int i=0;i<originalWord.length;i++)
		{
			String segmentWord = ArabicSegment(originalWord[i]);
			ArrayList <SimpleType> s = new ArrayList<>();
			String sepSeg [] = segmentWord.split(" ");
			for (int j=0;j<sepSeg.length;j++)
			{
				
				for (int k=0;k<tagedSent.size();k++)
				{
					if (tagedSent.get(k).word.equals(sepSeg[j]))
						s.add(tagedSent.get(k));
				}
			}
			arabicSegmentTag.put(originalWord[i], s);
		}
		return tagedSent;
		
	}
	public int maxNumberDiffrent(String w) {

		if (w.length() <= 3)
			return 1;
		else if (w.length() == 4)
			return 2;
		return w.length() - 4;
	}

	public int max(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	public int min(int x, int y, int z) {
		return Math.min(Math.min(x, y), z);
	}

	public int calcMinWordDist(String word1, String word2) {
		word2= englishNormalizer(word2);
		int dp[][] = new int[word1.length() + 1][word2.length() + 1];
		dp[0][0] = 0;
		for (int i = 0; i <= word1.length(); i++) {
			for (int j = 0; j <= word2.length(); j++) {
				StringBuilder s = new StringBuilder();

				if (i == 0 && j != 0) {
					dp[i][j] = j;
					s.append("#");
					s.append(word2.charAt(j - 1));
				}

				else if (j == 0 && i != 0) {
					s.append("#");
					s.append(word1.charAt(i - 1));
					dp[i][j] = i;
				}

				else if ((i != 0 || j != 0) && word1.charAt(i - 1) == word2.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1];

				else if (i != 0 && j != 0) {
					s.append(word1.charAt(i - 1));
					s.append(word2.charAt(j - 1));
					String key = s.toString();
					int ins = dp[i][j - 1];
					// if (key.contains("—") || matrix.insMatrix.get(key) == 0)
					ins++;
					int del = dp[i - 1][j];
					// if (key.contains("—") || matrix.delMatrix.get(key) == 0)
					del++;
					int rep = dp[i - 1][j - 1];
					// if (key.contains("—") || matrix.subMatrix.get(key) == 0)
					rep++;

					dp[i][j] = min(ins, del, rep);
				}

			}
		}
		return dp[word1.length()][word2.length()];
	}
	public int calcMinWordDistArabic(String word1, String word2) {
		word2=arabicNormalizer(word2);
		int dp[][] = new int[word1.length() + 1][word2.length() + 1];
		dp[0][0] = 0;
		for (int i = 0; i <= word1.length(); i++) {
			for (int j = 0; j <= word2.length(); j++) {
				StringBuilder s = new StringBuilder();

				if (i == 0 && j != 0) {
					dp[i][j] = j;
					s.append("#");
					s.append(word2.charAt(j - 1));
				}

				else if (j == 0 && i != 0) {
					s.append("#");
					s.append(word1.charAt(i - 1));
					dp[i][j] = i;
				}

				else if ((i != 0 || j != 0) && (word1.charAt(i - 1) == word2.charAt(j - 1)))
					dp[i][j] = dp[i - 1][j - 1];

				else if (i != 0 && j != 0) {
					s.append(word1.charAt(i - 1));
					s.append(word2.charAt(j - 1));
					String key = s.toString();
					int ins = dp[i][j - 1];
					// if (key.contains("—") || matrix.insMatrix.get(key) == 0)
					ins++;
					int del = dp[i - 1][j];
					// if (key.contains("—") || matrix.delMatrix.get(key) == 0)
					del++;
					int rep = dp[i - 1][j - 1];
					// if (key.contains("—") || matrix.subMatrix.get(key) == 0)
					rep++;

					dp[i][j] = min(ins, del, rep);
				}

			}
		}
		return dp[word1.length()][word2.length()];
	}

	public String finalBestEnglishWord(String word, String prevWord, int Gram , String tag) {
		List<SimpleType> BestWords = mostClosestEnglishWords(word);
		ArrayList<SimpleType> bestReturnWords = (mostFreqWordsGramEnglish(BestWords, prevWord, Gram , tag));
		bestFreqWords.addAll(bestReturnWords);
		return bestReturnWords.get(0).word;
	}
	
	public String ArabicSegment (String word)
	{
		Properties prop = new Properties();
		prop.put("inputEncoding","UTF-8");
		ArabicSegmenter s = new ArabicSegmenter(prop);
		s.loadSegmenter( "arabic-segmenter-atbtrain.ser.gz");
		String seg = s.segmentString(word);
		return seg;
		
	}
	public String finalBestArabicWord(String word, String prevWord, int Gram ) {
		List<SimpleType> BestWords = mostClosestArabicWords(word);
		ArrayList<SimpleType> bestReturnWords = (mostFreqWordsGramArabic(BestWords, prevWord, Gram , word));
		bestFreqWords.addAll(bestReturnWords);
		return bestReturnWords.get(0).word;
	}

	public void updateArrabic (String lineWords [])
	{
		String sente = new String();
		for (int j=0;j<lineWords.length;j++)
		{
			sente+=lineWords[j];
		}
		arabicTagger(sente);
	}
	public String[] englishLoop(String[] lineWords, ArrayList<SimpleType> taggedSent) {

		
		for (int i = 0; i < lineWords.length; i++) {
			
			if (lineWords.length > (i + 3)) {
				// check first if the 4 words within the corpus
				if (!check4GramEnglish(
						lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " " + lineWords[i + 3])) {
					// now check 3Gram if it is exist so the muistake is in the
					// last word
					if (check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
						// now get the most closest 50 words
						lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
								lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);
					} else if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
						// check 2 Gram if exist get the best conlination
						// between the second
						lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							// if not find the best last word
							lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
									lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);

						}
					} else if (check1GramEnglish(lineWords[i])) {
						// check if only the first word in Dic
						lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);
						if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							// now check 3Gram if it is exist so the muistake is
							// in the
							// last word
							if (check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
								// now get the most closest 50 words
								lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
										lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);
							} else if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
								// check 2 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// if not find the best last word
									lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
											lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);

								}
							}

						}

					} else {
						lineWords[i] = finalBestEnglishWord(lineWords[i], "", 1,taggedSent.get(i).Tag);
						if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							if (check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
								// now get the most closest 50 words
								lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
										lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);
							} else if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
								// check 2 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// if not find the best last word
									lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
											lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);

								}
							} else if (check1GramEnglish(lineWords[i])) {
								// check if only the first word in Dic
								lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);
								if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// now check 3Gram if it is exist so the
									// muistake is in the
									// last word
									if (check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
										// now get the most closest 50 words
										lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
												lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);
									} else if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
										// check 2 Gram if exist get the best
										// conlination
										// between the second
										lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2],
												lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
										// now check if the last word is already
										// in the grams no
										// need for further analysis
										if (!check4GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2]
												+ " " + lineWords[i + 3])) {
											// if not find the best last word
											lineWords[i + 3] = finalBestEnglishWord(lineWords[i + 3],
													lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4,taggedSent.get(i).Tag);

										}
									}

								}

							}
						}
					}
				}
			} else if (lineWords.length > (i + 2)) {

				// if there is not enogh words to have phares of 4 words
				if (!check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
					// now check 2Gram if it is exist so the muistake is in the
					// last word
					if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
						lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
					} else if (check1GramEnglish(lineWords[i])) {
						// check 1 Gram if exist get the best conlination
						// between the second
						lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
							// if not find the best last word
							lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1],
									3,taggedSent.get(i).Tag);

						}
					} else {
						// check if only the first word in Dic
						lineWords[i] = finalBestEnglishWord(lineWords[i], "", 1,taggedSent.get(i).Tag);
						if (!check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
							// now check 2Gram if it is exist so the muistake is
							// in the
							// last word
							if (check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
								// now get the most closest 50 words
								lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);
							} else {
								// check 1 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check3GramEnglish(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
									// if not find the best last word
									lineWords[i + 2] = finalBestEnglishWord(lineWords[i + 2],
											lineWords[i] + " " + lineWords[i + 1], 3,taggedSent.get(i).Tag);

								}
							}

						}

					}

				}

			} else if (lineWords.length > (i + 1)) {
				// if there is not enogh words to have phares of 4 words
				if (!check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
					// now check 2Gram if it is exist so the muistake is in the
					// last word
					if (check1GramEnglish(lineWords[i])) {
						lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);
					} else {
						// check 1 Gram if exist get the best conlination
						// between the second
						lineWords[i] = finalBestEnglishWord(lineWords[i], "", 1,taggedSent.get(i).Tag);
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check2GramEnglish(lineWords[i] + " " + lineWords[i + 1])) {
							// if not find the best last word
							lineWords[i + 1] = finalBestEnglishWord(lineWords[i + 1], lineWords[i], 2,taggedSent.get(i).Tag);

						}
					}
				}
			}
			else
			{
				if (!check1GramEnglish(lineWords[i]))
				{
					lineWords[i]=finalBestEnglishWord(lineWords[i], "", 1,taggedSent.get(i).Tag);
				}
			}

		}
		return lineWords;
	}
	
	public String[] arabicLoop(String[] lineWords ,  ArrayList<SimpleType> taggedSent) {

		for (int i = 0; i < lineWords.length; i++) {
			if (lineWords.length > (i + 3)) {
				// check first if the 4 words within the corpus
				if (!check4GramArabic(
						lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " " + lineWords[i + 3])) {
					// now check 3Gram if it is exist so the muistake is in the
					// last word
					if (check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
						// now get the most closest 50 words
						lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
								lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
						updateArrabic(lineWords);
					} else if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
						// check 2 Gram if exist get the best conlination
						// between the second
						lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1], 3);
						updateArrabic(lineWords);
		
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							// if not find the best last word
							lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
									lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
							updateArrabic(lineWords);
						}
					} else if (check1GramArabic(lineWords[i])) {
						// check if only the first word in Dic
						lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
						updateArrabic(lineWords);
						if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							// now check 3Gram if it is exist so the muistake is
							// in the
							// last word
							if (check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
								// now get the most closest 50 words
								lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
										lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
							updateArrabic(lineWords);
							} else if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
								// check 2 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3);
								updateArrabic(lineWords);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// if not find the best last word
									lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
											lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
									updateArrabic(lineWords);
								}
							}

						}

					} else {
						lineWords[i] = finalBestArabicWord(lineWords[i], "", 1);
						updateArrabic(lineWords);
						if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
								+ lineWords[i + 3])) {
							if (check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
								// now get the most closest 50 words
								lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
										lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
							updateArrabic(lineWords);
							} else if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
								// check 2 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3);
								updateArrabic(lineWords);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// if not find the best last word
									lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
											lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
									updateArrabic(lineWords);
								}
							} else if (check1GramArabic(lineWords[i])) {
								// check if only the first word in Dic
								lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
								updateArrabic(lineWords);
								if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2] + " "
										+ lineWords[i + 3])) {
									// now check 3Gram if it is exist so the
									// muistake is in the
									// last word
									if (check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
										// now get the most closest 50 words
										lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
												lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
									updateArrabic(lineWords);
									} else if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
										// check 2 Gram if exist get the best
										// conlination
										// between the second
										lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2],
												lineWords[i] + " " + lineWords[i + 1], 3);
										updateArrabic(lineWords);
										// now check if the last word is already
										// in the grams no
										// need for further analysis
										if (!check4GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2]
												+ " " + lineWords[i + 3])) {
											// if not find the best last word
											lineWords[i + 3] = finalBestArabicWord(lineWords[i + 3],
													lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2], 4);
											updateArrabic(lineWords);
										}
									}

								}

							}
						}
					}
				}
				
			} 
			else if (lineWords.length > (i + 2)) {

				// if there is not enogh words to have phares of 4 words
				if (!check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
					// now check 2Gram if it is exist so the muistake is in the
					// last word
					if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
						lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1], 3);
					updateArrabic(lineWords);
					} else if (check1GramArabic(lineWords[i])) {
						// check 1 Gram if exist get the best conlination
						// between the second
						lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
						updateArrabic(lineWords);
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
							// if not find the best last word
							lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2], lineWords[i] + " " + lineWords[i + 1],
									3);
							updateArrabic(lineWords);

						}
					} else {
						// check if only the first word in Dic
						lineWords[i] = finalBestArabicWord(lineWords[i], "", 1);
						updateArrabic(lineWords);
						if (!check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
							// now check 2Gram if it is exist so the muistake is
							// in the
							// last word
							if (check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
								// now get the most closest 50 words
								lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2],
										lineWords[i] + " " + lineWords[i + 1], 3);
								updateArrabic(lineWords);
							} else {
								// check 1 Gram if exist get the best
								// conlination
								// between the second
								lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
								updateArrabic(lineWords);
								// now check if the last word is already in the
								// grams no
								// need for further analysis
								if (!check3GramArabic(lineWords[i] + " " + lineWords[i + 1] + " " + lineWords[i + 2])) {
									// if not find the best last word
									lineWords[i + 2] = finalBestArabicWord(lineWords[i + 2],
											lineWords[i] + " " + lineWords[i + 1], 3);
									updateArrabic(lineWords);

								}
							}

						}

					}

				}
				

			} else if (lineWords.length > (i + 1)) {
				// if there is not enogh words to have phares of 4 words
				if (!check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
					// now check 2Gram if it is exist so the muistake is in the
					// last word
					if (check1GramArabic(lineWords[i])) {
						lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
						updateArrabic(lineWords);
					} else {
						// check 1 Gram if exist get the best conlination
						// between the second
						lineWords[i] = finalBestArabicWord(lineWords[i], "", 1);
						updateArrabic(lineWords);
						// now check if the last word is already in the grams no
						// need for further analysis
						if (!check2GramArabic(lineWords[i] + " " + lineWords[i + 1])) {
							// if not find the best last word
							lineWords[i + 1] = finalBestArabicWord(lineWords[i + 1], lineWords[i], 2);
							updateArrabic(lineWords);
						}
					}
				}
			}
			else
			{
				if (!check1GramArabic(lineWords[i]))
				{
					lineWords[i]=finalBestArabicWord(lineWords[i], "", 1);
					updateArrabic(lineWords);
				}
			}

		}
		return lineWords;
	}

	public ArrayList<SimpleType> mostFreqWordsGramEnglish(List<SimpleType> words, String preWord, int gram , String tag) {
		ArrayList<SimpleType> newWords = new ArrayList<>();
		for (int i = 0; i < words.size(); i++) {
			// System.out.println(preWord+" "+words.get(i).word);
			int freq = 0;
			if (gram!=1)
				freq=getEnglishFreq(preWord + " " + words.get(i).word, gram , tag);
			else
				freq=getEnglishFreq(words.get(i).word, gram , tag);
			SimpleType s = new SimpleType();
			s.number = freq;
			s.word = words.get(i).word;
			newWords.add(s);
		}
		Collections.sort(newWords, Collections.reverseOrder(new SimpleType().comparMin()));

		return newWords;
	}
	
	public ArrayList<SimpleType> mostFreqWordsGramArabic(List<SimpleType> words, String preWord, int gram , String originalWord) {
		ArrayList<SimpleType> newWords = new ArrayList<>();
		for (int i = 0; i < words.size(); i++) {
			// System.out.println(preWord+" "+words.get(i).word);
			int freq = 0;
			if (gram!=1)
				freq=getArabicFreq(preWord + " " + words.get(i).word, gram , originalWord);
			else
				freq=getArabicFreq(words.get(i).word, gram ,  originalWord);
			SimpleType s = new SimpleType();
			s.number = freq;
			s.word = words.get(i).word;
			newWords.add(s);
		}
		Collections.sort(newWords, Collections.reverseOrder(new SimpleType().comparMin()));

		return newWords;
	}

	public List<SimpleType> mostClosestEnglishWords(String word) {
		ArrayList<SimpleType> words = new ArrayList<SimpleType>();
		int possibleDiff = maxNumberDiffrent(word);
		for (String key : EnglishCorpus.Gram1Corpus.keySet()) {
			int minDiff = calcMinWordDist(word, key);
			if (minDiff <= possibleDiff) {
				SimpleType s = new SimpleType();
				s.number = minDiff;
				s.word = key;
				words.add(s);
			}
		}

		return words;
	}
	
	public List<SimpleType> mostClosestArabicWords(String word) {
		ArrayList<SimpleType> words = new ArrayList<SimpleType>();
		int possibleDiff = maxNumberDiffrent(word);
		for (String key : ArabicCorpus.Gram1Corpus.keySet()) {
			int minDiff = calcMinWordDistArabic(word, key);
			if (minDiff <= possibleDiff) {
				SimpleType s = new SimpleType();
				s.number = minDiff;
				s.word = key;
				words.add(s);
			}
		}

		return words;
	}


	public int getEnglishFreq(String word, int gram , String tag) {
		if (gram == 1) {
			if (EnglishCorpus.Gram1Corpus.containsKey(word)) {
				if (posTagger.englishTags.containsKey(word)&& posTagger.englishTags.get(word).contains(tag)) {
					return EnglishCorpus.Gram1Corpus.get(word);
				}
				else 
					return 0;
			} else
				return 0;
		}

		else if (gram == 2) {
			if (EnglishCorpus.Gram2Corpus.containsKey(word))
				return EnglishCorpus.Gram2Corpus.get(word);
			else
				return 0;
		} else if (gram == 3) {
			if (EnglishCorpus.Gram3Corpus.containsKey(word))
				return EnglishCorpus.Gram3Corpus.get(word);
			else
				return 0;
		} else if (gram == 4) {
			if (EnglishCorpus.Gram4Corpus.containsKey(word))
				return EnglishCorpus.Gram4Corpus.get(word);
			else
				return 0;
		}
		return 0;
	}
	
	public int getArabicFreq(String word, int gram , String originalWord) {
		if (gram == 1) {
			if (ArabicCorpus.Gram1Corpus.containsKey(word)) {
				String [] segWord = ArabicSegment(word).split("");
				for (int j=0;j<segWord.length;j++)
				{
					if (posTagger.arabicTags.containsKey(segWord[j]))
					{
						if (arabicSegmentTag.get(originalWord).size()-1 < j)
							return 0;
						String tag = arabicSegmentTag.get(originalWord).get(j).Tag;
						if (!posTagger.arabicTags.get(segWord[j]).contains(tag) )
							return 0;
					}
					else 
						return 0;
				}
				 
					return ArabicCorpus.Gram1Corpus.get(word);
				
			} else
				return 0;
		}

		else if (gram == 2) {
			if (ArabicCorpus.Gram2Corpus.containsKey(word))
				return ArabicCorpus.Gram2Corpus.get(word);
			else
				return 0;
		} else if (gram == 3) {
			if (ArabicCorpus.Gram3Corpus.containsKey(word))
				return ArabicCorpus.Gram3Corpus.get(word);
			else
				return 0;
		} else if (gram == 4) {
			if (ArabicCorpus.Gram4Corpus.containsKey(word))
				return ArabicCorpus.Gram4Corpus.get(word);
			else
				return 0;
		}
		return 0;
	}

	public boolean check4GramEnglish(String words) {
		if (EnglishCorpus.Gram4Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check1GramEnglish(String words) {
		if (EnglishCorpus.Gram1Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check3GramEnglish(String words) {
		if (EnglishCorpus.Gram3Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check2GramEnglish(String words) {
		if (EnglishCorpus.Gram2Corpus.containsKey(words))
			return true;
		return false;
	}
	
	public boolean check4GramArabic(String words) {
		if (ArabicCorpus.Gram4Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check1GramArabic(String words) {
		if (ArabicCorpus.Gram1Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check3GramArabic(String words) {
		if (ArabicCorpus.Gram3Corpus.containsKey(words))
			return true;
		return false;
	}

	public boolean check2GramArabic(String words) {
		if (ArabicCorpus.Gram2Corpus.containsKey(words))
			return true;
		return false;
	}

	public String mainLoop(String text) throws IOException {
		ArrayList <SimpleType> specialChar = new ArrayList<>();
		String result = new String();
		// a regular expression for English words
		Pattern pEnglish = Pattern.compile("^[A-Za-z,;'\"\\s]*");
		//Pattern pArabic = Pattern.compile("[أ-ي]*");
		String eachLine [] = text.split("\n");
		for (int j=0;j<eachLine.length;j++) {
			String line = eachLine[j];
			String[] oldLineWords = line.split(" ");
			String newLine = new String();
			for (int i=0;i<oldLineWords.length;i++)
			{
				if (oldLineWords[i].matches(".*[0-9]+.*")|| oldLineWords[i].matches(".*[.|#|?|؟|,|;|\"|\'|!]+.*"))
				{
					SimpleType s = new SimpleType();
					s.word=oldLineWords[i];
					s.number=i;
					specialChar.add(s);
				}
				else
					newLine+=oldLineWords[i]+" ";
			}
			newLine = newLine.substring(0, newLine.length()-1); //to remove the space
			Matcher m = pEnglish.matcher(newLine);
			if (m.matches())
			{
				//english
				newLine=englishNormalizer(newLine);
			}
			else
			{
				newLine= arabicNormalizer(newLine);
			}
			String[] lineWords = newLine.split(" ");
			String[] newWords =  new String [lineWords.length] ;
			if (m.matches())
			{
				ArrayList <SimpleType> taggedSent = englishTagger(newLine);
				newWords=englishLoop(lineWords , taggedSent);
			}
			else
			{
				ArrayList <SimpleType> taggedSent = arabicTagger(newLine);
				newWords=arabicLoop(lineWords , taggedSent);
			}
			
			Collections.sort(specialChar, SimpleType.comparMin());
			int countForSpecialChar =0;
			for (int i = 0; i < newWords.length; i++)
			{
				if (specialChar.size()>0 && specialChar.get(0).number+countForSpecialChar==i)
				{
					result += specialChar.get(0).word+ " ";
					countForSpecialChar++;
					specialChar.remove(0);
				}
				result+=(newWords[i] + " ");
			}
			if (specialChar.size()!=0)
			{
				for (int i=0;i<specialChar.size();i++)
				{
					result+=(specialChar.get(i).word+ " ");
					
				}
			}
			result+=("\n");
		}
		File file = new File("possibleWords.txt");
		FileWriter fileWritter = new FileWriter(file.getName());
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		for (int i = 0; i < bestFreqWords.size(); i++)
			bufferWritter.write(bestFreqWords.get(i).word + " ");
		bufferWritter.close();
		return result;
		
	}
	
	public void mainLoop() throws IOException {
		ArrayList <SimpleType> specialChar = new ArrayList<>();
		FileInputStream f = new FileInputStream("input.txt");
		InputStreamReader r = new InputStreamReader(f);
		BufferedReader reader = new BufferedReader(r);
		// a regular expression for English words
		Pattern pEnglish = Pattern.compile("^[A-Za-z,;'\"\\s]*");
		//Pattern pArabic = Pattern.compile("[أ-ي]*");
		while (reader.ready()) {
			String line = reader.readLine();
			String[] oldLineWords = line.split(" ");
			String newLine = new String();
			for (int i=0;i<oldLineWords.length;i++)
			{
				if (oldLineWords[i].matches(".*[0-9]+.*")|| oldLineWords[i].matches(".*[.|#|?|؟|,|;|\"|\'|!]+.*"))
				{
					SimpleType s = new SimpleType();
					s.word=oldLineWords[i];
					s.number=i;
					specialChar.add(s);
				}
				else
					newLine+=oldLineWords[i]+" ";
			}
			newLine = newLine.substring(0, newLine.length()-1); //to remove the space
			Matcher m = pEnglish.matcher(newLine);
			if (m.matches())
			{
				//english
				newLine=englishNormalizer(newLine);
			}
			else
			{
				newLine= arabicNormalizer(newLine);
			}
			String[] lineWords = newLine.split(" ");
			String[] newWords =  new String [lineWords.length] ;
			if (m.matches())
			{
				ArrayList <SimpleType> taggedSent = englishTagger(newLine);
				newWords=englishLoop(lineWords , taggedSent);
			}
			else
			{
				ArrayList <SimpleType> taggedSent = arabicTagger(newLine);
				newWords=arabicLoop(lineWords , taggedSent);
			}
			File file = new File("output.txt");
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			Collections.sort(specialChar, SimpleType.comparMin());
			int countForSpecialChar =0;
			for (int i = 0; i < newWords.length; i++)
			{
				if (specialChar.size()>0 && specialChar.get(0).number+countForSpecialChar==i)
				{
					bufferWritter.write(specialChar.get(0).word+ " ");
					countForSpecialChar++;
					specialChar.remove(0);
				}
				bufferWritter.write(newWords[i] + " ");
			}
			if (specialChar.size()!=0)
			{
				for (int i=0;i<specialChar.size();i++)
				{
					bufferWritter.write(specialChar.get(i).word+ " ");
					
				}
			}
			bufferWritter.write("\n");
			bufferWritter.close();
		}
		File file = new File("possibleWords.txt");
		FileWriter fileWritter = new FileWriter(file.getName());
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		for (int i = 0; i < bestFreqWords.size(); i++)
			bufferWritter.write(bestFreqWords.get(i).word + " ");
		bufferWritter.close();
	}

	public String englishNormalizer(String Word) {
		Word = Word.toLowerCase();
		return Word;
	}

	public String arabicNormalizer(String Word) {

		Word.replaceAll("أ", "ا");
		Word.replaceAll("إ", "ا");
		Word.replaceAll("آ", "ا");
		Word.replaceAll("أ", "ا");
		Word.replaceAll("ى", "ا"); // it may be ي too so must handle both cases

		Word.replaceAll("ة", "ه");

		return Word;
	}

}
