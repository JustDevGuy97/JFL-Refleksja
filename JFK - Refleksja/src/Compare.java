@Description(description = "Metoda porównuje dwie liczby")
public class Compare implements ICallable
{
	@Override
	public String call(String arg1, String arg2)
	{
		return String.valueOf(Float.parseFloat(arg1)==Float.parseFloat(arg2));
	}
}