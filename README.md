# SNN
Shared Nearest Neighbors (SNN) clustering algorithm implementation in Java.

This is a Java implementation of the clustering algorithm described in:

Ertöz, L., Steinbach, M., & Kumar, V. (2003). 
Finding clusters of different sizes, shapes, and densities in noisy, high dimensional data. SDM. 
Retrieved from http://epubs.siam.org/doi/abs/10.1137/1.9781611972733.5

The code can easily be accessed via MATLAB, such as in the following example:

```matlab
javaaddpath('C:\Users\Cássio\Dropbox\workspace\snncluster\target\snncluster-0.0.1-SNAPSHOT.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/javaml-0.1.7.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/lib/ajt-2.9.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/lib/commons-math-1.2.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/lib/Jama-1.0.2.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/lib/libsvm.jar');
javaaddpath('C:/Users/Cássio/tools/javaml-0.1.7/lib/weka.jar');
javaaddpath('C:/Users/Cássio/.m2/repository/org/la4j/la4j/0.5.5/la4j-0.5.5.jar');

% javaaddpath('C:\Users\Cássio\.m2\repository\nz\ac\waikato\cms\weka\weka-dev\3.7.12\weka-dev-3.7.12.jar');
% javaaddpath('C:\Users\Cássio\.m2\repository\org\jfree\jfreechart\1.0.19\jfreechart-1.0.19-javadoc.jar');
% javaaddpath('C:\Users\Cássio\.m2\repository\br\fapesp\myutils\myutils\1.0-SNAPSHOT\myutils-1.0-SNAPSHOT.jar');

% rng default;
% Y1 = normrnd(0,0.5,30,2);
% Y2 = normrnd(10,0.5,30,2);
% Y3 = [Y1 ; Y2];
% N = [unifrnd(min(Y3(:,1)), max(Y3(:,1)), 30, 1) unifrnd(min(Y3(:,2)), max(Y3(:,2)), 30, 1)];
% Y = [Y3 ; N];
% Y = Y3;
% scatter(Y(:,1), Y(:,2));
% lbls = br.fapesp.snn.snncluster.SNN.snn(Y, 10, 5, 7);

gscatter(Y(:,1), Y(:,2), lbls);

```
