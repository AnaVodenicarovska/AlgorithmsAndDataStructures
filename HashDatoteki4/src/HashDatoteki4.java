import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
public class HashDatoteki4 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s, ruta,sodrzina,ime,vrednost,staro,komanda;
		String[] pom;
		int N,M,i,j,h;
		s=br.readLine();
		N=Integer.parseInt(s);
		OBHT<String,String> tabela= new OBHT<String,String>(2*N+1);
		for(i=0;i<N;i++) {
			s=br.readLine();
			pom=s.split(" ");
			ruta=pom[0];
			ime=pom[1];
			sodrzina=pom[2];
			h=tabela.search(sodrzina);
			vrednost=ruta+" "+ime;
			if(h==-1) {
				tabela.insert(sodrzina, ";"+vrednost+";");
			}
			else {
				staro=tabela.buckets[h].value;
				staro+=vrednost+";";
				tabela.insert(sodrzina, staro);
			}
		}
		//System.out.println(tabela);
		s=br.readLine();
		M=Integer.parseInt(s);
		for(i=0;i<M;i++) {
			s=br.readLine();
			pom=s.split(" ");
			komanda=pom[0];
			ruta=pom[1];
			ime=pom[2];
			sodrzina=pom[3];
			vrednost=ruta+" "+ime;
			h=tabela.search(sodrzina);
			if(komanda.equals("add")) {
				if(h==-1)
					tabela.insert(sodrzina, ";"+vrednost+";");
				else {
					staro=tabela.buckets[h].value;
					staro+=vrednost+";";
					tabela.insert(sodrzina, vrednost);
				}
			}
			else if(komanda.equals("delete")) {
				if(h!=-1) {
					staro=tabela.buckets[h].value;
					if(staro.contains(";"+vrednost+";"))
						staro=staro.replace(";"+vrednost+";",";");
					//System.out.println(staro);
					if(staro.equals(";"))
							tabela.delete(sodrzina);
					else
						tabela.insert(sodrzina, staro);
				}
			}
			else if(komanda.equals("find")) {
				if(h==-1)
					System.out.println("false");
				else {
					staro=tabela.buckets[h].value;
					if(staro.contains(";"+vrednost+";"))
						System.out.println("true");
					else
						System.out.println("false");
				}
			}
		}
		sodrzina=br.readLine();
		sodrzina="("+sodrzina+")";
		h=tabela.search(sodrzina);
		if(h==-1)
			System.out.println("/");
		else {
			vrednost=tabela.buckets[h].value;
			vrednost=vrednost.substring(1);
			vrednost=vrednost.replace(";"," "+sodrzina+" ");
			System.out.println(vrednost.trim());
		}
		
	}

}
