package p3;

import p1.A;
import p2.B;

class C {
	{
		getB().m1A(this.getA());
	}

	A getA() {
		return null;
	}

	B getB() {
		return null;
	}
}