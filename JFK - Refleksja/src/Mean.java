@Description(description = "Metoda zwraca �redni� liczb")
public class Mean implements ICallable
{
	@Override
	public String call(String arg1, String arg2)
	{
		return String.valueOf((Float.parseFloat(arg1)+Float.parseFloat(arg2))/2);
	}
}