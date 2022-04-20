\ENCODING=ISO-8859-1
\Problem name: ilog.cplex

Minimize
 obj1: x0 + x1 + x2 + x3 + x4
Subject To
 c1:     x0 + x1 + x2 + x3 + x4 >= 2
 v0:     0.5 x1 + 0.5 x4 - y1,0 <= 0
 v1:     0.5 x0 + 0.5 x2 - y1,1 <= 0
 v2:     0.5 x1 + 0.5 x3 - y1,2 <= 0
 v3:     0.5 x2 + 0.5 x4 - y1,3 <= 0
 v4:     0.5 x0 + 0.5 x3 - y1,4 <= 0
 s_hull: y2,0 + y2,1 + y2,2 + y2,3 + y2,4  = 5
Bounds
 0 <= x0 <= 1
 0 <= x1 <= 1
 0 <= x2 <= 1
 0 <= x3 <= 1
 0 <= x4 <= 1
 0 <= y1,0 <= 1
 0 <= y1,1 <= 1
 0 <= y1,2 <= 1
 0 <= y1,3 <= 1
 0 <= y1,4 <= 1
 0 <= y2,0 <= 1
 0 <= y2,1 <= 1
 0 <= y2,2 <= 1
 0 <= y2,3 <= 1
 0 <= y2,4 <= 1
Binaries
 x0  x1  x2  x3  x4 
Generals
 y1,0  y1,1  y1,2  y1,3  y1,4  y2,0  y2,1  y2,2  y2,3  y2,4 
End
