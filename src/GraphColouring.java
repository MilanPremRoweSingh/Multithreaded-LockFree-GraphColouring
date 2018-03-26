import java.util.Random;

public class GraphColouring 
{	
	static GraphNode[] graph;
	
	public static void main( String[] args )
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
		
		graph = new GraphNode[n];
		
		for( int i = 0; i < n; i++ )
		{
			graph[i] = new GraphNode();
		}
		
		for ( int i = 0; i < e; i++ )
		{
			boolean edgeCreated = false;
					
			while( !edgeCreated )
			{
				Random rand = new Random();
				
				int u = rand.nextInt( n );
				int v = rand.nextInt( n );
				
				edgeCreated = GraphNode.createEdge( graph[u], graph[v] );
			}
		}
		graph[0].getLowestViableColour();
		
	}
	
}
