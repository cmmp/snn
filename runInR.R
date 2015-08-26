Sys.setenv(JAVA_HOME="") # have to do this for rJava...

require(rJava)

.jinit()
.jaddClassPath("C:/Users/Cássio/Dropbox/workspace/snncluster/target/snncluster-0.0.1-SNAPSHOT-jar-with-dependencies.jar")

# .jclassPath() # show classpath

set.seed(1234)

Y1 = matrix(rnorm(30*2, mean = 0, sd = 1), 30, 2)
Y2 = matrix(rnorm(30*2, mean = 10, sd = 1), 30, 2)

Y3 = rbind(Y1, Y2)

snn = J("br.fapesp.snn.snncluster.SNN")

# snn(X, K, eps, minpts)
lbls = snn$snn(.jarray(Y3, dispatch = TRUE), as.integer(15), as.double(5), as.integer(10))

plot(Y3, col = as.factor(lbls))
legend('bottomright', legend = unique(lbls), col = 1:length(lbls), pch = 1)

