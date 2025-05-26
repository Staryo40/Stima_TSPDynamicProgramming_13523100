import scala.math._
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

        println(s"Minimum cost: $minCost")
        println(s"Path: ${path.mkString(" -> ")}")
    }
}  

class PathReconstruction(dist: Array[Array[Int]], start: Int) {
    val n = dist.length
    val start0 = start - 1
    require(start >= 1 && start <= n, s"Start node must be in range 1 to $n.")

    val INF = Int.MaxValue / 2
    val dp = Array.fill(1 << n, n)(INF)
    val prev = Array.fill(1 << n, n)(-1)

    def solve(): (Int, List[Int]) = {
        // Base case: from start0 to all others
        for (i <- 0 until n if i != start0) {
            val mask = 1 << start0
            dp(mask | (1 << i))(i) = dist(start0)(i)
            prev(mask | (1 << i))(i) = start0
        }

        for (mask <- 0 until (1 << n)) {
            for (u <- 0 until n if (mask & (1 << u)) != 0) {
                val prevMask = mask ^ (1 << u)
                for (v <- 0 until n if (v != u && (mask & (1 << v)) != 0)) {
                val newCost = dp(prevMask)(v) + dist(v)(u)
                if (newCost < dp(mask)(u)) {
                    dp(mask)(u) = newCost
                    prev(mask)(u) = v
                }
                }
            }
        }

        val fullMask = (1 << n) - 1
        val (minCost, lastNode) = (0 until n)
            .filter(_ != start0)
            .map(i => (dp(fullMask)(i) + dist(i)(start0), i))
            .minBy(_._1)

        val path = reconstructPath(fullMask, lastNode)
        (minCost, ((start0 :: path) :+ start0).map(_ + 1)) // +1 to convert to 1-based
    }

    def reconstructPath(mask: Int, last: Int): List[Int] = {
        if (mask == ((1 << start0) | (1 << last))) List(last)
        else {
            val prevNode = prev(mask)(last)
            reconstructPath(mask ^ (1 << last), prevNode) :+ last
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