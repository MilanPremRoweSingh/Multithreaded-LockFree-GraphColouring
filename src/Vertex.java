import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class Vertex 
{
	protected ArrayList<Vertex> adjNodes = new ArrayList<Vertex>();
	public volatile int colour = 0;
	public volatile int id = GraphColouring.currID++;
	
	static public boolean createEdge( Vertex u, Vertex v )
	{
		boolean uvAreAdj = false;
		for ( Vertex v0 : u.adjNodes )
		{
			uvAreAdj = ( v0 == v );
			if ( uvAreAdj )
				break;
		}
		
		if ( !uvAreAdj )
		{
			u.adjNodes.add( v );
			v.adjNodes.add( u );
			return true;
		}
		else
			return false;
	}
	
	public int getLowestViableColour()
	{
		ArrayList<Integer> usedColours = new ArrayList<Integer>();
		for ( Vertex v : adjNodes )
		{
			Integer colour =  v.colour;
			if( !usedColours.contains( colour ) && colour.intValue() != 0 )
			{
				usedColours.add( colour );
			}
		}
		usedColours.sort( new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
			
		});
		
		int colour = 1;
		for( int i = 0; i < usedColours.size(); i++ )
		{
			if( ( usedColours.get( i ).intValue() == colour ) )
			{
				colour++;
				continue;
			}
			else
			{
				return colour;			
			}
			
		}
		
		return colour;
	}
	
	public boolean isConflictingIndexed()
	{
		for( Vertex node : adjNodes )
		{
			if( node.colour == this.colour && node.id < this.id )
				return true;
		}
		return false;
	}
	
	public boolean isConflicting()
	{
		for( Vertex node : adjNodes )
		{
			if( node.colour == this.colour )
				return true;
		}
		return false;
	}
	
	
	
}
