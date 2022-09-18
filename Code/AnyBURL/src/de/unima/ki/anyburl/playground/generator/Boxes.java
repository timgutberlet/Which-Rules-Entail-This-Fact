package de.unima.ki.anyburl.playground.generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;

public class Boxes {
	
	public static void main(String[] args) throws IOException {
		
		Settings.READ_CYCLIC_RULES = 0;
		Settings.READ_ACYCLIC1_RULES = 1;
		Settings.READ_ACYCLIC2_RULES = 0;
		Settings.READ_ZERO_RULES = 0;
		
		RuleReader rr = new RuleReader();
		LinkedList<Rule> rules = rr.read("exp/patrick/transformer/boxes/rules-10");
		

		for (Rule r : rules) {
			System.out.println(r);
		}
		System.exit(0);
		
		int n = 1000;
		
		
		StringBuilder sb = new StringBuilder();
		
		
		for (int i = 0; i < n; i++) {
			String size = "";
			String color = "";
			Random rand = new Random();
			double r1 = rand.nextDouble();
			double r2 = rand.nextDouble();
			double r3 = rand.nextDouble();
			// size
			if (r1 <= 0.333) {
				size = "small";
			}
			else {
				if  (r1 <= 0.666) size = "medium";
				else size = "large";
			}
			// color
			if (r2 <= 0.333) {
				color = "white";
			}
			else {
				if  (r2 <= 0.666) color = "red";
				else color = "black";
			}
			
			
			System.out.println("b" + i + "\t" + "size" + "\t" + size);
			System.out.println("b" + i + "\t" + "color" + "\t" + color);
			

			if (size.equals("large") && color.equals("black")) {
				if (r3 <= 0.7) {
					System.out.println("b" + i + "\t" + "contains" + "\t" + "gold");
				}
				else {
					sb.append("b" + i + "\t" + "contains" + "\t" + "gold");
					sb.append("\n");
					
				}
			}
			else {
				if (r3 <= 0.7) {
					System.out.println("b" + i + "\t" + "contains" + "\t" + "nothing");
				}
				else {
					sb.append("b" + i + "\t" + "contains" + "\t" + "nothing");
					sb.append("\n");
					
				}
			}

		}
		System.out.println("---");
		System.out.println(sb);
	}
}
