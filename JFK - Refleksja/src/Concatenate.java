@Description(description = "Wykonuje konkatenacj� ci�g�w")
public class Concatenate implements ICallable {
	@Override
	public String call(String arg1, String arg2) {

		return String.valueOf(arg1 + arg2);
	}
}