package quiz.server.dao;

public class Pair<A, B> {
	public A first;
	public B second;
	
	public Pair(A a, B b) {
		first = a;
		second = b;
	}
	
	public static <A, B> Pair<A, B> make(A a, B b) {
		return new Pair<A, B>(a, b);
	}
	
	@Override
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}
}
