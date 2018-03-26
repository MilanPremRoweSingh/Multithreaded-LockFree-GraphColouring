import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphColouring 
{	
	static Vertex[] graph;
	public static Vertex[] conflictSet;
	public static AtomicInteger numConf;
	public static int currID = 0;
	private static int n, e, t;
	
	private static class ColouringThread extends Thread
	{
		ArrayList<Vertex> workset;
		boolean isColouring;
		
		public ColouringThread() 
		{
			this.workset  = new ArrayList<Vertex>();
			isColouring = true;
		}
		
		public ColouringThread( ColouringThread colourerThread ) 
		{
			this.workset = colourerThread.workset;
			isColouring = false;
		}
		
		public void addVertToWorkset( Vertex v )
		{
			workset.add( v );
		}
		
		public void clearWorkset()
		{
			workset.clear();
		}
		
		@Override
		public void run() 
		{
			if ( isColouring )
			{
				for( Vertex node : workset )
				{
					node.colour = node.getLowestViableColour();
					//System.out.println( node.colour ); //DEBUG
				}
			}
			else
			{
				for( Vertex node : workset )
				{
					boolean isConflicting = node.isConflictingIndexed();
					if ( isConflicting )
					{
						conflictSet[ GraphColouring.numConf.getAndIncrement() ] = node;
					}
				}
			}
		}
	}
	
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
		
		generateGraph();
		
		numConf = new AtomicInteger( n );
		
		ColouringThread[] threads = new ColouringThread[t];
		conflictSet = graph;
		
		long time = System.currentTimeMillis();
		while( numConf.get() > 0 ) 
		{	

			for ( int i = 0; i < t; i++ )
			{
				threads[i] = new ColouringThread();
			}
			
			for( int i = 0; i < numConf.get(); i++ ) //Partition conflict set between threads
			{
				threads[i%t].addVertToWorkset( conflictSet[i] );
			}
			
			for ( int i = 0; i < t; i++ )
				threads[i].start();
			
			for ( int i = 0; i < t; i++)
			{
				try 
				{
					threads[i].join();
				} 
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}
			}

			numConf.set( 0 );
			
			for ( int i = 0; i < t; i++ )
			{
				threads[i] = new ColouringThread( threads[i] );
				threads[i].start();
			}
			
			for ( int i = 0; i < t; i++)
			{
				try 
				{
					threads[i].join();
				} 
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}
				threads[i].clearWorkset();
			}
		} 
		
		System.out.println( "Time to colour: " + ( ( System.currentTimeMillis()-time )/1000.0  ));
		
		System.out.println( checkColouring( graph ) );
		
	}
	
	public static void generateGraph()
	{
		long time = System.currentTimeMillis();
		graph = new Vertex[n];
		
		for( int i = 0; i < n; i++ )
		{
			graph[i] = new Vertex();
		}
		
		ArrayList<int[]> possibleEdges = new ArrayList<int[]>( n * (n-1) );
		for( int i = 0; i < n; i++ )
			for( int j = 0; j < n; j++ )
				if( i != j )
					possibleEdges.add( new int[] {i, j} );
		
		int loc_e = e;
		Random rand = new Random();
		while( loc_e > 0 )
		{
			int nextEdge = rand.nextInt( possibleEdges.size() );
			
			int[] verts = fastRemove( nextEdge, possibleEdges );
			Vertex.createEdge( graph[ verts[0] ], graph[ verts[1] ] );
			loc_e--;
		}
		System.out.println( System.currentTimeMillis() - time );
	}
	
	public static int[] fastRemove( int idx, ArrayList<int[]> array )
	{
		int[] temp = array.get( idx );
		
		array.set( idx, array.get( array.size() - 1 ) );
		array.remove( array.size() - 1 );
		
		return temp;
	}
	
	public static boolean checkColouring( Vertex[] localGraph )
	{
		for ( Vertex node : localGraph )
		{
			if ( node.isConflicting() )
				return false;
		}
		return true;
	}
}
