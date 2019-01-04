import java.util.Random;

@Description(description = "Generuje liczbê ca³kowit¹ z przedzia³u wyznaczonego argumentami 1 i 2")
public class Generator implements ICallable {
	@Override
	public String call(String arg1, String arg2) {

		Random rand = new Random();
		return String.valueOf(rand.nextInt((int)Math.floor(Float.parseFloat(arg2)) - (int)Math.ceil(Float.parseFloat(arg1)) + 1) + (int)Math.ceil(Float.parseFloat(arg1)));
	}
}