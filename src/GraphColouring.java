import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
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
				}
			}
			else
			{
				for( Vertex node : workset )
				{
					boolean isConflicting = node.isConflictingIndexed();
					if ( isConflicting )
					{
						node.isConflictingIndexed();
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
			
			long temp = (long)n*((long)n-1l)/2l;
			if ( n < 1 || e < 0 || e > temp || t < 1 )
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
		
		for( int i = 0; i < n; i++ )
		{
			for( int j = 0; j < n; j++ )
			{
				if ( i == j )
					continue;
			}
		}
		
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
					System.out.println("Check");
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
		graph = new Vertex[n];
		
		for( int i = 0; i < n; i++ )
		{
			graph[i] = new Vertex();
		}
		
		if ( (double)e / (double)(n*n) > 1.0/64.0 ) //If the desired graph is dense, must keep track of indices used
		{
			ArrayList<Integer> nums = new ArrayList<Integer>( n );
			for ( int i = 0; i < n; i++ )
				nums.add( i );
	
			ArrayList<Pair<Integer>> pairs = generatePairs( null, nums );
			
			int loc_e = e;
			Random rand = new Random();
			while( loc_e > 0 )
			{
				int nextEdge = rand.nextInt( pairs.size() );

				Pair<Integer> pair = fastRemove( nextEdge, pairs);
				Vertex.createEdge( graph[ pair.first ], graph[ pair.last ] );
				loc_e--;
			}
		}
		else
		{
			int loc_e = e;
			Random rand = new Random();
			while( loc_e > 0 )
			{
				int u = rand.nextInt( n );
				
				int v = ( rand.nextInt( u + n - 1 ) + 1 ) % n ;

				if ( Vertex.createEdge( graph[u], graph[v] ) )
					loc_e--;
			}
		}
	}
	
	public static <T> T fastRemove( int idx, ArrayList<T> array )
	{
		T temp = array.get( idx );
		
		array.set( idx, array.get( array.size() - 1 ) );
		array.remove( array.size() - 1 );
		
		return temp;
	}
	
	private static class Pair<T>
	{
		public T first;
		public T last;
		
		Pair( T _first, T _last )
		{
			first = _first;
			last  = _last;
		}
	}
	
	public static <T> ArrayList<Pair<T>> generatePairs( ArrayList<Pair<T>> set, ArrayList<T> items )
	{
		if ( set == null )
			set = new ArrayList<Pair<T>>( items.size() * items.size() - 1 );
		
		for( int i = 0; i < items.size() - 1; i++ )
		{
			set.add( new Pair<T>( items.get(i), items.get( items.size() - 1 ) ) );
		}
		items.remove( items.size() - 1 );
		
		if( items.size() > 1 )
			return generatePairs( set, items );
		else
			return set;
	}
	
	public static boolean checkColouring( Vertex[] localGraph )
	{
		for ( Vertex node : localGraph )
		{
			if ( node.isConflicting() )
			{
				return false;
			}
		}
		return true;
	}
}
