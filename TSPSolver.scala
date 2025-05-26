import scala.math._
import scala.collection.mutable
import scala.io.Source
import scala.util.Try

object TSPSolver {
    def main(args: Array[String]): Unit = {
        if (args.length != 2) {
            println("Usage: scala src/*.scala <input_file> <start_node>")
            sys.exit(1)
        }

        val dist = InputReader.readDistanceMatrix(args(0))
        val startNode = try {
            args(1).toInt
        } catch {
            case _: NumberFormatException =>
            println("Error: start_node must be a valid integer.")
            sys.exit(1)
        }

        if (startNode < 1 || startNode > dist.length) {
            println(s"Error: start_node must be in the range 1 to ${dist.length}.")
            sys.exit(1)
        }

        val solver = new PathReconstruction(dist, startNode)
        val (minCost, path) = solver.solve()

        println("Distance matrix:")
        dist.foreach(row => println(row.mkString(" ")))
        println("")
        println(s"Minimum cost: $minCost")
        println(s"Path: ${path.mkString(" -> ")}")
    }
}  

class PathReconstruction(dist: Array[Array[Int]], start: Int) {
    val n = dist.length
    val start0 = start - 1
    require(start >= 1 && start <= n, s"Start node must be in range 1 to $n.")

    val INF = Int.MaxValue / 2
    val dp = mutable.Map[(Set[Int], Int), Int]()
    val prev = mutable.Map[(Set[Int], Int), Int]()

    def solve(): (Int, List[Int]) = {
        val allNodes = (0 until n).toSet

        // Base case: from start0 to i
        for (i <- 0 until n if i != start0) { // Loop over all nodes except start0
            val visited = Set(start0, i)
            dp((visited, i)) = dist(start0)(i)
            prev((visited, i)) = start0
        }

        // Iterate over increasing sizes of visited sets
        for (size <- 3 to n) { // Start from 3, because size 2 was covered in base case
            for {
                visited <- allNodes.subsets(size) if visited.contains(start0)
                current <- visited if current != start0
            } {
                val previousVisited = visited - current
                val (cost, pred) = previousVisited // Get minimum
                    .filter(_ != current)
                    .map(prevNode => (
                        dp.getOrElse((previousVisited, prevNode), INF) + dist(prevNode)(current),
                        prevNode
                    ))
                    .minBy(_._1)

                dp((visited, current)) = cost
                prev((visited, current)) = pred
            }
        }

        val fullVisited = allNodes
        val (minCost, lastNode) = (allNodes - start0)
            .map(i => (dp((fullVisited, i)) + dist(i)(start0), i))
            .minBy(_._1)

        val path = reconstructPath(fullVisited, lastNode)
        (minCost, ((start0 :: path) :+ start0).map(_ + 1)) // RETURN
    }

    def reconstructPath(visited: Set[Int], last: Int): List[Int] = {
        if (visited == Set(start0, last)) List(last)
        else {
            val pred = prev((visited, last))
            reconstructPath(visited - last, pred) :+ last
        }
    }
}

object InputReader {
  def readDistanceMatrix(filename: String): Array[Array[Int]] = {
    val lines = Source.fromFile(filename).getLines().toList

    if (lines.isEmpty) {
      throw new IllegalArgumentException("Error: Input file is empty.")
    }

    // Parsing node count
    val n = Try(lines.head.trim.toInt).getOrElse {
      throw new IllegalArgumentException("Error: First line must be a valid integer (number of nodes).")
    }

    if (n <= 0) {
      throw new IllegalArgumentException("Error: Number of nodes is not positive.")
    }

    val matrixLines = lines.tail.take(n)

    if (matrixLines.length != n) {
      throw new IllegalArgumentException(s"Error: Expected $n rows of matrix, but found ${matrixLines.length}.")
    }

    val matrix = matrixLines.zipWithIndex.map { case (line, rowIndex) =>
      val row = line.trim.split("\\s+").map(_.toIntOption)
      if (row.length != n) {
        throw new IllegalArgumentException(s"Error: Row ${rowIndex + 1} does not have $n columns.")
      }
      row.map(_.getOrElse {
        throw new IllegalArgumentException(s"Error: Non-integer value found in row ${rowIndex + 1}.")
      })
    }

    matrix.toArray
  }

// FOR PRINTING READ RESULT
//   def main(args: Array[String]): Unit = {
//     if (args.length != 1) {
//       println("Usage: scala InputReader.scala <input_file>")
//       sys.exit(1)
//     }

//     val filename = args(0)
//     val matrix = readDistanceMatrix(filename)

//     println("Distance matrix:")
//     matrix.foreach(row => println(row.mkString(" ")))
//   }
}