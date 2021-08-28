import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

class MapEntry<K extends Comparable<K>, E> implements Comparable<K> {

	// Each MapEntry object is a pair consisting of a key (a Comparable
	// object) and a value (an arbitrary object).
	K key;
	E value;

	public MapEntry(K key, E val) {
		this.key = key;
		this.value = val;
	}

	public int compareTo(K that) {
		// Compare this map entry to that map entry.
		@SuppressWarnings("unchecked")
		MapEntry<K, E> other = (MapEntry<K, E>) that;
		return this.key.compareTo(other.key);
	}

	public String toString() {
		return "<" + key + "," + value + ">";
	}
}

class OBHT<K extends Comparable<K>, E> {

	// An object of class OBHT is an open-bucket hash table, containing entries
	// of class MapEntry.
	MapEntry<K, E>[] buckets;

	// buckets[b] is null if bucket b has never been occupied.
	// buckets[b] is former if bucket b is formerly-occupied
	// by an entry that has since been deleted (and not yet replaced).

	static final int NONE = -1; // ... distinct from any bucket index.

	private static final MapEntry former = new MapEntry(null, null);
	// This guarantees that, for any genuine entry e,
	// e.key.equals(former.key) returns false.

	private int occupancy = 0;
	// ... number of occupied or formerly-occupied buckets in this OBHT.

	@SuppressWarnings("unchecked")
	public OBHT(int m) {
		// Construct an empty OBHT with m buckets.
		buckets = (MapEntry<K, E>[]) new MapEntry[m];
	}

	private int hash(K key) {
		// Translate key to an index of the array buckets.
		return Math.abs(key.hashCode()) % buckets.length;
	}

	public int search(K targetKey) {
		// Find which if any bucket of this OBHT is occupied by an entry whose key
		// is equal to targetKey. Return the index of that bucket.
		int b = hash(targetKey);
		int n_search = 0;
		for (;;) {
			MapEntry<K, E> oldEntry = buckets[b];
			if (oldEntry == null)
				return NONE;
			else if (targetKey.equals(oldEntry.key))
				return b;
			else {
				b = (b + 1) % buckets.length;
				n_search++;
				if (n_search == buckets.length)
					return NONE;

			}
		}
	}

	public void insert(K key, E val) {
		// Insert the entry <key, val> into this OBHT.
		MapEntry<K, E> newEntry = new MapEntry<K, E>(key, val);
		int b = hash(key);
		int n_search = 0;
		for (;;) {
			MapEntry<K, E> oldEntry = buckets[b];
			if (oldEntry == null) {
				if (++occupancy == buckets.length) {
					System.out.println("Hash tabelata e polna!!!");
				}
				buckets[b] = newEntry;
				return;
			} else if (oldEntry == former || key.equals(oldEntry.key)) {
				buckets[b] = newEntry;
				return;
			} else {
				b = (b + 1) % buckets.length;
				n_search++;
				if (n_search == buckets.length)
					return;

			}
		}
	}

	@SuppressWarnings("unchecked")
	public void delete(K key) {
		// Delete the entry (if any) whose key is equal to key from this OBHT.
		int b = hash(key);
		int n_search = 0;
		for (;;) {
			MapEntry<K, E> oldEntry = buckets[b];

			if (oldEntry == null)
				return;
			else if (key.equals(oldEntry.key)) {
				buckets[b] = former;// (MapEntry<K,E>)former;
				return;
			} else {
				b = (b + 1) % buckets.length;
				n_search++;
				if (n_search == buckets.length)
					return;

			}
		}
	}

	public String toString() {
		String temp = "";
		for (int i = 0; i < buckets.length; i++) {
			temp += i + ":";
			if (buckets[i] == null)
				temp += "\n";
			else if (buckets[i] == former)
				temp += "former\n";
			else
				temp += buckets[i] + "\n";
		}
		return temp;
	}

	public OBHT<K, E> clone() {
		OBHT<K, E> copy = new OBHT<K, E>(buckets.length);
		for (int i = 0; i < buckets.length; i++) {
			MapEntry<K, E> e = buckets[i];
			if (e != null && e != former)
				copy.buckets[i] = new MapEntry<K, E>(e.key, e.value);
			else
				copy.buckets[i] = e;
		}
		return copy;
	}
}

class Temperatura {
	String interval1, interval2;
	float temperatura;

	public Temperatura(String interval1, String interval2, float temperatura) {
		super();
		this.interval1 = interval1;
		this.interval2 = interval2;
		this.temperatura = temperatura;
	}
}

public class HashJuni2016 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s, grad, interv, interv2;
		int i, N, h;
		float temp, maxtemp = 0;
		String[] pom;
		s = br.readLine();
		N = Integer.parseInt(s);
		OBHT<String, Temperatura> tabela = new OBHT<String, Temperatura>(2 * N + 1);
		Temperatura t, staro;
		for (i = 0; i < N; i++) {
			s = br.readLine();
			pom = s.split(" ");
			grad = pom[0];
			interv = pom[1];
			interv2 = pom[2];
			temp = Float.parseFloat(pom[3]);
			h = tabela.search(grad);
			if (h == -1) {
				t = new Temperatura(interv, interv2, temp);
				tabela.insert(grad, t);
			} else {
				staro = tabela.buckets[h].value;
				if (temp > staro.temperatura) {
					t = new Temperatura(interv, interv2, temp);
					tabela.insert(grad, t);
				}

			}
		}
		grad = br.readLine();
		h = tabela.search(grad);
		if (h == -1) {
			System.out.println("Nema temperatura za dadeniot grad");
		} else {
			t = tabela.buckets[h].value;
			System.out.println(grad + " " + t.interval1 + "-" + t.interval2 + " " + t.temperatura);
		}
	}
}
