
public class GraphColouring 
{	
	static void main( String[] args )
	{
		int n, e, t;
		
		try
		{
			n = Integer.parseInt( args[0] );
			e = Integer.parseInt( args[1] );
			t = Integer.parseInt( args[2] );
			
			if ( n < 1 || e < 0 || e > n*(n-1) || t < 1 )
			{
				System.out.println( "Invalid arguments" );
				return;
			}
		}
		catch ( NumberFormatException exc )
		{
			System.out.println( "Invalid arguments" );
			return;
		}
		catch ( IndexOutOfBoundsException exc )
		{
			System.out.println( "Invalid arguments" );
			return;
		}
	}
	
}
