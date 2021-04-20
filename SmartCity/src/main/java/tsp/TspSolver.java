package tsp;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
This is just a test for Tsp
It can be used as follows:
    	AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(Start,List,Finish);
    	adjacencyMatrix.calculateAdjacencyMatrix();
    	Tsp newTsp = new Tsp();
    	newTsp.tsp(adjacencyMatrix.getMatrix());
*/
/*public class TspSolver
{
    public static void main(String... arg)
    {
        
    	
    	int number_of_nodes;
        Scanner scanner = null;
        try
        {
            System.out.println("Enter the number of nodes in the graph");
            scanner = new Scanner(System.in);
            number_of_nodes = scanner.nextInt();
            int adjacency_matrix[][] = new int[number_of_nodes + 1][number_of_nodes + 1];
            System.out.println("Enter the adjacency matrix");
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    adjacency_matrix[i][j] = scanner.nextInt();
                }
            }
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    if (adjacency_matrix[i][j] == 1 && adjacency_matrix[j][i] == 0)
                    {
                        adjacency_matrix[j][i] = 1;
                    }
                }
            }
            System.out.println("the citys are visited as follows");
            Tsp tspNearestNeighbour = new Tsp();
            tspNearestNeighbour.tsp(adjacency_matrix);
        } catch (InputMismatchException inputMismatch)
         {
             System.out.println("Wrong Input format");
         }
        scanner.close();
    }
}
*/
