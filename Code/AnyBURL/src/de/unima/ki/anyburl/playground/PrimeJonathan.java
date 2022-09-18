package de.unima.ki.anyburl.playground;

public class PrimeJonathan {

	public static void main(String[] args) {
		
		
		System.out.println("Hallo Jonathan!");
		System.out.println("Gleich gehts los:");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 1; i < 400000000; i++) {
			if (isPrime(i)) System.out.println(i);
		}

	}
	
	
	public static boolean isPrime(int p) {
		for (int j = 2; j <= (p/2); j++) {
			if (p % j == 0) {
				return false;
			}
		}
		return true;
		
		
	}

}
