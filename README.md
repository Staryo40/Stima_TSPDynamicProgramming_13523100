# Travelling Salesperson Problem (TSP) Solver using Dynamic Programming with Scala
This program solves the Travelling Salesperson Problem (TSP) using dynamic programming. Given an input file (.txt) containing the number of nodes and their pairwise distances, the program computes the shortest possible route that visits each city exactly once and returns to the starting city. Implemented in Scala as part of the IF2211 Strategi Algoritma course assignment.

<p align="center">
<img src="https://github.com/user-attachments/assets/0c6bfa35-742b-4b62-8fbc-d05134305090" alt="Rush Hour Solver Program Interface" width="700"/>
</p>

## Program Requirements
1. Java JDK 17
2. Scala CLI (recommended) or sbt, download <a href="https://www.scala-lang.org/download/">here</a>
## Compiling the Program
Using Scala CLI, no compilation is needed, it compiles and runs the program in one command.  
## Running the Program
Run the line below in the root directory to compile and run the program
```bash
scala TSPSolver.scala -- <input_file> <node_start>
```
There are two parameters to pass, input_file is the .txt file that has the distance matrix, while node_start is the node to start with for printing the path of the solution (range 1 to n, n being the node count of the graph).  
Example:
```bash
scala TSPSolver.scala -- data/kuliah.txt 1
```
## About The Creators
<table>
  <tr>
    <th>Nama Lengkap</th>
    <th>NIM</th>
    <th>Kelas</th>
  </tr>
  <tr>
    <td>Aryo Wisanggeni</td>
    <td>13523100</td>
    <th>K02</th>
  </tr>
</table>