import java.util.ArrayList;
import java.util.Random;

public class GraphColouring 
{	
	static GraphNode[] graph;
	public static int currID = 0;
	private static int n, e, t;
	
	public static void main( String[] args )
	{
		
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
		
		ArrayList<int[]> possibleEdges = new ArrayList<int[]>( n * (n-1) );
		for( int i = 0; i < n; i++ )
			for( int j = 0; j < n; j++ )
				if( i != j )
					possibleEdges.add( new int[] {i, j} );
		
		int loc_e = e;
		while( loc_e > 0 )
		{
			Random rand = new Random();
			int nextEdge = rand.nextInt( possibleEdges.size() );
			
			int[] verts = possibleEdges.remove( nextEdge );
			GraphNode.createEdge( graph[ verts[0] ], graph[ verts[1] ] );
			loc_e--;
		}

		for( GraphNode node : graph )
		{
			node.colour = node.getLowestViableColour();
			System.out.println( node.colour );
		}
		System.out.println("Done");	
	}
	
	
	
	
}
