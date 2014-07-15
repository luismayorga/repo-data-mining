(defproject nose "0.1.0-SNAPSHOT"
  :description "App to detect the evolution of bad smells over several versions of a Java SVN repository"
  :url "https://github.com/rapsioux/nose"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.4"]
                 [org.xerial/sqlite-jdbc "3.7.15-M1"]
                 [org.tmatesoft.svnkit/svnkit "1.8.3-1"]
                 [org.clojure/data.zip "0.1.1"]
                 [commons-io/commons-io "2.4"]
                 [com.mchange/c3p0 "0.9.2.1"]]
  :main ^:skip-aot nose.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
